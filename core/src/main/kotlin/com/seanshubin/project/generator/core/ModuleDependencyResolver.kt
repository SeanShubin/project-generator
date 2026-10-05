package com.seanshubin.project.generator.core

/**
 * Turns the dependency keys in a project's `modules` section into
 * [ResolvedDependency] values.
 *
 * A key is looked up in the `dependencies` section first, so that an entry
 * there can attach a scope to a sibling module; failing that it is taken as a
 * module name; failing that it is an error.  This is the one piece of
 * dependency semantics shared by every build system.
 *
 * Note that `global` is deliberately not folded in here.  Whether global
 * dependencies are inherited by modules or have to be repeated in each one is a
 * build-system difference -- Maven inherits them from the parent pom, while
 * Cargo has no such mechanism -- so each emitter decides, and passes the keys
 * it wants resolved.
 */
class ModuleDependencyResolver(private val project: Project) {
    fun resolveAll(dependencyKeys: List<String>): List<ResolvedDependency> =
        dependencyKeys.map(::resolve)

    fun resolve(dependencyKey: String): ResolvedDependency = when {
        project.dependencies.containsKey(dependencyKey) ->
            fromSpec(dependencyKey, project.dependencies.getValue(dependencyKey))

        project.modules.containsKey(dependencyKey) ->
            ResolvedDependency.Module(dependencyKey, scope = null)

        else ->
            throw RuntimeException("Dependency '$dependencyKey' not found in dependencies or modules")
    }

    private fun fromSpec(dependencyKey: String, spec: DependencySpec): ResolvedDependency = when (spec) {
        is DependencySpec.Internal ->
            ResolvedDependency.Module(dependencyKey, spec.scope)

        is DependencySpec.External ->
            ResolvedDependency.MavenArtifact(dependencyKey, spec.group, spec.artifact, spec.scope)

        is DependencySpec.Crate ->
            ResolvedDependency.Crate(
                dependencyKey,
                spec.crate,
                spec.version,
                spec.features,
                spec.defaultFeatures,
                spec.scope
            )
    }
}
