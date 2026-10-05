package com.seanshubin.project.generator.cargo

import com.seanshubin.project.generator.core.BuildSystem
import com.seanshubin.project.generator.core.DependencySpec
import com.seanshubin.project.generator.core.Preset
import com.seanshubin.project.generator.core.Project
import com.seanshubin.project.generator.core.StringUtility
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CargoTomlNodeImplTest {
    private val stubCrateVersionLookup = object : CrateVersionLookup {
        override fun latestProductionVersion(crateName: String) = "9.9.9"
    }
    private val renderer = TomlRendererImpl(StringUtility.indent)
    private val cargoTomlNode = CargoTomlNodeImpl(stubCrateVersionLookup)

    private fun project(
        dependencies: Map<String, DependencySpec> = emptyMap(),
        global: List<String> = emptyList(),
        modules: Map<String, List<String>> = mapOf("engine" to emptyList()),
        entryPoints: Map<String, String> = emptyMap(),
        preset: Preset = Preset.NONE
    ) = Project(
        prefix = listOf("com", "example"),
        name = listOf("sample"),
        description = "Sample Project",
        version = "1.2.3",
        language = "rust",
        developer = null,
        dependencies = dependencies,
        versionOverrides = emptyList(),
        global = global,
        modules = modules,
        javaVersion = "25",
        entryPoints = entryPoints,
        buildSystem = BuildSystem.CARGO,
        preset = preset
    )

    private fun workspaceText(project: Project) =
        renderer.toLines(cargoTomlNode.generateWorkspaceToml(project)).joinToString("\n")

    private fun crateText(project: Project, module: String) =
        renderer.toLines(
            cargoTomlNode.generateCrateToml(project, module, project.modules.getValue(module))
        ).joinToString("\n")

    @Test
    fun workspaceListsEveryModuleAsMemberAndPathDependency() {
        val project = project(modules = mapOf("engine" to emptyList(), "rules" to listOf("engine")))
        val text = workspaceText(project)
        assertTrue(text.contains("\"crates/engine\","), text)
        assertTrue(text.contains("\"crates/rules\","), text)
        assertTrue(text.contains("engine = { path = \"crates/engine\" }"), text)
        assertTrue(text.contains("rules = { path = \"crates/rules\" }"), text)
    }

    @Test
    fun workspacePinsVersionAndEditionOnce() {
        val text = workspaceText(project())
        assertTrue(text.contains("[workspace.package]"), text)
        assertTrue(text.contains("version = \"1.2.3\""), text)
        assertTrue(text.contains("edition = \"2024\""), text)
        assertTrue(text.contains("resolver = \"3\""), text)
    }

    @Test
    fun crateWithOnlyAVersionRendersAsAPlainString() {
        val project = project(dependencies = mapOf("rand" to crate("rand")))
        assertTrue(workspaceText(project).contains("rand = \"9.9.9\""), workspaceText(project))
    }

    @Test
    fun declaredVersionIsUsedWithoutALookup() {
        val project = project(dependencies = mapOf("rand" to crate("rand", version = "0.8.5")))
        assertTrue(workspaceText(project).contains("rand = \"0.8.5\""), workspaceText(project))
    }

    @Test
    fun featuresAndDefaultFeaturesBecomeAnInlineTable() {
        val project = project(
            dependencies = mapOf(
                "bevy" to crate("bevy", features = listOf("2d", "ui"), defaultFeatures = false)
            )
        )
        val text = workspaceText(project)
        assertTrue(
            text.contains("bevy = { version = \"9.9.9\", default-features = false, features = [\"2d\", \"ui\"] }"),
            text
        )
    }

    @Test
    fun aliasedCrateCarriesThePackageKey() {
        val project = project(dependencies = mapOf("fastrand" to crate("rand")))
        assertTrue(workspaceText(project).contains("package = \"rand\""), workspaceText(project))
    }

    @Test
    fun crateManifestDefersEveryDependencyToTheWorkspace() {
        val project = project(
            dependencies = mapOf("serde" to crate("serde")),
            modules = mapOf("engine" to emptyList(), "rules" to listOf("engine", "serde"))
        )
        val text = crateText(project, "rules")
        assertTrue(text.contains("name = \"rules\""), text)
        assertTrue(text.contains("version.workspace = true"), text)
        assertTrue(text.contains("engine = { workspace = true }"), text)
        assertTrue(text.contains("serde = { workspace = true }"), text)
    }

    @Test
    fun globalDependenciesAreRepeatedInEveryCrate() {
        // Cargo has no inherited dependencies, so unlike Maven each member has
        // to name them.
        val project = project(
            dependencies = mapOf("serde" to crate("serde")),
            global = listOf("serde"),
            modules = mapOf("engine" to emptyList(), "rules" to emptyList())
        )
        assertTrue(crateText(project, "engine").contains("serde = { workspace = true }"))
        assertTrue(crateText(project, "rules").contains("serde = { workspace = true }"))
    }

    @Test
    fun testScopedDependencyGoesInDevDependencies() {
        val project = project(
            dependencies = mapOf("mockall" to crate("mockall", scope = "test")),
            modules = mapOf("engine" to listOf("mockall"))
        )
        val text = crateText(project, "engine")
        assertTrue(text.contains("[dev-dependencies]"), text)
        assertFalse(text.contains("\n[dependencies]"), text)
    }

    @Test
    fun crateWithNoDependenciesOmitsTheTable() {
        val text = crateText(project(), "engine")
        assertFalse(text.contains("[dependencies]"), text)
        assertFalse(text.contains("[dev-dependencies]"), text)
    }

    @Test
    fun entryPointModuleGetsABinarySection() {
        val project = project(entryPoints = mapOf("engine" to "ignored"))
        val text = crateText(project, "engine")
        assertTrue(text.contains("[[bin]]"), text)
        assertTrue(text.contains("name = \"engine\""), text)
        assertTrue(text.contains("path = \"src/main.rs\""), text)
    }

    @Test
    fun libraryModuleHasNoBinarySection() {
        assertFalse(crateText(project(), "engine").contains("[[bin]]"))
    }

    @Test
    fun bevyPresetAddsTheDependencyAndTheDevProfile() {
        val project = project(preset = Preset.BEVY, global = listOf("bevy"))
        val text = workspaceText(project)
        assertTrue(text.contains("bevy = \"9.9.9\""), text)
        assertTrue(text.contains("[profile.dev]"), text)
        assertTrue(text.contains("[profile.dev.package.\"*\"]"), text)
        assertTrue(crateText(project, "engine").contains("bevy = { workspace = true }"))
    }

    @Test
    fun projectOwnBevyDeclarationOverridesThePreset() {
        val project = project(
            dependencies = mapOf("bevy" to crate("bevy", version = "0.18.0", features = listOf("2d"))),
            preset = Preset.BEVY
        )
        val text = workspaceText(project)
        assertTrue(text.contains("version = \"0.18.0\""), text)
        assertTrue(text.contains("features = [\"2d\"]"), text)
    }

    @Test
    fun noPresetMeansNoProfileTables() {
        assertFalse(workspaceText(project()).contains("[profile"))
    }

    @Test
    fun mavenDependencyInACargoProjectIsRejected() {
        val project = project(
            dependencies = mapOf("stdlib" to DependencySpec.External("org.jetbrains.kotlin", "kotlin-stdlib", null)),
            modules = mapOf("engine" to listOf("stdlib"))
        )
        val failure = assertFailsWith<RuntimeException> {
            cargoTomlNode.generateCrateToml(project, "engine", listOf("stdlib"))
        }
        assertTrue(failure.message!!.contains("maven artifact"), failure.message!!)
    }

    @Test
    fun unknownDependencyIsRejected() {
        val project = project(modules = mapOf("engine" to listOf("nonexistent")))
        assertFailsWith<RuntimeException> {
            cargoTomlNode.generateCrateToml(project, "engine", listOf("nonexistent"))
        }
    }

    @Test
    fun crateNameIsTheModuleNameWithoutPrefixOrProjectName() {
        // Unlike the JVM, where the group id carries the reverse domain name.
        val text = crateText(project(), "engine")
        assertEquals("[package]", text.lines().first())
        assertTrue(text.contains("name = \"engine\""), text)
        assertFalse(text.contains("com"), text)
        assertFalse(text.contains("sample"), text)
    }

    private fun crate(
        name: String,
        version: String? = null,
        features: List<String> = emptyList(),
        defaultFeatures: Boolean? = null,
        scope: String? = null
    ) = DependencySpec.Crate(name, version, features, defaultFeatures, scope)
}
