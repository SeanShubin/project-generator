package com.seanshubin.project.generator.domain

import java.nio.file.Path

/**
 * Notifications for source file operations.
 *
 * Emits structured events for source file discovery and processing,
 * enabling testability and redirection of warnings/errors.
 */
interface SourceFileNotifications {
    /**
     * Emitted when a path exists but is not a directory.
     *
     * @param path The path that is not a directory
     */
    fun pathNotDirectory(path: Path)

    /**
     * Emitted when a source file is not found.
     *
     * @param sourcePath The source file path that was not found
     */
    fun sourceFileNotFound(sourcePath: Path)

    /**
     * Emitted when an error occurs copying and transforming a file.
     *
     * @param sourcePath The source file path
     * @param targetPath The target file path
     * @param errorMessage The error message
     */
    fun fileTransformationError(sourcePath: Path, targetPath: Path, errorMessage: String)
}
