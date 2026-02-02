package com.seanshubin.project.generator.console

import com.seanshubin.project.generator.core.GroupArtifactVersionScope

/**
 * Consumes Maven lookup events and formats them for output.
 */
class MavenEventConsumer(private val emit: (String) -> Unit) {
    fun onLookupVersion(uri: String, dependency: GroupArtifactVersionScope) {
        emit("group:${dependency.group} artifact:${dependency.artifact} version:${dependency.version} uri:$uri")
    }
}
