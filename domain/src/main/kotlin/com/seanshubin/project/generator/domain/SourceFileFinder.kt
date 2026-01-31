package com.seanshubin.project.generator.domain

import com.seanshubin.project.generator.di.contract.FilesContract
import java.nio.file.Path

interface SourceFileFinder {
    fun findSourceFiles(
        sourceProjectPath: Path,
        targetProjectPath: Path,
        sourceProject: Project,
        targetProject: Project,
        moduleMapping: Map<String, String>
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
        moduleMapping: Map<String, String>
    ): List<SourceFileInfo> {
        val sourceFiles = mutableListOf<SourceFileInfo>()

        for ((sourceModule, targetModule) in moduleMapping) {
            findSourceFilesInSourceSet(
                sourceProjectPath, targetProjectPath,
                sourceProject, targetProject,
                sourceModule, targetModule,
                "main", sourceFiles
            )

            findSourceFilesInSourceSet(
                sourceProjectPath, targetProjectPath,
                sourceProject, targetProject,
                sourceModule, targetModule,
                "test", sourceFiles
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

        val sourceSetDir = sourceProjectPath
            .resolve(sourceModule)
            .resolve("src/$sourceSet")

        if (!files.exists(sourceSetDir)) {
            return
        }

        if (!files.isDirectory(sourceSetDir)) {
            notifications.pathNotDirectory(sourceSetDir)
            return
        }

        val languageDirs = files.list(sourceSetDir).toList().filter { files.isDirectory(it) }

        for (languageDir in languageDirs) {
            val language = languageDir.fileName.toString()

            val moduleSourceRoot = languageDir
                .resolve(sourcePackagePath.joinToString("/"))

            val moduleTargetRoot = targetProjectPath
                .resolve(targetModule)
                .resolve("src/$sourceSet/$language")
                .resolve(targetPackagePath.joinToString("/"))

            if (files.exists(moduleSourceRoot) && files.isDirectory(moduleSourceRoot)) {
                walkDirectory(moduleSourceRoot, moduleTargetRoot, sourceModule, targetModule, results)
            }
        }
    }

    private fun walkDirectory(
        sourceRoot: Path,
        targetRoot: Path,
        sourceModule: String,
        targetModule: String,
        results: MutableList<SourceFileInfo>
    ) {
        val entries = files.list(sourceRoot).toList()

        entries.forEach { entry ->
            val relativePath = sourceRoot.relativize(entry)
            val targetPath = targetRoot.resolve(relativePath)

            if (files.isDirectory(entry)) {
                walkDirectory(entry, targetPath.parent.resolve(relativePath.fileName), sourceModule, targetModule, results)
            } else {
                results.add(SourceFileInfo(entry, targetPath, sourceModule, targetModule))
            }
        }
    }

    private fun parseModuleName(module: String): List<String> {
        val moduleSeparator = "-"
        return module.split(moduleSeparator)
    }
}
