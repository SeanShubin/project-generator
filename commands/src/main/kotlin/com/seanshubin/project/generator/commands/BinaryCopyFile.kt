package com.seanshubin.project.generator.commands

import java.nio.file.Path
import java.util.Arrays

data class BinaryCopyFile(
    val sourcePath: Path,
    val targetPath: Path
) : Command {
    override fun execute(environment: Environment) {
        try {
            if (!environment.files.exists(sourcePath)) {
                environment.onSourceFileNotFound(sourcePath)
                return
            }

            val sourceBytes = environment.files.readAllBytes(sourcePath)

            val parent = targetPath.parent
            if (parent != null) {
                environment.files.createDirectories(parent)
            }

            val existed = environment.files.exists(targetPath)
            if (existed) {
                val existingBytes = environment.files.readAllBytes(targetPath)
                if (Arrays.equals(existingBytes, sourceBytes)) {
                    environment.onFileUnchanged(targetPath)
                    return
                }
                environment.files.write(targetPath, sourceBytes)
                environment.onFileModified(targetPath)
            } else {
                environment.files.write(targetPath, sourceBytes)
                environment.onFileCreated(targetPath)
            }
        } catch (e: Exception) {
            environment.onFileTransformationError(sourcePath, targetPath, e.message ?: "Unknown error")
            throw e
        }
    }
}
