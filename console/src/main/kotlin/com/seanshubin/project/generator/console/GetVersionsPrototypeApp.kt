package com.seanshubin.project.generator.console

import org.xml.sax.helpers.DefaultHandler
import java.io.InputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory

object GetVersionsPrototypeApp {
    @JvmStatic
    fun main(args: Array<String>) {
        val group = "io.arrow-kt"
        val artifact = "arrow-core"
        val groupPath = group.replace(".", "/")
        val uri = "https://repo1.maven.org/maven2/$groupPath/$artifact/maven-metadata.xml"
        println(uri)
        val httpClient = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder().uri(URI.create(uri)).build()
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream())

        val statusCode = response.statusCode()
        if(!isSuccess(statusCode)){
            throw RuntimeException("Unexpected status code $statusCode")
        }
        val body = response.body()
        val saxParserFactory = SAXParserFactory.newInstance()
        val saxParser: SAXParser = saxParserFactory.newSAXParser()
        val saxParserHandler = VersionHandler()
        saxParser.parse(body, saxParserHandler)
        saxParserHandler.lines().forEach(::println)
    }
    fun isSuccess(statusCode:Int):Boolean = statusCode in 200..299
    class VersionHandler: DefaultHandler() {
        val path = mutableListOf<String>()
        val latestPath = listOf("metadata", "versioning", "latest")
        val releasePath = listOf("metadata", "versioning", "release")
        val lastUpdatedPath = listOf("metadata", "versioning", "lastUpdated")
        val versionPath = listOf("metadata", "versioning", "versions", "version")
        var latestVersion:String? = null
        var releaseVersion:String? = null
        var lastUpdated:String? = null
        val versions = mutableListOf<String>()
        fun lines():List<String>{
            return listOf(
                "latest version: $latestVersion",
                "release version: $releaseVersion",
                "last updated: $lastUpdated",
                "versions(${versions.size})"
            ) + versions.map { "  $it" }
        }
        override fun startElement(uri: String, localName: String, qName: String, attributes: org.xml.sax.Attributes) {
            path.add(qName)
        }

        override fun characters(ch: CharArray, start: Int, length: Int) {
            val value = String(ch, start, length)
            if(path == versionPath){
                versions.add(value)
            }
            if(path == latestPath) {
                latestVersion = updateOnce("latest version",latestVersion, value)
            }
            if(path == releasePath) {
                releaseVersion = updateOnce("release version",releaseVersion, value)
            }
            if(path == lastUpdatedPath) {
                lastUpdated = updateOnce("last updated",lastUpdated, value)
            }
        }

        override fun endElement(uri: String, localName: String, qName: String) {
            path.removeLast()
        }

        fun updateOnce(caption:String, current:String?, new:String):String =
            if(current == null) new else throw RuntimeException("Multiple updates to $caption: '$current', '$new'")
    }
}