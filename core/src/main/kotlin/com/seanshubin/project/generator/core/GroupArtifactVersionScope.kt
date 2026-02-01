package com.seanshubin.project.generator.core

data class GroupArtifactVersionScope(
    val group: String,
    val artifact: String,
    val version: String,
    val scope: String?
)
