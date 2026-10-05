package com.seanshubin.project.generator.cargo

import com.seanshubin.project.generator.core.DependencySpec
import com.seanshubin.project.generator.core.ModuleDependencyResolver
import com.seanshubin.project.generator.core.Preset
import com.seanshubin.project.generator.core.Project
import com.seanshubin.project.generator.core.ResolvedDependency

/**
 * Builds the TOML for a Cargo workspace and its member crates.
 *
 * Versions are centralized: every external crate is pinned once in the root
 * `[workspace.dependencies]`, and every member refers to it with
 * `{ workspace = true }`. That is the same two-level arrangement Maven gets
 * from `dependencyManagement` in the parent pom plus version-less
 * `<dependency>` entries in each child, which is why the module graph and
 * dependency resolution could be shared with the Maven emitter even though
 * none of the syntax is.
 *
 * Internal dependencies are path dependencies on sibling crates, so a crate's
 * name is simply its module name -- unlike the JVM, where the group id carries
 * the reverse domain name. `prefix` and `name` are therefore unused here.
 */
class CargoTomlNodeImpl(private val crateVersionLookup: CrateVersionLookup) : CargoTomlNode {
    override fun generateWorkspaceToml(project: Project): TomlNode {
        val effectiveProject = withPresetDependencies(project)
        val members = project.modules.keys.map { TomlValue.of(CargoLayout.cratePath(it)) }
        val workspaceTable = TomlNode.Table(
            "workspace",
            listOf(
                TomlNode.KeyValue("resolver", TomlValue.of(RESOLVER)),
                TomlNode.KeyValue("members", TomlValue.Array(members, multiline = true))
            )
        )
        val packageTable = TomlNode.Table(
            "workspace.package",
            listOf(
                TomlNode.KeyValue("version", TomlValue.of(project.version)),
                TomlNode.KeyValue("edition", TomlValue.of(EDITION))
            )
        )
        val dependenciesTable = TomlNode.Table(
            "workspace.dependencies",
            internalDependencyEntries(project) + externalDependencyEntries(effectiveProject)
        )
        val presetTables = when (project.preset) {
            Preset.NONE -> emptyList()
            Preset.BEVY -> BevyPreset.profileTables()
        }
        return TomlNode.Document(
            listOf(
                workspaceTable,
                TomlNode.BlankLine,
                packageTable,
                TomlNode.BlankLine,
                dependenciesTable
            ) + presetTables
        )
    }

    override fun generateCrateToml(project: Project, module: String, dependencies: List<String>): TomlNode {
        val effectiveProject = withPresetDependencies(project)
        val resolver = ModuleDependencyResolver(effectiveProject)

        // Cargo has no equivalent of Maven's inherited parent dependencies, so
        // global dependencies are repeated in each member that will use them.
        val dependencyKeys = (project.global + dependencies).distinct()
        val resolved = resolver.resolveAll(dependencyKeys).map { verifyCargoCompatible(it, module) }
        val (testScoped, compileScoped) = resolved.partition { it.isTestScope }

        val packageTable = TomlNode.Table(
            "package",
            listOf(
                TomlNode.KeyValue("name", TomlValue.of(module)),
                TomlNode.KeyValue("version.workspace", TomlValue.TRUE),
                TomlNode.KeyValue("edition.workspace", TomlValue.TRUE)
            )
        )
        val binaryTable = if (project.entryPoints.containsKey(module)) {
            listOf(
                TomlNode.BlankLine,
                TomlNode.ArrayTable(
                    "bin",
                    listOf(
                        TomlNode.KeyValue("name", TomlValue.of(module)),
                        TomlNode.KeyValue(
                            "path",
                            TomlValue.of("${CargoLayout.SOURCE_DIRECTORY}/${CargoLayout.MAIN_FILE}")
                        )
                    )
                )
            )
        } else {
            emptyList()
        }
        return TomlNode.Document(
            listOf(packageTable) +
                    binaryTable +
                    dependencyTable("dependencies", compileScoped) +
                    dependencyTable("dev-dependencies", testScoped)
        )
    }

    /**
     * A member's dependency table. Every entry defers to the workspace, so the
     * version and feature selection stay in one place at the root.
     */
    private fun dependencyTable(tableName: String, dependencies: List<ResolvedDependency>): List<TomlNode> {
        if (dependencies.isEmpty()) return emptyList()
        val entries = dependencies.map { dependency ->
            TomlNode.KeyValue(dependency.key, TomlValue.InlineTable(listOf("workspace" to TomlValue.TRUE)))
        }
        return listOf(TomlNode.BlankLine, TomlNode.Table(tableName, entries))
    }

    /** Every module is a path dependency, available to any member that asks for it. */
    private fun internalDependencyEntries(project: Project): List<TomlNode> =
        project.modules.keys.map { module ->
            TomlNode.KeyValue(
                module,
                TomlValue.InlineTable(listOf("path" to TomlValue.of(CargoLayout.cratePath(module))))
            )
        }

    private fun externalDependencyEntries(project: Project): List<TomlNode> =
        project.dependencies.entries
            .mapNotNull { (key, spec) ->
                (spec as? DependencySpec.Crate)?.let { key to it }
            }
            .map { (key, crate) ->
                TomlNode.KeyValue(key, crateValue(key, crate))
            }

    private fun crateValue(key: String, crate: DependencySpec.Crate): TomlValue {
        val version = crate.version ?: crateVersionLookup.latestProductionVersion(crate.crate)
        val entries = buildList {
            add("version" to TomlValue.of(version))

            // Only needed when the spec aliases the crate under a different key.
            if (crate.crate != key) {
                add("package" to TomlValue.of(crate.crate))
            }
            crate.defaultFeatures?.let { add("default-features" to TomlValue.of(it)) }
            if (crate.features.isNotEmpty()) {
                add("features" to TomlValue.Array(crate.features.map(TomlValue::of)))
            }
        }

        // A crate with nothing but a version reads better as `name = "1.2.3"`.
        return if (entries.size == 1) TomlValue.of(version) else TomlValue.InlineTable(entries)
    }

    private fun withPresetDependencies(project: Project): Project = when (project.preset) {
        Preset.NONE -> project
        Preset.BEVY -> project.copy(dependencies = BevyPreset.dependencies() + project.dependencies)
    }

    private fun verifyCargoCompatible(dependency: ResolvedDependency, module: String): ResolvedDependency {
        if (dependency is ResolvedDependency.MavenArtifact) {
            throw RuntimeException(
                "Module '$module' depends on '${dependency.key}', which is a maven artifact " +
                        "(${dependency.group}:${dependency.artifact}). A cargo project can only depend on " +
                        "crates and sibling modules."
            )
        }
        return dependency
    }

    companion object {
        /**
         * Hardcoded rather than taken from the spec because every Rust project
         * here uses the current edition; add a spec field if one ever needs to
         * pin an older one. The resolver version is the one edition 2024
         * implies.
         */
        private const val EDITION = "2024"
        private const val RESOLVER = "3"
    }
}
