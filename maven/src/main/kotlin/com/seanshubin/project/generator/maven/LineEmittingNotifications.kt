package com.seanshubin.project.generator.maven

import com.seanshubin.project.generator.core.GroupArtifactVersionScope

class LineEmittingNotifications(private val emit: (String) -> Unit) : Notifications {
    override fun lookupVersionEvent(uriString: String, dependency: GroupArtifactVersionScope) {
        emit("resolved ${dependency.group}:${dependency.artifact} -> ${dependency.version}")
    }
}
