package com.seanshubin.project.generator.core

/**
 * Specification for a dependency, which can be either external (from Maven Central, etc.)
 * or internal (a module within the same project).
 *
 * The key in the dependencies map serves different purposes:
 * - For External: It's an alias used to reference the dependency in the modules section
 * - For Internal: It's the actual module name
 *
 * Example in project-specification.json:
 * ```json
 * {
 *   "dependencies": {
 *     "stdlib": {
 *       "group": "org.jetbrains.kotlin",
 *       "artifact": "kotlin-stdlib-jdk8"
 *     },
 *     "test": {
 *       "group": "org.jetbrains.kotlin",
 *       "artifact": "kotlin-test-junit",
 *       "scope": "test"
 *     },
 *     "di-test": {
 *       "scope": "test"
 *     }
 *   },
 *   "modules": {
 *     "di-contract": [],
 *     "di-test": ["di-contract"],
 *     "domain": ["di-test"]
 *   }
 * }
 * ```
 *
 * In this example:
 * - `stdlib` and `test` are External dependencies (have group/artifact)
 * - `di-test` is an Internal dependency (no group/artifact, key is the module name)
 * - `domain` depends on `di-test` at test scope
 */
sealed class DependencySpec {
    abstract val scope: String?

    /**
     * An external dependency from a Maven repository.
     *
     * @property group The Maven group ID (e.g., "org.jetbrains.kotlin")
     * @property artifact The Maven artifact ID (e.g., "kotlin-stdlib-jdk8")
     * @property scope Optional scope (e.g., "test", "provided"). Defaults to "compile" if omitted.
     */
    data class External(
        val group: String,
        val artifact: String,
        override val scope: String?
    ) : DependencySpec()

    /**
     * An internal module dependency within the same project.
     *
     * The module name is derived from the key in the dependencies map.
     * Group and artifact are inferred from the project structure.
     *
     * @property scope Optional scope (e.g., "test", "provided"). Defaults to "compile" if omitted.
     */
    data class Internal(
        override val scope: String?
    ) : DependencySpec()
}
