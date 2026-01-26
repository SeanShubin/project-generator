package com.seanshubin.project.generator.domain

import java.nio.file.Path

data class SourceDependency(
    val sourceProjectPath: Path,
    val moduleMapping: Map<String, String>
)
