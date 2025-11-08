package com.seanshubin.project.generator.domain

data class Dependency(
    val group: String,            // maven group id
    val artifact: String,         // maven artifact id
    val lockedAtVersion: String?, // defaults to latest version, only specify this if you want to hold the version back
    val scope: String?            // defaults to omitted, which in maven defaults to compiled
)
