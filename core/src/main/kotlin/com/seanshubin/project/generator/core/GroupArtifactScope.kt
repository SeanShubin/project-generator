package com.seanshubin.project.generator.core

data class GroupArtifactScope(
    val group: String,            // maven group id
    val artifact: String,         // maven artifact id
    val scope: String?            // defaults to omitted, which in maven defaults to compiled
)
