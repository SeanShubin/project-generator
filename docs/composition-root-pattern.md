# Composition Root Pattern

## Overview

Composition roots are responsible for **wiring dependencies only** - they should not contain any logic, formatting, or business rules. All logic belongs in dedicated classes with clear responsibilities.

## The Pattern

### Before: Logic in Composition Root

```kotlin
class ProjectDependencies(
    project: Project,
    baseDirectory: Path,
    private val integrations: Integrations
) {
    // ❌ BAD: Lambda contains logic (object creation based on parameters)
    private val createKeyStore: (Path) -> KeyValueStore = { path: Path ->
        JsonFileKeyValueStore(files, path)
    }

    // ❌ BAD: String formatting logic in composition root
    private val onPathNotDirectory: (Path) -> Unit = { path ->
        integrations.emitError("Warning: Source module path is not a directory, skipping: $path")
    }
}
```

**Why this is bad:**
- Composition root knows how to create KeyValueStores (construction logic)
- Composition root knows how to format error messages (formatting logic)
- Logic cannot be tested independently
- Violates single responsibility - composition roots should only wire

### After: Logic Extracted to Dedicated Classes

```kotlin
class ProjectDependencies(
    project: Project,
    baseDirectory: Path,
    private val integrations: Integrations
) {
    // ✅ GOOD: Create factory object
    private val keyValueStoreFactory = JsonFileKeyValueStoreFactory(files)

    // ✅ GOOD: Create event consumer
    private val sourceFileEventConsumer = SourceFileEventConsumer(integrations.emitError)

    // ✅ GOOD: Pass method references - no logic here
    private val environment: Environment = EnvironmentImpl(
        files,
        keyValueStoreFactory::create,  // Factory handles creation logic
        sourceFileEventConsumer::onPathNotDirectory,  // Consumer handles formatting
        // ...
    )
}
```

**Why this is better:**
- Composition root only creates objects and passes references
- Factory classes handle creation logic
- Event consumers handle formatting logic
- Each class has a single, clear responsibility
- All logic is testable independently

## Factory Classes

### Purpose
Factory classes encapsulate object creation logic. When you need to create objects based on runtime parameters, extract that logic into a factory.

### Pattern
```kotlin
// Factory interface (if needed for multiple implementations)
interface KeyValueStoreFactory {
    fun create(path: Path): KeyValueStore
}

// Factory implementation
class JsonFileKeyValueStoreFactory(private val files: FilesContract) {
    fun create(path: Path): KeyValueStore {
        return JsonFileKeyValueStore(files, path)
    }
}

// Usage in composition root
class ProjectDependencies(...) {
    private val keyValueStoreFactory = JsonFileKeyValueStoreFactory(files)
    private val environment = EnvironmentImpl(
        files,
        keyValueStoreFactory::create  // Pass method reference
    )
}
```

### When to Use
- When composition root needs to pass a function that creates objects
- When creation logic depends on runtime parameters
- When you want to encapsulate construction decisions

## Event Consumer Classes

### Purpose
Event consumer classes encapsulate event formatting logic. When domain classes emit events, consumers format those events for output.

### Pattern
```kotlin
// Event consumer
class SourceFileEventConsumer(private val emitError: (String) -> Unit) {
    fun onPathNotDirectory(path: Path) {
        emitError("Warning: Source module path is not a directory, skipping: $path")
    }

    fun onSourceFileNotFound(sourcePath: Path) {
        emitError("Error: Source file not found: $sourcePath")
    }
}

// Usage in composition root
class ProjectDependencies(...) {
    private val sourceFileEventConsumer = SourceFileEventConsumer(integrations.emitError)
    private val sourceFileFinder = SourceFileFinderImpl(
        files,
        sourceFileEventConsumer::onPathNotDirectory  // Pass method reference
    )
}
```

### When to Use
- When domain classes need to emit events with structured data
- When you need different formatting for production vs test
- When you want to keep formatting logic out of composition roots

## Examples from This Codebase

### Factory Classes
- `JsonFileKeyValueStoreFactory` - Creates KeyValueStore instances
- `ProjectRunnerFactory` - Creates ProjectDependencies.runner for next DI stage
- `ConfigRunnerFactory` - Creates ConfigFileDependencies.runner for next DI stage

### Event Consumer Classes
- `MavenEventConsumer` - Formats Maven lookup events
- `SourceFileEventConsumer` - Formats source file events
- `ModuleMappingEventConsumer` - Formats module mapping validation events
- `FileOperationEventConsumer` - Formats file operation events

### Composition Roots
- `EntryPoint` - Creates ProductionIntegrations and starts bootstrap
- `ArgsDependencies` - Stage 1: Parses args and wires to config stage
- `ConfigFileDependencies` - Stage 2: Loads config and wires to project stage
- `ProjectDependencies` - Stage 3: Wires all application dependencies

## Benefits of This Pattern

### 1. **Clear Separation of Concerns**
- **Factories** - Handle object creation logic
- **Event Consumers** - Handle event formatting logic
- **Composition Roots** - Handle dependency wiring only

### 2. **Testability**
- Factory logic can be tested independently
- Event formatting can be tested independently
- Composition roots need no tests (pure wiring)

### 3. **Single Responsibility**
Each class has one reason to change:
- Factory changes when construction logic changes
- Event consumer changes when formatting changes
- Composition root changes when dependencies change

### 4. **No Logic in Composition Roots**
Composition roots become pure declarations:
```kotlin
private val factory = FactoryImpl(dependencies)
private val consumer = ConsumerImpl(output)
private val service = ServiceImpl(factory::method, consumer::method)
```

No conditionals, no string formatting, no calculations - just object creation and wiring.

## Verification

To verify composition roots contain no logic:

1. **No lambdas with logic** - `{ x -> new Thing(x) }` should be `factory::create`
2. **No string formatting** - `{ x -> emit("Error: $x") }` should be `consumer::onError`
3. **No conditionals** - `if`/`else`/`when` don't belong in composition roots
4. **No calculations** - arithmetic, string manipulation belong in other classes
5. **Only object creation and method references** - `val x = X(deps)` and `x::method`

## Related Documentation

- [Staged Dependency Injection](staged-dependency-injection.md) - DI pattern for runtime configuration
- [Function-Based Events](function-based-events.md) - Event system using function injection
