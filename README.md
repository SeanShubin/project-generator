# Project Generator

Two tools for working with Maven multi-module Kotlin projects:

1. **Project Generator** — create a complete project from a JSON specification, eliminating manual pom.xml authoring
2. **Project Replicator** — clone an existing project into a new namespace, transforming all package names and references

## Features

### Project Generation
- Generate complete multi-module Maven projects from JSON
- Declare dependencies once, apply globally or per-module
- Automatic module dependency management
- Entry point configuration with jar packaging
- Source dependency copying from other projects
- Publishing configuration (Maven Central)

### Project Replication
- Clone an entire project tree into a new package prefix
- Transform all source file package declarations and imports automatically
- Copy binary files unchanged; optionally mark paths as verbatim
- Update `project-specification.json` in the clone with the new prefix

## Prerequisites
- Java 25+
- Maven 3.x
- Kotlin 2.3.0+

## Building
```bash
mvn clean package
```

## Project Generation

### Running
```bash
java -jar console/target/project-generator-console.jar [specification-file]
```

If no argument is provided, uses `project-specification.json` from the current directory.

### Specification Format
See [docs/sample-specification.json](docs/sample-specification.json) for a complete example.

Minimal specification:
```json
{
  "prefix": ["com", "example"],
  "name": ["my", "project"],
  "description": "My Project",
  "version": "1.0.0",
  "language": "kotlin",
  "developer": {
    "name": "Developer Name",
    "githubName": "username",
    "mavenUserName": "username",
    "organization": "Organization",
    "url": "https://example.com/"
  },
  "global": ["stdlib", "test"],
  "dependencies": {
    "stdlib": {
      "group": "org.jetbrains.kotlin",
      "artifact": "kotlin-stdlib"
    },
    "test": {
      "group": "org.jetbrains.kotlin",
      "artifact": "kotlin-test-junit",
      "scope": "test"
    },
    "domain": {
      "scope": "test"
    }
  },
  "modules": {
    "console": [],
    "domain": [],
    "integration-test": ["domain"]
  },
  "entryPoints": {
    "console": "com.example.my.project.console.Main"
  },
  "javaVersion": "25"
}
```

In this example, `integration-test` depends on `domain` at test scope because the `domain` entry in dependencies specifies `"scope": "test"`. The module can reference other modules either directly (for compile scope) or through the dependencies section (to specify a different scope).

### What Gets Generated
- Multi-module Maven project structure
- Parent and module pom.xml files
- Assembly configuration for entry points
- Publishing configuration
- Source file structure

### Specification Fields
- **prefix**: Package prefix (e.g., `["com", "example"]`)
- **name**: Project name parts (e.g., `["my", "project"]`)
- **modules**: Map of module names to their dependencies (other module names or dependency names)
- **dependencies**: Named dependencies, can be either:
  - **External** (Maven artifacts): Must have `group` and `artifact` fields, optionally `scope`
  - **Internal** (local modules): Only have `scope` field (key is the module name)
- **global**: Dependencies applied to all modules (must be external)
- **entryPoints**: Module names mapped to main class paths
- **sourceDependencies**: Optional - copy and transform source code from other local projects (array)
  - Each entry contains:
    - `sourceProjectPath`: Path to external project (relative or absolute)
    - `moduleMapping`: Map of source module names to target module names
  - Source code is copied and package names are transformed to match target project structure
  - **Important**: Only modules listed in the source project's `exports` can be imported
- **exports**: Optional - list of module names that other projects can import via source dependencies
  - Modules must be explicitly listed here to be importable by other projects
  - Use for utility libraries and reusable components
  - Leave empty for leaf applications
- **versionOverrides**: Optional - override dependency versions

### Internal Dependencies with Scope

By default, when modules depend on other modules, they use compile scope. To specify a different scope (like test), declare the module in the `dependencies` section:

```json
{
  "dependencies": {
    "stdlib": {
      "group": "org.jetbrains.kotlin",
      "artifact": "kotlin-stdlib"
    },
    "test": {
      "group": "org.jetbrains.kotlin",
      "artifact": "kotlin-test-junit",
      "scope": "test"
    },
    "data-layer": {
      "scope": "test"
    }
  },
  "modules": {
    "domain": [],
    "data-layer": ["domain"],
    "integration-tests": ["domain", "data-layer"]
  }
}
```

In this example:
- `integration-tests` depends on `domain` at **compile scope** (referenced directly)
- `integration-tests` depends on `data-layer` at **test scope** (declared in dependencies with scope)
- External dependencies work the same way - specify `group`/`artifact` for Maven artifacts
- Internal dependencies only need `scope` - the key is the module name

**Key Points:**
- Module dependencies without a `scope` declaration default to compile scope
- To use test scope, add the module to the `dependencies` section with only a `scope` field
- The `modules` section stays simple - just lists dependencies by name
- Scope information is centralized in the `dependencies` section

### Source Dependencies Example

Copy modules from multiple local projects:
```json
{
  "sourceDependencies": [
    {
      "sourceProjectPath": "../kotlin-reusable",
      "moduleMapping": {
        "dynamic-core": "dynamic-core",
        "dynamic-json": "dynamic-json",
        "di-contract": "di-contract"
      }
    },
    {
      "sourceProjectPath": "../jvmspec",
      "moduleMapping": {
        "classfile": "classfile-core"
      }
    }
  ]
}
```

This copies the specified modules from each source project and transforms their package declarations to match your project's prefix and name.

### Exports Example

**Utility Library** (exports modules for others to use):
```json
{
  "name": ["kotlin", "reusable"],
  "modules": {
    "di-contract": [],
    "di-delegate": ["di-contract"],
    "dynamic-core": [],
    "dynamic-json": ["dynamic-core", "di-delegate"],
    "internal-utils": []
  },
  "exports": [
    "di-contract",
    "di-delegate",
    "dynamic-core",
    "dynamic-json"
  ]
}
```
**Note**: `internal-utils` is NOT exported - only for internal use.

**Application** (imports from utility library):
```json
{
  "name": ["my", "app"],
  "modules": {
    "domain": ["di-contract"],
    "console": ["domain"]
  },
  "sourceDependencies": [
    {
      "sourceProjectPath": "../kotlin-reusable",
      "moduleMapping": {
        "di-contract": "di-contract",
        "di-delegate": "di-delegate"
      }
    }
  ],
  "exports": []
}
```
**Note**: Leaf applications typically don't export anything.

If the application tries to import `internal-utils`, the generator will fail with:
```
Cannot import non-exported modules from source project: kotlin.reusable

Non-exported modules: internal-utils
Available exports: di-contract, di-delegate, dynamic-core, dynamic-json

Resolution:
  1. Remove these modules from your moduleMapping, OR
  2. Add them to the "exports" section in kotlin.reusable's project-specification.json
```

## Project Replication

### Running
```bash
java -jar replicator/target/project-generator-replicator.jar [replication-spec-file] [destination-directory]
```

If no arguments are provided, uses `replication-spec.json` from the current directory and writes output to `.`.

### What Replication Does
- Walks the source project tree (respecting `.gitignore`)
- Transforms package declarations and imports in Kotlin/Java source files
- Performs text replacement of old package paths in config files
- Copies binary files as-is
- Updates `project-specification.json` in the output with the new prefix

### Replication Specification Format
```json
{
  "sourceDirectory": "/path/to/source-project",
  "newPrefix": ["com", "example"],
  "generateCodeStructure": false,
  "verbatimPaths": ["path/to/file/to/copy/unchanged"]
}
```

**Fields:**
- **sourceDirectory**: Path to the project to clone (absolute or relative to the spec file)
- **newPrefix**: Package prefix for the cloned project (replaces the source project's prefix)
- **generateCodeStructure**: Optional — overrides the `generateCodeStructure` flag in the copied `project-specification.json`
- **verbatimPaths**: Optional — list of paths to copy byte-for-byte without any text transformation

### Example
To clone `../my-template` into a new package namespace:
```json
{
  "sourceDirectory": "../my-template",
  "newPrefix": ["com", "acme", "newproject"],
  "generateCodeStructure": false
}
```

Run from the directory where the new project should be created:
```bash
java -jar /path/to/project-generator-replicator.jar replication-spec.json .
```

## Documentation
- [Staged Dependency Injection](docs/staged-dependency-injection.md)
- [Function-Based Events](docs/function-based-events.md)
- [Composition Root Pattern](docs/composition-root-pattern.md)

