# Project Generator

A tool to generate Maven multi-module Kotlin projects from a JSON specification file, eliminating the need to manually create and maintain pom.xml files.

## Features
- Generate complete multi-module Maven projects from JSON
- Declare dependencies once, apply globally or per-module
- Automatic module dependency management
- Entry point configuration with jar packaging
- Source dependency copying from other projects
- Publishing configuration (Maven Central)

## Prerequisites
- Java 25+
- Maven 3.x
- Kotlin 2.3.0+

## Building
```bash
mvn clean package
```

## Usage

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
      "artifact": "kotlin-stdlib-jdk8"
    },
    "test": {
      "group": "org.jetbrains.kotlin",
      "artifact": "kotlin-test-junit",
      "scope": "test"
    }
  },
  "modules": {
    "console": ["domain"],
    "domain": []
  },
  "entryPoints": {
    "console": "com.example.my.project.console.Main"
  },
  "javaVersion": "25"
}
```

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
- **dependencies**: Named dependencies with group/artifact/version/scope
- **global**: Dependencies applied to all modules
- **entryPoints**: Module names mapped to main class paths
- **sourceDependencies**: Optional - copy and transform source code from other local projects (array)
  - Each entry contains:
    - `sourceProjectPath`: Path to external project (relative or absolute)
    - `moduleMapping`: Map of source module names to target module names
  - Source code is copied and package names are transformed to match target project structure
- **versionOverrides**: Optional - override dependency versions

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

## Documentation
- [Staged Dependency Injection](docs/staged-dependency-injection.md)
- [Function-Based Events](docs/function-based-events.md)
- [Composition Root Pattern](docs/composition-root-pattern.md)

## License
Unlicense
