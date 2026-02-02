package com.seanshubin.project.generator.console

import java.nio.file.Path

/**
 * Consumes file operation events and formats them for output.
 */
class FileOperationEventConsumer(private val emit: (String) -> Unit) {
    fun onFileCreated(path: Path) {
        emit("created $path")
    }

    fun onFileModified(path: Path) {
        emit("modified $path")
    }

    fun onFileUnchanged(path: Path) {
        emit("unchanged $path")
    }

    fun onDirectoryCreated(path: Path) {
        emit("directory created $path")
    }
}
