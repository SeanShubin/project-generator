# Staged Dependency Injection Implementation

## Overview

This document describes the staged dependency injection pattern implemented in this project, which separates I/O boundaries from business logic to enable comprehensive testing.

## Architecture

### Stages

The application now has four distinct stages:

1. **Integrations Stage** (I/O Boundaries)
   - `Integrations` interface defines all I/O operations
   - `ProductionIntegrations` provides real implementations
   - Test implementations can provide fakes

2. **Args Stage** (Command-Line Parsing)
   - `ArgsDependencies` takes `Integrations` + command-line args
   - Parses arguments and creates next stage

3. **Config Stage** (Configuration Loading)
   - `ConfigFileDependencies` takes `Integrations` + config paths
   - Loads configuration from files
   - Creates application dependencies

4. **Application Stage** (Business Logic)
   - `ProjectDependencies` takes `Integrations` + configuration
   - Wires all domain objects and business logic
   - No direct I/O access

### Flow

```
EntryPoint.main(args)
  ↓
  Creates ProductionIntegrations
  ↓
ArgsDependencies(args, integrations)
  ↓
  Parses command-line arguments
  ↓
ConfigFileDependencies(configFile, baseDir, integrations)
  ↓
  Loads configuration
  ↓
ProjectDependencies(project, baseDir, integrations)
  ↓
  Wires all application components
  ↓
runner.run()
```

## I/O Boundaries in Integrations

The `Integrations` interface captures all I/O operations:

- **`emit: (String) -> Unit`** - Standard output (System.out)
- **`emitError: (String) -> Unit`** - Standard error (System.err)
- **`files: FilesContract`** - File system operations
- **`httpClientFactory: HttpClientFactory`** - HTTP client creation

## Benefits

### 1. Complete Testability

The entire application can now be tested with fake integrations:

```kotlin
// Test integrations
class TestIntegrations : Integrations {
    val emittedLines = mutableListOf<String>()
    val emittedErrors = mutableListOf<String>()

    override val emit = { line: String -> emittedLines.add(line) }
    override val emitError = { line: String -> emittedErrors.add(line) }
    override val files = InMemoryFilesContract()
    override val httpClientFactory = FakeHttpClientFactory()
}

// Test usage
val testIntegrations = TestIntegrations()
val dependencies = ArgsDependencies(arrayOf("test-config.json"), testIntegrations)
dependencies.runner.run()

// Verify output
assertEquals(expectedLine, testIntegrations.emittedLines[0])
```

### 2. Deep Testing Enabled

You can now test the entire dependency chain from `ArgsDependencies` through `ProjectDependencies` without any real I/O:

- Test full application flow with fake file system
- Capture and verify all output
- Provide canned HTTP responses
- No mocking of internal collaborators needed

### 3. Clear Separation of Concerns

- **Integrations**: Pure I/O boundary
- **Bootstrap stages**: Configuration and setup
- **Application**: Pure business logic

### 4. Easy Environment Switching

```kotlin
// Production
fun main(args: Array<String>) {
    val integrations = ProductionIntegrations
    ArgsDependencies(args, integrations).runner.run()
}

// Testing
fun test() {
    val integrations = TestIntegrations()
    ArgsDependencies(testArgs, integrations).runner.run()
}

// Debug with captured output
fun debug() {
    val capturedOutput = mutableListOf<String>()
    val integrations = object : Integrations {
        override val emit = { line: String ->
            capturedOutput.add(line)
            println(line)
        }
        // ... other implementations
    }
    ArgsDependencies(args, integrations).runner.run()
}
```

## Files Changed

### New Files
- `console/src/main/kotlin/.../Integrations.kt` - I/O boundary interface
- `console/src/main/kotlin/.../ProductionIntegrations.kt` - Production implementation

### Modified Files
- `console/src/main/kotlin/.../EntryPoint.kt` - Creates and passes ProductionIntegrations
- `console/src/main/kotlin/.../ArgsDependencies.kt` - Accepts and passes Integrations
- `console/src/main/kotlin/.../ConfigFileDependencies.kt` - Accepts and passes Integrations
- `console/src/main/kotlin/.../ProjectDependencies.kt` - Uses Integrations for I/O

## Pattern Compliance

This implementation follows the **Staged Dependency Injection** pattern described in the project's architectural rules:

- **Stage 1: Integrations** - Pure I/O boundary (files, network, console)
- **Stage 2-3: Bootstrap** - Args and Config stages load configuration
- **Stage 4: Application** - ProjectDependencies wires domain objects

Each stage:
1. Takes dependencies from previous stages
2. Creates new dependencies needed by subsequent stages
3. Passes accumulated dependencies forward

## Future Enhancements

### Test Integrations Implementation

Create a comprehensive test integrations implementation:

```kotlin
class TestIntegrations(
    private val fileContents: Map<Path, String> = emptyMap(),
    private val httpResponses: Map<String, String> = emptyMap()
) : Integrations {
    val emittedLines = mutableListOf<String>()
    val emittedErrors = mutableListOf<String>()

    override val emit = { line: String -> emittedLines.add(line) }
    override val emitError = { line: String -> emittedErrors.add(line) }
    override val files = InMemoryFilesContract(fileContents)
    override val httpClientFactory = FakeHttpClientFactory(httpResponses)
}
```

### Integration Test Suite

Create integration tests using TestIntegrations:

```kotlin
class ApplicationIntegrationTest {
    @Test
    fun testFullApplicationFlow() {
        val fileContents = mapOf(
            Paths.get("project-specification.json") to """{"prefix": ["com"], ...}"""
        )
        val integrations = TestIntegrations(fileContents)

        ArgsDependencies(arrayOf("project-specification.json"), integrations).runner.run()

        // Verify application behavior through captured I/O
        assertTrue(integrations.emittedLines.any { it.contains("Generated successfully") })
    }
}
```

## References

- Event Systems rule: All events now go through injected interfaces
- Dependency Injection rule: Staged DI section
- Coupling and Cohesion: I/O separated from business logic
