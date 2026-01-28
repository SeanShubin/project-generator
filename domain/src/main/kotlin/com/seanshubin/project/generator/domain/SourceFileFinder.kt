package com.seanshubin.project.generator.domain

import com.seanshubin.project.generator.contract.FilesContract
import java.nio.file.Path

/**
 * Finds all source files in mapped modules that need to be copied and transformed.
 *
 * Walks the directory structure of mapped modules in the source project to identify
 * all source files (both main and test), and determines their corresponding target paths
 * in the target project.
 */
interface SourceFileFinder {
    /**
     * Finds all source files in the mapped modules.
     *
     * For each module mapping (source → target), walks both the main and test source trees
     * and creates a SourceFileInfo for each source file found, containing both the source
     * path and the calculated target path.
     *
     * Both src/main and src/test directories are walked. If either doesn't exist for a module,
     * it's silently skipped (this is normal - some modules may only have main code without tests).
     *
     * Warnings are printed to stderr for:
     * - Paths that exist but aren't directories (skipped)
     *
     * @param sourceProjectPath The absolute path to the external source project
     * @param targetProjectPath The absolute path to the target project being generated
     * @param sourceProject The source project metadata (for package structure)
     * @param targetProject The target project metadata (for package structure)
     * @param moduleMapping Map of source module names to target module names
     * @param language The source file language extension (e.g., "kotlin", "java")
     * @return List of all source files to be copied with their source and target paths (main and test)
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
    private val files: FilesContract,
    private val notifications: SourceFileNotifications
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
            // Find main source files
            findSourceFilesInSourceSet(
                sourceProjectPath, targetProjectPath,
                sourceProject, targetProject,
                sourceModule, targetModule,
                language, "main", sourceFiles
            )

            // Find test source files
            findSourceFilesInSourceSet(
                sourceProjectPath, targetProjectPath,
                sourceProject, targetProject,
                sourceModule, targetModule,
                language, "test", sourceFiles
            )
        }

        return sourceFiles
    }

    private fun findSourceFilesInSourceSet(
        sourceProjectPath: Path,
        targetProjectPath: Path,
        sourceProject: Project,
        targetProject: Project,
        sourceModule: String,
        targetModule: String,
        language: String,
        sourceSet: String,
        results: MutableList<SourceFileInfo>
    ) {
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
            .resolve("src/$sourceSet/$language")
            .resolve(sourcePackagePath.joinToString("/"))

        val moduleTargetRoot = targetProjectPath
            .resolve(targetModule)
            .resolve("src/$sourceSet/$language")
            .resolve(targetPackagePath.joinToString("/"))

        if (!files.exists(moduleSourceRoot)) {
            // Module source directory doesn't exist - this might be intentional.
            // For example, a module might only have main code without tests, or vice versa.
            // Silently skip rather than warning.
            return
        }

        if (!files.isDirectory(moduleSourceRoot)) {
            notifications.pathNotDirectory(moduleSourceRoot)
            return
        }

        // Walk the source tree to find all source files
        walkDirectory(moduleSourceRoot, moduleTargetRoot, sourceModule, targetModule, language, results)
    }

    private fun walkDirectory(
        sourceRoot: Path,
        targetRoot: Path,
        sourceModule: String,
        targetModule: String,
        language: String,
        results: MutableList<SourceFileInfo>
    ) {
        val extension = languageToExtension(language)
        val entries = files.list(sourceRoot).toList()

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

    private fun languageToExtension(language: String): String {
        return when (language) {
            "kotlin" -> ".kt"
            "java" -> ".java"
            else -> ".$language"  // Fallback for unknown languages
        }
    }
}
