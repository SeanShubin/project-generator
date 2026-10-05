package com.seanshubin.project.generator.core

/**
 * A dependency key from a module's dependency list, resolved against the
 * project's `dependencies` and `modules` sections.
 *
 * Resolution is build-system independent -- the rules for turning a key into
 * "a sibling module", "a Maven artifact" or "a crate" are the same whoever is
 * emitting -- so it is modeled here rather than in the Maven or Cargo emitter.
 * What each build system then *writes* for a resolved dependency differs
 * completely, and that part stays in the emitters.
 */
sealed interface ResolvedDependency {
    /** The key as written in the spec. */
    val key: String

    /** Optional scope; "test" is the only scope with cross-build-system meaning. */
    val scope: String?

    /** True when this dependency is only needed to compile and run tests. */
    val isTestScope: Boolean get() = scope == TEST_SCOPE

    /** Another module in this same project. */
    data class Module(
        override val key: String,
        override val scope: String?
    ) : ResolvedDependency

    /** An artifact from a Maven repository. */
    data class MavenArtifact(
        override val key: String,
        val group: String,
        val artifact: String,
        override val scope: String?
    ) : ResolvedDependency

    /** A crate from crates.io. */
    data class Crate(
        override val key: String,
        val crate: String,
        val version: String?,
        val features: List<String>,
        val defaultFeatures: Boolean?,
        override val scope: String?
    ) : ResolvedDependency

    companion object {
        const val TEST_SCOPE = "test"
    }
}
