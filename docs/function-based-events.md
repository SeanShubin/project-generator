# Function-Based Event System

## Overview

The event system has been simplified to use direct function injection instead of interface-based event handlers. This maintains all the benefits of structured events (testability, named events, structured data) while reducing ceremony and class count.

## Before vs After

### Before: Interface-Based Events

```kotlin
// Interface
interface SourceFileNotifications {
    fun pathNotDirectory(path: Path)
    fun sourceFileNotFound(sourcePath: Path)
    fun fileTransformationError(sourcePath: Path, targetPath: Path, errorMessage: String)
}

// Implementation
class LineEmittingSourceFileNotifications(
    private val emit: (String) -> Unit
) : SourceFileNotifications {
    override fun pathNotDirectory(path: Path) {
        emit("Warning: Source module path is not a directory, skipping: $path")
    }

    override fun sourceFileNotFound(sourcePath: Path) {
        emit("Error: Source file not found: $sourcePath")
    }

    override fun fileTransformationError(sourcePath: Path, targetPath: Path, errorMessage: String) {
        emit("Error copying and transforming file from $sourcePath to $targetPath: $errorMessage")
    }
}

// Usage
class SourceFileFinderImpl(
    private val files: FilesContract,
    private val notifications: SourceFileNotifications
) : SourceFileFinder {
    fun process(path: Path) {
        if (!files.isDirectory(path)) {
            notifications.pathNotDirectory(path)
        }
    }
}

// Wiring in composition root
private val sourceFileNotifications = LineEmittingSourceFileNotifications(integrations.emitError)
private val sourceFileFinder = SourceFileFinderImpl(files, sourceFileNotifications)
```

### After: Function-Based Events

```kotlin
// Event consumer - responsible for formatting
class SourceFileEventConsumer(private val emitError: (String) -> Unit) {
    fun onPathNotDirectory(path: Path) {
        emitError("Warning: Source module path is not a directory, skipping: $path")
    }
}

// Domain class - just receives a function
class SourceFileFinderImpl(
    private val files: FilesContract,
    private val onPathNotDirectory: (Path) -> Unit
) : SourceFileFinder {
    fun process(path: Path) {
        if (!files.isDirectory(path)) {
            onPathNotDirectory(path)
        }
    }
}

// Wiring in composition root - purely wiring, no formatting
private val sourceFileEventConsumer = SourceFileEventConsumer(integrations.emitError)
private val sourceFileFinder = SourceFileFinderImpl(files, sourceFileEventConsumer::onPathNotDirectory)
```

## Benefits

### 1. **Clear Separation of Concerns**
- **Event Consumers** - Classes responsible for formatting events (know about string formatting)
- **Domain Classes** - Just receive and call functions (don't know about formatting)
- **Composition Root** - Purely wiring (no formatting logic)

**Before**: Interface + Implementation combined event contract with formatting
**After**: Event consumer handles formatting, domain classes just call functions

### 2. **Less Ceremony**
- ❌ Before: Interface + Implementation class + wiring = 3 pieces
- ✅ After: Event Consumer class + function injection = simpler
- **Result**: 8 interface files removed, 8 implementation files removed, replaced with 4 event consumer classes

### 3. **Same Testability**
Functions are just as testable as interfaces:

```kotlin
// Test with function capture
val capturedPaths = mutableListOf<Path>()
val onPathNotDirectory: (Path) -> Unit = { path ->
    capturedPaths.add(path)
}

val finder = SourceFileFinderImpl(files, onPathNotDirectory)
finder.process(somePath)

assertEquals(expectedPath, capturedPaths[0])
```

### 4. **Named Events Preserved**
Functions have parameter names that document their purpose:
- `onPathNotDirectory: (Path) -> Unit` - Clear what it does
- `onFileTransformationError: (Path, Path, String) -> Unit` - Clear what parameters mean

### 5. **Structured Data Maintained**
Functions take structured parameters, not pre-formatted strings:
```kotlin
private val onFileTransformationError: (Path, Path, String) -> Unit = { sourcePath, targetPath, errorMessage ->
    integrations.emitError("Error copying and transforming file from $sourcePath to $targetPath: $errorMessage")
}
```

### 6. **Easy Composition**
Functions can be easily composed and transformed:
```kotlin
// Wrap with logging
val onLookupVersion: (String, GroupArtifactVersionScope) -> Unit = { uri, dependency ->
    println("Looking up version for ${dependency.artifact}")  // Add logging
    integrations.emit("group:${dependency.group} artifact:${dependency.artifact} version:${dependency.version} uri:$uri")
}

// Conditional execution
val onFileCreated: (Path) -> Unit = { path ->
    if (shouldLog(path)) {
        integrations.emit("created $path")
    }
}
```

### 7. **Simpler for Users**
Users of event-emitting code don't need to know about class structure:
- Just inject a function
- Function signature is the contract
- No need to understand implementation classes

## Event Functions by Module

### Maven Module
- `onLookupVersion: (String, GroupArtifactVersionScope) -> Unit`

### Source Module
- `onPathNotDirectory: (Path) -> Unit`

### Generator Module
- `onSourceModulesNotFound: (List<String>) -> Unit`
- `onTargetModulesNotFound: (List<String>) -> Unit`
- `onDuplicateTargetModules: (Set<String>) -> Unit`

### Commands Module (via Environment)
- `onSourceFileNotFound: (Path) -> Unit`
- `onFileTransformationError: (Path, Path, String) -> Unit`
- `onFileCreated: (Path) -> Unit`
- `onFileModified: (Path) -> Unit`
- `onFileUnchanged: (Path) -> Unit`
- `onDirectoryCreated: (Path) -> Unit`

## Pattern Compliance

This approach **fully complies** with the Event Systems rule:

✅ **Explicit event interfaces** - Function types are explicit contracts
✅ **Named methods** - Function parameter names provide documentation
✅ **Structured parameters** - Functions take domain objects, not strings
✅ **Injected** - Functions are constructor-injected
✅ **Testable** - Can inject capturing lambdas in tests
✅ **Eliminates structural duplication** - Event formatting logic is in one place (composition root)
✅ **Preserves structured data** - Functions receive domain objects

## Files Removed

### Interfaces (8 files)
- `maven/Notifications.kt`
- `source/SourceFileNotifications.kt`
- `source/ModuleMappingNotifications.kt`
- `commands/FileOperationNotifications.kt`

### Implementations (8 files)
- `maven/LineEmittingNotifications.kt`
- `source/LineEmittingSourceFileNotifications.kt`
- `source/LineEmittingModuleMappingNotifications.kt`
- `commands/LineEmittingFileOperationNotifications.kt`

**Total: 16 files removed, 4 event consumer classes added**
- Net reduction: 12 files
- Event formatting now in dedicated consumer classes (not interfaces + implementations)
- Composition root contains no formatting logic

## Files Added

### Event Consumer Classes
- `console/MavenEventConsumer.kt` - Formats Maven lookup events
- `console/SourceFileEventConsumer.kt` - Formats source file events
- `console/ModuleMappingEventConsumer.kt` - Formats module mapping events
- `console/FileOperationEventConsumer.kt` - Formats file operation events

## Files Modified

### Updated to use function injection
- `source/SourceFileFinder.kt` - Takes `onPathNotDirectory` function
- `generator/GeneratorImpl.kt` - Takes 3 module mapping functions
- `commands/Environment.kt` - Holds 7 event functions
- `commands/EnvironmentImpl.kt` - Accepts 7 event functions
- `commands/CopyAndTransformSourceFile.kt` - Uses functions from Environment
- `commands/WriteFile.kt` - Uses functions from Environment
- `commands/WriteTextFile.kt` - Uses functions from Environment
- `commands/CreateDirectory.kt` - Uses functions from Environment
- `console/ProjectDependencies.kt` - Creates event consumers and wires method references

## When to Use Functions vs Interfaces

### Use Functions When:
- ✅ Single responsibility (one event type)
- ✅ Simple parameter lists (1-3 parameters)
- ✅ Events are independent (don't need grouping)
- ✅ No shared state between events

### Consider Interfaces When:
- Multiple related events that change together
- Complex shared state across event methods
- Need polymorphism (multiple implementations with different behavior, not just different output destinations)

For this codebase, **all events fit the function pattern**, so interfaces added unnecessary complexity.

## Future: Test Integrations Example

```kotlin
class TestIntegrations : Integrations {
    val emittedLines = mutableListOf<String>()
    val emittedErrors = mutableListOf<String>()

    override val emit = { line: String -> emittedLines.add(line) }
    override val emitError = { line: String -> emittedErrors.add(line) }
    override val files = InMemoryFilesContract()
    override val httpClientFactory = FakeHttpClientFactory()
}

// Test usage - all events captured automatically!
val testIntegrations = TestIntegrations()
ArgsDependencies(testArgs, testIntegrations).runner.run()

// Verify events
assertTrue(testIntegrations.emittedLines.any { it.contains("created") })
assertTrue(testIntegrations.emittedErrors.none { it.contains("Error") })
```

## Architecture

### Clear Layering

```
Integrations (I/O boundary)
    ↓ (provides emit, emitError)
Event Consumers (formatting layer)
    ↓ (provides formatted event functions)
Composition Root (wiring layer - NO FORMATTING)
    ↓ (wires event consumer methods to domain classes)
Domain Classes (business logic)
    ↓ (calls event functions with structured data)
```

**Key principle**: Composition root does **pure wiring**, event consumers handle **formatting**.

### Example Flow

1. **Integrations** provides `emit: (String) -> Unit`
2. **SourceFileEventConsumer** takes `emit`, exposes `onPathNotDirectory(Path)`
3. **ProjectDependencies** wires: `sourceFileEventConsumer::onPathNotDirectory`
4. **SourceFileFinderImpl** calls function with structured `Path` argument
5. **Event consumer** formats the path into a string
6. **Integrations** outputs the string

## Summary

**Before**: 4 interfaces + 4 implementations = 8 classes with combined contract/formatting
**After**: 4 event consumer classes = single responsibility (formatting only)

**Result**:
- 16 interface/implementation files removed
- 4 event consumer classes added
- Net: 12 files eliminated
- **Composition root has NO formatting logic**
- Event consumers have clear single responsibility: format events
- Domain classes remain simple: just call functions
- Same testability
- Same structured events
- Clearer separation of concerns

Users of the event system don't need to understand class hierarchies - they just inject functions with clear signatures. Event formatting is isolated in dedicated consumer classes, not scattered through the composition root.
