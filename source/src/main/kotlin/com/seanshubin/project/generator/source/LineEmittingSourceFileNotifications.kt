package com.seanshubin.project.generator.source

import java.nio.file.Path

/**
 * Implementation of SourceFileNotifications that emits warnings and errors as lines.
 *
 * Formats structured event data into human-readable messages and passes them to
 * an injected emit function (typically System.err::println in production).
 */
class LineEmittingSourceFileNotifications(
    private val emit: (String) -> Unit
) : SourceFileNotifications {
    override fun pathNotDirectory(path: Path) {
        emit("Warning: Source module path is not a directory, skipping: $path")
    }

    override fun sourceFileNotFound(sourcePath: Path) {
        emit("Error: Source file not found: $sourcePath")
    }

    override fun fileTransformationError(sourcePath: Path, targetPath: Path, errorMessage: String) {
        emit("Error copying and transforming file from $sourcePath to $targetPath: $errorMessage")
    }
}
