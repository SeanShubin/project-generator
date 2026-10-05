package com.seanshubin.project.generator.generator

import com.seanshubin.project.generator.cargo.CargoLayout
import com.seanshubin.project.generator.cargo.CargoTomlNode
import com.seanshubin.project.generator.cargo.TomlRenderer
import com.seanshubin.project.generator.commands.Command
import com.seanshubin.project.generator.commands.CreateDirectory
import com.seanshubin.project.generator.commands.CreateFileIfAbsent
import com.seanshubin.project.generator.commands.WriteFile
import com.seanshubin.project.generator.core.DependencySpec
import com.seanshubin.project.generator.core.Preset
import com.seanshubin.project.generator.core.Project
import java.nio.file.Path

/**
 * Emits a Cargo project: a workspace manifest at the root, a member crate per
 * module under `crates/`, and `cargo`-based helper scripts.
 *
 * Several spec features are JVM-only and are rejected rather than ignored --
 * see [validate]. The alternative would be generating a project that looks
 * configured and is not.
 */
class CargoBuildSystemGenerator(
    private val baseDirectory: Path,
    private val tomlRenderer: TomlRenderer,
    private val cargoTomlNode: CargoTomlNode,
    private val scriptEmitter: ScriptEmitter
) : BuildSystemGenerator {
    override fun validate(project: Project) {
        rejectUnsupported(project.sourceDependencies.isNotEmpty(), "sourceDependencies") {
            "Source replication rewrites JVM package and import statements, which have no " +
                    "equivalent in Rust, where a module path does not follow the directory layout."
        }
        rejectUnsupported(project.generateCodeStructure, "generateCodeStructure") {
            "The code structure tool analyzes JVM bytecode."
        }
        rejectUnsupported(project.mavenPlugin.isNotEmpty(), "mavenPlugin") {
            "Maven plugins are a JVM concept."
        }
        rejectUnsupported(project.gradlePlugin.isNotEmpty(), "gradlePlugin") {
            "Gradle plugins are a JVM concept."
        }
        rejectUnsupported(project.publishToMavenCentral, "publishToMavenCentral") {
            "Crates publish to crates.io, not Maven Central."
        }
        verifyNoCrateShadowsAModule(project)
    }

    override fun rootCommands(project: Project): List<Command> {
        val toml = cargoTomlNode.generateWorkspaceToml(project)
        val lines = tomlRenderer.toLines(toml)
        val path = baseDirectory.resolve(CargoLayout.MANIFEST_FILE)
        return listOf(WriteFile(path, lines))
    }

    override fun moduleCommands(project: Project, module: String, dependencies: List<String>): List<Command> {
        val toml = cargoTomlNode.generateCrateToml(project, module, dependencies)
        val lines = tomlRenderer.toLines(toml)
        val cratePath = baseDirectory.resolve(CargoLayout.CRATES_DIRECTORY).resolve(module)
        val sourcePath = cratePath.resolve(CargoLayout.SOURCE_DIRECTORY)

        // A crate with a manifest but no crate root does not compile, so a stub
        // is supplied -- but only if absent, since after the first run this file
        // holds real code.
        val isBinary = project.entryPoints.containsKey(module)
        val rootFileName = if (isBinary) CargoLayout.MAIN_FILE else CargoLayout.LIB_FILE
        val rootFileContent = if (isBinary) binaryStub(project) else libraryStub(module)

        return listOf(
            CreateDirectory(sourcePath),
            WriteFile(cratePath.resolve(CargoLayout.MANIFEST_FILE), lines),
            CreateFileIfAbsent(sourcePath.resolve(rootFileName), rootFileContent)
        )
    }

    override fun supplementalCommands(project: Project): List<Command> {
        val assetsCommands = when (project.preset) {
            Preset.NONE -> emptyList()
            // Bevy loads assets from a directory next to the binary by default,
            // and panics at runtime if it is missing.
            Preset.BEVY -> listOf(CreateDirectory(baseDirectory.resolve("assets")))
        }
        return helperFileCommands() + assetsCommands
    }

    private fun helperFileCommands(): List<Command> =
        listOf(
            scriptEmitter.fileAtRoot("gitignore.txt", ".gitignore"),
            scriptEmitter.scriptsDirectory()
        ) + SCRIPT_NAMES.flatMap { scriptName ->
            // Both shells are generated because every Rust project here is
            // driven from PowerShell on Windows and bash elsewhere.
            listOf(
                scriptEmitter.script("$scriptName.sh"),
                scriptEmitter.script("$scriptName.ps1")
            )
        }

    private fun libraryStub(module: String): String =
        "//! The ${module} crate.\n"

    private fun binaryStub(project: Project): String = when (project.preset) {
        Preset.NONE -> "fn main() {\n    println!(\"hello\");\n}\n"
        Preset.BEVY -> "use bevy::prelude::*;\n\nfn main() {\n" +
                "    App::new().add_plugins(DefaultPlugins).run();\n}\n"
    }

    private fun rejectUnsupported(isPresent: Boolean, fieldName: String, reason: () -> String) {
        if (isPresent) {
            throw IllegalArgumentException(
                "$fieldName is not supported for cargo projects. ${reason()}"
            )
        }
    }

    /**
     * A crate dependency keyed the same as a module would produce two entries
     * with one key in `[workspace.dependencies]`, which is a TOML parse error.
     * Caught here so the message names the cause.
     */
    private fun verifyNoCrateShadowsAModule(project: Project) {
        val shadowed = project.dependencies
            .filter { (_, spec) -> spec is DependencySpec.Crate }
            .keys
            .filter { project.modules.containsKey(it) }
        if (shadowed.isNotEmpty()) {
            throw IllegalArgumentException(
                "These crate dependencies have the same name as a module: ${shadowed.joinToString(", ")}. " +
                        "A crate name must be distinct from every module name, because both become entries " +
                        "in [workspace.dependencies]."
            )
        }
    }

    companion object {
        private val SCRIPT_NAMES = listOf(
            "_build", "_clean", "_test", "_doc",
            "build", "clean", "test", "doc"
        )
    }
}
