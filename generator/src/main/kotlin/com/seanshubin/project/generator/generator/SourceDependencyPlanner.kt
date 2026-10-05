package com.seanshubin.project.generator.generator

import com.seanshubin.project.generator.commands.Command
import com.seanshubin.project.generator.commands.CopyAndTransformSourceFile
import com.seanshubin.project.generator.core.ModuleName
import com.seanshubin.project.generator.core.Project
import com.seanshubin.project.generator.core.SourceDependency
import com.seanshubin.project.generator.source.PackageTransformation
import com.seanshubin.project.generator.source.SourceFileFinder
import com.seanshubin.project.generator.source.SourceProjectLoader
import java.nio.file.Path

/**
 * Plans the copying and rewriting of source files imported from another
 * project.
 *
 * This is independent of the build system in the sense that nothing here reads
 * a pom or a manifest -- it works off the module graph and the package naming
 * convention. It is, however, specific to languages whose directory layout
 * mirrors their namespace, which is why a build system is allowed to reject it
 * in [BuildSystemGenerator.validate] rather than being forced to support it.
 */
class SourceDependencyPlanner(
    private val baseDirectory: Path,
    private val sourceProjectLoader: SourceProjectLoader,
    private val sourceFileFinder: SourceFileFinder,
    private val onSourceModulesNotFound: (List<String>) -> Unit,
    private val onTargetModulesNotFound: (List<String>) -> Unit,
    private val onDuplicateTargetModules: (Set<String>) -> Unit
) {
    fun commands(project: Project): List<Command> =
        project.sourceDependencies.flatMap { sourceDependency ->
            commandsForSourceDependency(project, sourceDependency)
        }

    private fun commandsForSourceDependency(
        project: Project,
        sourceDependency: SourceDependency
    ): List<Command> {
        // Load source project metadata to determine package structure
        val sourceProject = sourceProjectLoader.loadProject(sourceDependency.sourceProjectPath)

        // Validate module mappings
        validateModuleMappings(sourceProject, project, sourceDependency.moduleMapping)

        // Build transformation map
        val transformations = buildTransformations(
            sourceProject,
            project,
            sourceDependency.moduleMapping
        )

        // Find all source files in mapped modules
        val sourceFiles = sourceFileFinder.findSourceFiles(
            sourceDependency.sourceProjectPath,
            baseDirectory,
            sourceProject,
            project,
            sourceDependency.moduleMapping
        )

        // Generate copy commands for each source file
        return sourceFiles.map { sourceFileInfo ->
            CopyAndTransformSourceFile(
                sourceFileInfo.sourcePath,
                sourceFileInfo.targetPath,
                transformations,
                sourceDependency.sourceProjectPath,
                sourceFileInfo.sourceModule
            )
        }
    }

    private fun validateModuleMappings(
        sourceProject: Project,
        targetProject: Project,
        moduleMapping: Map<String, String>
    ) {
        // Phase 1: Validate that source modules are exported
        val exports = sourceProject.exports
        val nonExportedModules = moduleMapping.keys.filterNot { it in exports }
        if (nonExportedModules.isNotEmpty()) {
            val sourceProjectName = (sourceProject.prefix + sourceProject.name).joinToString(".")
            val availableExports = if (exports.isEmpty()) "none" else exports.joinToString(", ")
            throw IllegalArgumentException(
                """
                Cannot import non-exported modules from source project: $sourceProjectName

                Non-exported modules: ${nonExportedModules.joinToString(", ")}
                Available exports: $availableExports

                Resolution:
                  1. Remove these modules from your moduleMapping, OR
                  2. Add them to the "exports" section in the project-specification.json of $sourceProjectName

                Note: Modules must be explicitly marked as exportable for other projects to import them.
                """.trimIndent()
            )
        }

        // Validate that source modules exist in source project
        val sourceModules = sourceProject.modules.keys
        val unmappedSourceModules = moduleMapping.keys.filterNot { it in sourceModules }
        if (unmappedSourceModules.isNotEmpty()) {
            onSourceModulesNotFound(unmappedSourceModules)
        }

        // Validate that target modules exist in target project
        val targetModules = targetProject.modules.keys
        val unmappedTargetModules = moduleMapping.values.filterNot { it in targetModules }
        if (unmappedTargetModules.isNotEmpty()) {
            onTargetModulesNotFound(unmappedTargetModules)
        }

        // Check for duplicate target modules (multiple source modules mapping to same target)
        val duplicateTargets = moduleMapping.values.groupingBy { it }.eachCount().filter { it.value > 1 }
        if (duplicateTargets.isNotEmpty()) {
            onDuplicateTargetModules(duplicateTargets.keys)
        }
    }

    private fun buildTransformations(
        sourceProject: Project,
        targetProject: Project,
        moduleMapping: Map<String, String>
    ): List<PackageTransformation> {
        // Direct module transformations (e.g., jvmspec.analysis -> inversion-guard.jvmspec.analysis)
        val directTransformations = moduleMapping.map { (sourceModule, targetModule) ->
            val sourceModuleParts = ModuleName.parts(sourceModule)
            val targetModuleParts = ModuleName.parts(targetModule)

            val sourcePackage = sourceProject.prefix +
                    sourceProject.name +
                    sourceModuleParts

            val targetPackage = targetProject.prefix +
                    targetProject.name +
                    targetModuleParts

            PackageTransformation(sourcePackage, targetPackage)
        }

        // Automatic transformations for common source dependencies
        // (e.g., both projects import di-contract from kotlin-reusable)
        val automaticTransformations = buildAutomaticTransformations(sourceProject, targetProject)

        return directTransformations + automaticTransformations
    }

    private fun buildAutomaticTransformations(
        sourceProject: Project,
        targetProject: Project
    ): List<PackageTransformation> {
        val transformations = mutableListOf<PackageTransformation>()

        // Build a map of source project path to module mappings for both projects
        val sourceDepsByPath = sourceProject.sourceDependencies.associateBy { it.sourceProjectPath }
        val targetDepsByPath = targetProject.sourceDependencies.associateBy { it.sourceProjectPath }

        // Find common source dependencies (e.g., both import from kotlin-reusable)
        val commonSourcePaths = sourceDepsByPath.keys.intersect(targetDepsByPath.keys)

        for (commonSourcePath in commonSourcePaths) {
            val sourceDep = sourceDepsByPath[commonSourcePath]!!
            val targetDep = targetDepsByPath[commonSourcePath]!!

            // Find modules that both projects import from this common source
            val sourceModules = sourceDep.moduleMapping.values.toSet()
            val targetModules = targetDep.moduleMapping.values.toSet()
            val commonModules = sourceModules.intersect(targetModules)

            // Create transformations for each common module
            for (commonModule in commonModules) {
                val moduleParts = ModuleName.parts(commonModule)

                val sourcePackage = sourceProject.prefix +
                        sourceProject.name +
                        moduleParts

                val targetPackage = targetProject.prefix +
                        targetProject.name +
                        moduleParts

                transformations.add(PackageTransformation(sourcePackage, targetPackage))
            }
        }

        return transformations
    }
}
