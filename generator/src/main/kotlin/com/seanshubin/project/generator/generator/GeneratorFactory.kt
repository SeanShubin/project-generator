package com.seanshubin.project.generator.generator

import com.seanshubin.project.generator.cargo.CargoTomlNode
import com.seanshubin.project.generator.cargo.TomlRenderer
import com.seanshubin.project.generator.core.BuildSystem
import com.seanshubin.project.generator.core.Project
import com.seanshubin.project.generator.gradle.GradleFileNode
import com.seanshubin.project.generator.gradle.GradleKotlinDslRenderer
import com.seanshubin.project.generator.maven.MavenXmlNode
import com.seanshubin.project.generator.source.SourceFileFinder
import com.seanshubin.project.generator.source.SourceProjectLoader
import com.seanshubin.project.generator.xml.XmlRenderer
import java.nio.file.Path

/**
 * Assembles a [Generator] for a project, choosing the build system strategy
 * from the spec.
 *
 * The choice lives here, rather than at each wiring site, because both the
 * console and the replicator need it and neither should have to know the set of
 * build systems. The base directory is a parameter rather than a constructor
 * field because the replicator generates into a destination it only learns at
 * runtime.
 */
class GeneratorFactory(
    private val xmlRenderer: XmlRenderer,
    private val mavenXmlNode: MavenXmlNode,
    private val gradleFileNode: GradleFileNode,
    private val gradleRenderer: GradleKotlinDslRenderer,
    private val tomlRenderer: TomlRenderer,
    private val cargoTomlNode: CargoTomlNode,
    private val sourceProjectLoader: SourceProjectLoader,
    private val sourceFileFinder: SourceFileFinder,
    private val onSourceModulesNotFound: (List<String>) -> Unit,
    private val onTargetModulesNotFound: (List<String>) -> Unit,
    private val onDuplicateTargetModules: (Set<String>) -> Unit
) {
    fun create(project: Project, baseDirectory: Path): Generator {
        val buildSystemGenerator = buildSystemGenerator(project, baseDirectory)
        val sourceDependencyPlanner = SourceDependencyPlanner(
            baseDirectory,
            sourceProjectLoader,
            sourceFileFinder,
            onSourceModulesNotFound,
            onTargetModulesNotFound,
            onDuplicateTargetModules
        )
        return GeneratorImpl(buildSystemGenerator, sourceDependencyPlanner)
    }

    private fun buildSystemGenerator(project: Project, baseDirectory: Path): BuildSystemGenerator =
        when (project.buildSystem) {
            BuildSystem.MAVEN -> MavenBuildSystemGenerator(
                baseDirectory,
                xmlRenderer,
                mavenXmlNode,
                gradleFileNode,
                gradleRenderer,
                ScriptEmitter(baseDirectory, MAVEN_RESOURCE_DIRECTORY)
            )

            BuildSystem.CARGO -> CargoBuildSystemGenerator(
                baseDirectory,
                tomlRenderer,
                cargoTomlNode,
                ScriptEmitter(baseDirectory, CARGO_RESOURCE_DIRECTORY)
            )
        }

    companion object {
        private const val MAVEN_RESOURCE_DIRECTORY = "generated-project-files"
        private const val CARGO_RESOURCE_DIRECTORY = "generated-project-files/cargo"
    }
}
