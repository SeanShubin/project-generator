package com.seanshubin.project.generator.commands

import java.nio.file.Path

/**
 * Implementation of FileOperationNotifications that emits concise status messages.
 *
 * Formats file operations into brief, clear messages:
 * - "created <path>" for new files
 * - "modified <path>" for overwritten files
 * - "unchanged <path>" for files with identical content
 * - "created-dir <path>" for new directories
 */
class LineEmittingFileOperationNotifications(
    private val emit: (String) -> Unit
) : FileOperationNotifications {
    override fun fileCreated(path: Path) {
        emit("created $path")
    }

    override fun fileModified(path: Path) {
        emit("modified $path")
    }

    override fun fileUnchanged(path: Path) {
        emit("unchanged $path")
    }

    override fun directoryCreated(path: Path) {
        emit("created-dir $path")
    }
}
