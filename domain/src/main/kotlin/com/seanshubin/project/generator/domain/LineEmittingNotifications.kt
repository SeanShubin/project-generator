package com.seanshubin.project.generator.domain

class LineEmittingNotifications(private val emit:(String)->Unit):Notifications {
    override fun lookupVersionEvent(uriString: String, dependency: GroupArtifactVersion) {
        emit("group:${dependency.group} artifact:${dependency.artifact} version:${dependency.version} uri:$uriString")
    }
}
