package com.seanshubin.project.generator.domain

interface Notifications {
    fun lookupVersionEvent(uriString: String, dependency: GroupArtifactVersion)
}
