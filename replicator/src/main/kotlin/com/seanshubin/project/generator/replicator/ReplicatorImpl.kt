package com.seanshubin.project.generator.replicator

import com.seanshubin.project.generator.commands.*
import com.seanshubin.project.generator.source.PackageTransformation
import com.seanshubin.project.generator.source.SourceProjectLoader
import java.nio.file.FileVisitOption
import java.util.stream.Stream
import java.nio.file.Files
import java.nio.file.Path

class ReplicatorImpl(
    private val sourceProjectLoader: SourceProjectLoader,
    private val fileClassifier: FileClassifier
) : Replicator {
    override fun replicate(spec: ReplicationSpec, destination: Path): List<Command> {
        val sourceProject = sourceProjectLoader.loadProject(spec.sourceDirectory)
        val oldPkgParts = sourceProject.prefix + sourceProject.name
        val newPkgParts = spec.newPrefix + sourceProject.name
        val transformation = PackageTransformation(oldPkgParts, newPkgParts)
        val oldDot = oldPkgParts.joinToString(".")
        val newDot = newPkgParts.joinToString(".")
        val oldSlash = oldPkgParts.joinToString("/")
        val newSlash = newPkgParts.joinToString("/")
        val textReplacements = listOf(oldDot to newDot, oldSlash to newSlash)
        val generateCodeStructure = spec.generateCodeStructure ?: sourceProject.generateCodeStructure
        val rootPomReplacements = if (!generateCodeStructure)
            textReplacements + codeStructurePluginReplacement(spec.sourceDirectory)
        else textReplacements

        val gitIgnoreFilter = loadGitIgnoreFilter(spec.sourceDirectory)

        return Files.walk(spec.sourceDirectory, FileVisitOption.FOLLOW_LINKS)
            .filter { Files.isRegularFile(it) }
            .flatMap { sourcePath ->
                val relative = spec.sourceDirectory.relativize(sourcePath).toString().replace("\\", "/")
                if (gitIgnoreFilter.isIgnored(relative)) return@flatMap Stream.empty()
                val transformedRelative = relative.replace(oldSlash, newSlash)
                val targetPath = destination.resolve(transformedRelative)
                val effectiveTextReplacements = if (relative == "pom.xml") rootPomReplacements else textReplacements
                commandsFor(
                    sourcePath, targetPath, relative, effectiveTextReplacements,
                    listOf(transformation), spec.newPrefix, generateCodeStructure
                ).stream()
            }
            .toList()
    }

    private fun loadGitIgnoreFilter(sourceDirectory: Path): GitIgnoreFilter {
        val gitIgnorePath = sourceDirectory.resolve(".gitignore")
        val patterns = if (Files.exists(gitIgnorePath)) Files.readAllLines(gitIgnorePath) else emptyList()
        return GitIgnoreFilter(patterns)
    }

    private fun codeStructurePluginReplacement(sourceDirectory: Path): List<Pair<String, String>> {
        val rootPom = sourceDirectory.resolve("pom.xml")
        if (!Files.exists(rootPom)) return emptyList()
        val content = Files.readString(rootPom)
        val block = extractCodeStructurePluginBlock(content) ?: return emptyList()
        return listOf(block to "")
    }

    private fun extractCodeStructurePluginBlock(content: String): String? {
        val marker = "<groupId>com.seanshubin.code.structure</groupId>"
        val markerIdx = content.indexOf(marker)
        if (markerIdx < 0) return null
        val pluginOpen = "<plugin>"
        val pluginClose = "</plugin>"
        val start = content.lastIndexOf(pluginOpen, markerIdx)
        val end = content.indexOf(pluginClose, markerIdx) + pluginClose.length
        if (start < 0 || end < pluginClose.length) return null
        return content.substring(start, end)
    }

    private fun commandsFor(
        sourcePath: Path,
        targetPath: Path,
        relativePath: String,
        textReplacements: List<Pair<String, String>>,
        transformations: List<PackageTransformation>,
        newPrefix: List<String>,
        generateCodeStructure: Boolean
    ): List<Command> {
        return when (fileClassifier.classify(relativePath)) {
            FileClass.Skip -> emptyList()
            FileClass.SourceTransform -> listOf(ReplicateSourceFile(sourcePath, targetPath, transformations))
            FileClass.TextTransform -> {
                val cmds = mutableListOf<Command>(CopyAndTransformTextFile(sourcePath, targetPath, textReplacements))
                if (sourcePath.fileName.toString() == "project-specification.json") {
                    cmds += SetJsonConfig(targetPath, newPrefix, listOf("prefix"))
                    cmds += SetJsonConfig(targetPath, generateCodeStructure, listOf("generateCodeStructure"))
                }
                cmds
            }
            FileClass.BinaryCopy -> listOf(BinaryCopyFile(sourcePath, targetPath))
        }
    }
}
