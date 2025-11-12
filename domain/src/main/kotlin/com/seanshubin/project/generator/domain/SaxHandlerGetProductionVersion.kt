package com.seanshubin.project.generator.domain

import org.xml.sax.helpers.DefaultHandler

class SaxHandlerGetProductionVersion : DefaultHandler() {
    val path = mutableListOf<String>()
    val versionPath = listOf("metadata", "versioning", "versions", "version")
    val versions = mutableListOf<String>()
    fun latestReleaseVersion(): String {
        val releases = versions.filter { VersionRules.isReleaseVersion(it) }
        val sortedReleases = releases.sortedWith(VersionRules.versionNumberComparator)
        return sortedReleases.last()
    }

    override fun startElement(uri: String, localName: String, qName: String, attributes: org.xml.sax.Attributes) {
        path.add(qName)
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
        val value = String(ch, start, length)
        if (path == versionPath) {
            versions.add(value)
        }
    }

    override fun endElement(uri: String, localName: String, qName: String) {
        path.removeLast()
    }
}
