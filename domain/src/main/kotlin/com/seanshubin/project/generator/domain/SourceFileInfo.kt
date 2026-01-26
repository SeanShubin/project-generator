package com.seanshubin.project.generator.domain

import java.nio.file.Path

data class SourceFileInfo(
    val sourcePath: Path,
    val targetPath: Path,
    val sourceModule: String,
    val targetModule: String
)
