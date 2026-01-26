package com.seanshubin.project.generator.domain

import java.nio.file.Path

/**
 * Information about a source file to be copied and transformed.
 *
 * Contains the paths and module names needed to copy a file from a source project
 * to a target project with appropriate transformations.
 *
 * @property sourcePath The absolute path to the source file in the external project
 * @property targetPath The absolute path where the file should be copied in the target project
 * @property sourceModule The name of the module in the source project (e.g., "source-module-f")
 * @property targetModule The name of the module in the target project (e.g., "target-module-j")
 */
data class SourceFileInfo(
    val sourcePath: Path,
    val targetPath: Path,
    val sourceModule: String,
    val targetModule: String
)
