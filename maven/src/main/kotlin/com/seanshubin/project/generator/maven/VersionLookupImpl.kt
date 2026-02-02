package com.seanshubin.project.generator.maven

import com.seanshubin.project.generator.core.GroupArtifactVersionScope
import com.seanshubin.project.generator.http.Http
import com.seanshubin.project.generator.xml.XmlParserFactory

class VersionLookupImpl(
    private val http: Http,
    private val xmlParserFactory: XmlParserFactory,
    private val lookupVersionEvent: (String, GroupArtifactVersionScope) -> Unit
) : VersionLookup {
    override fun latestProductionVersion(group: String, artifact: String): String {
        val dotSeparator = "."
        val pathSeparator = "/"
        val groupPath = group.replace(dotSeparator, pathSeparator)
        val uri = "https://repo1.maven.org/maven2/$groupPath/$artifact/maven-metadata.xml"
        val xmlText = http.getAssertSuccess(uri)
        val handler = SaxHandlerGetProductionVersion()
        val saxParser = xmlParserFactory.createParser()
        saxParser.parse(xmlText.byteInputStream(), handler)
        val releaseVersion = handler.latestReleaseVersion()
        val dependency = GroupArtifactVersionScope(group, artifact, releaseVersion, scope = null)
        lookupVersionEvent(uri, dependency)
        return releaseVersion
    }
}
