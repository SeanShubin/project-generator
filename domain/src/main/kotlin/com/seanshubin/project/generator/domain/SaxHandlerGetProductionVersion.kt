package com.seanshubin.project.generator.domain

import org.xml.sax.helpers.DefaultHandler

class SaxHandlerGetProductionVersion : DefaultHandler() {
    val path = mutableListOf<String>()
    val releasePath = listOf("metadata", "versioning", "release")
    var releaseVersion: String? = null
    override fun startElement(uri: String, localName: String, qName: String, attributes: org.xml.sax.Attributes) {
        path.add(qName)
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
        val value = String(ch, start, length)
        if (path == releasePath) {
            if (releaseVersion == null) {
                releaseVersion = value
            } else {
                throw RuntimeException("Multiple updates to release version: '$releaseVersion', '$value'")
            }
        }
    }

    override fun endElement(uri: String, localName: String, qName: String) {
        path.removeLast()
    }
}
