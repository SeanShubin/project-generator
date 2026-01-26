package com.seanshubin.project.generator.domain

import com.seanshubin.project.generator.contract.FilesContract
import java.nio.file.Path

interface SourceFileFinder {
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

            if (!files.exists(moduleSourceRoot)) continue

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
