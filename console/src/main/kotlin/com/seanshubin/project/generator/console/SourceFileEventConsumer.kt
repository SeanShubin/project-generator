package com.seanshubin.project.generator.console

import java.nio.file.Path

/**
 * Consumes source file events and formats them for output.
 */
class SourceFileEventConsumer(private val emitError: (String) -> Unit) {
    fun onPathNotDirectory(path: Path) {
        emitError("Warning: Source module path is not a directory, skipping: $path")
    }

    fun onSourceFileNotFound(sourcePath: Path) {
        emitError("Error: Source file not found: $sourcePath")
    }

    fun onFileTransformationError(sourcePath: Path, targetPath: Path, errorMessage: String) {
        emitError("Error copying and transforming file from $sourcePath to $targetPath: $errorMessage")
    }
}
