package com.seanshubin.project.generator.maven

interface VersionLookup {
    fun latestProductionVersion(group: String, artifact: String): String
}
