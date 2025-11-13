package com.seanshubin.project.generator.domain

data class GroupArtifactVersionScope(
    val group: String,
    val artifact: String,
    val version: String,
    val scope: String?
)
