package com.seanshubin.project.generator.domain

import javax.xml.parsers.SAXParserFactory

class VersionLookupImpl(private val http: Http):VersionLookup {
    override fun latestProductionVersion(group: String, artifact: String): String {
        val groupPath = group.replace(".", "/")
        val uri = "https://repo1.maven.org/maven2/$groupPath/$artifact/maven-metadata.xml"
        val xmlText = http.getAssertSuccess(uri)
        val handler = SaxHandlerGetProductionVersion()
        val saxParserFactory = SAXParserFactory.newInstance()
        val saxParser = saxParserFactory.newSAXParser()
        saxParser.parse(xmlText.byteInputStream(), handler)
        return handler.releaseVersion!!
    }
}
