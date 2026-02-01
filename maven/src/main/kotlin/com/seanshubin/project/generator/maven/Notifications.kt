package com.seanshubin.project.generator.maven

import com.seanshubin.project.generator.core.GroupArtifactVersionScope

interface Notifications {
    fun lookupVersionEvent(uriString: String, dependency: GroupArtifactVersionScope)
}
