package com.seanshubin.project.generator.commands

import java.nio.file.Path

data class CopyAndTransformTextFile(
    val sourcePath: Path,
    val targetPath: Path,
    val replacements: List<Pair<String, String>>
) : Command {
    override fun execute(environment: Environment) {
        try {
            if (!environment.files.exists(sourcePath)) {
                environment.onSourceFileNotFound(sourcePath)
                return
            }

            val sourceText = environment.files.readString(sourcePath)
            val transformedText = replacements.fold(sourceText) { acc, (old, new) -> acc.replace(old, new) }

            val parent = targetPath.parent
            if (parent != null) {
                environment.files.createDirectories(parent)
            }

            val existed = environment.files.exists(targetPath)
            if (existed) {
                val existingText = environment.files.readString(targetPath)
                if (existingText == transformedText) {
                    environment.onFileUnchanged(targetPath)
                    return
                }
                environment.files.writeString(targetPath, transformedText)
                environment.onFileModified(targetPath)
            } else {
                environment.files.writeString(targetPath, transformedText)
                environment.onFileCreated(targetPath)
            }
        } catch (e: Exception) {
            environment.onFileTransformationError(sourcePath, targetPath, e.message ?: "Unknown error")
            throw e
        }
    }
}
