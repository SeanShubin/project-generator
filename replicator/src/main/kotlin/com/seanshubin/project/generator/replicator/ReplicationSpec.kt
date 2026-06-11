package com.seanshubin.project.generator.replicator

import java.nio.file.Path

data class ReplicationSpec(
    val sourceDirectory: Path,
    val newPrefix: List<String>,
    val generateCodeStructure: Boolean? = null,
    val verbatimPaths: List<String> = emptyList(),
    val developer: Any? = DEVELOPER_ABSENT
) {
    companion object {
        val DEVELOPER_ABSENT: Any = Any()
    }
}
