package com.seanshubin.project.generator.domain

interface VersionLookup {
    fun latestProductionVersion(group: String, artifact: String): String
}
