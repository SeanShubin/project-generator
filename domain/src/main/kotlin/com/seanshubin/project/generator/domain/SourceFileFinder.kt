package com.seanshubin.project.generator.domain

import com.seanshubin.project.generator.contract.FilesContract
import java.nio.file.Path

/**
 * Finds all source files in mapped modules that need to be copied and transformed.
 *
 * Walks the directory structure of mapped modules in the source project to identify
 * all source files, and determines their corresponding target paths in the target project.
 */
interface SourceFileFinder {
    /**
     * Finds all source files in the mapped modules.
     *
     * For each module mapping (source → target), walks the source module's directory tree
     * and creates a SourceFileInfo for each source file found, containing both the source
     * path and the calculated target path.
     *
     * Warnings are printed to stderr for:
     * - Source module directories that don't exist (skipped)
     * - Paths that exist but aren't directories (skipped)
     *
     * @param sourceProjectPath The absolute path to the external source project
     * @param targetProjectPath The absolute path to the target project being generated
     * @param sourceProject The source project metadata (for package structure)
     * @param targetProject The target project metadata (for package structure)
     * @param moduleMapping Map of source module names to target module names
     * @param language The source file language extension (e.g., "kotlin", "java")
     * @return List of all source files to be copied with their source and target paths
     */
    fun findSourceFiles(
        sourceProjectPath: Path,
        targetProjectPath: Path,
        sourceProject: Project,
        targetProject: Project,
        moduleMapping: Map<String, String>,
        language: String
    ): List<SourceFileInfo>
}

class SourceFileFinderImpl(
    private val files: FilesContract
) : SourceFileFinder {
    override fun findSourceFiles(
        sourceProjectPath: Path,
        targetProjectPath: Path,
        sourceProject: Project,
        targetProject: Project,
        moduleMapping: Map<String, String>,
        language: String
    ): List<SourceFileInfo> {
        val sourceFiles = mutableListOf<SourceFileInfo>()

        for ((sourceModule, targetModule) in moduleMapping) {
            val sourceModuleParts = parseModuleName(sourceModule)
            val targetModuleParts = parseModuleName(targetModule)

            val sourcePackagePath = sourceProject.prefix +
                    sourceProject.name +
                    sourceModuleParts

            val targetPackagePath = targetProject.prefix +
                    targetProject.name +
                    targetModuleParts

            val moduleSourceRoot = sourceProjectPath
                .resolve(sourceModule)
                .resolve("src/main/$language")
                .resolve(sourcePackagePath.joinToString("/"))

            val moduleTargetRoot = targetProjectPath
                .resolve(targetModule)
                .resolve("src/main/$language")
                .resolve(targetPackagePath.joinToString("/"))

            if (!files.exists(moduleSourceRoot)) {
                // Module source directory doesn't exist - this might be intentional if the module
                // only has test code or generated code. Log a warning but continue.
                System.err.println("Warning: Source module directory not found, skipping: $moduleSourceRoot")
                continue
            }

            if (!files.isDirectory(moduleSourceRoot)) {
                System.err.println("Warning: Source module path is not a directory, skipping: $moduleSourceRoot")
                continue
            }

            // Walk the source tree to find all source files
            walkDirectory(moduleSourceRoot, moduleTargetRoot, sourceModule, targetModule, language, sourceFiles)
        }

        return sourceFiles
    }

    private fun walkDirectory(
        sourceRoot: Path,
        targetRoot: Path,
        sourceModule: String,
        targetModule: String,
        language: String,
        results: MutableList<SourceFileInfo>
    ) {
        val extension = ".$language"
        val entries = files.list(sourceRoot)

        entries.forEach { entry ->
            val relativePath = sourceRoot.relativize(entry)
            val targetPath = targetRoot.resolve(relativePath)

            if (files.isDirectory(entry)) {
                // Recursively walk subdirectories
                walkDirectory(entry, targetPath.parent.resolve(relativePath.fileName), sourceModule, targetModule, language, results)
            } else if (entry.toString().endsWith(extension)) {
                // Add source file to results
                results.add(SourceFileInfo(entry, targetPath, sourceModule, targetModule))
            }
        }
    }

    private fun parseModuleName(module: String): List<String> {
        val moduleSeparator = "-"
        return module.split(moduleSeparator)
    }
}
