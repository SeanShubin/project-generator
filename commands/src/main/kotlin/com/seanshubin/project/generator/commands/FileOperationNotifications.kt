package com.seanshubin.project.generator.commands

import java.nio.file.Path

/**
 * Notifications for file system operations.
 *
 * Emits structured events for file and directory operations,
 * enabling visibility into what the generator is doing.
 */
interface FileOperationNotifications {
    /**
     * Emitted when a file is created for the first time.
     *
     * @param path The path of the file that was created
     */
    fun fileCreated(path: Path)

    /**
     * Emitted when an existing file is overwritten.
     *
     * @param path The path of the file that was modified
     */
    fun fileModified(path: Path)

    /**
     * Emitted when a file would be overwritten but content is identical.
     *
     * @param path The path of the file that was unchanged
     */
    fun fileUnchanged(path: Path)

    /**
     * Emitted when a directory is created.
     *
     * @param path The path of the directory that was created
     */
    fun directoryCreated(path: Path)
}
