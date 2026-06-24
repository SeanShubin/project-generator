package com.seanshubin.project.generator.maven

import com.seanshubin.project.generator.core.Developer
import com.seanshubin.project.generator.core.Project
import com.seanshubin.project.generator.xml.StringUtility
import com.seanshubin.project.generator.xml.XmlRendererImpl
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MavenXmlNodeImplTest {
    private val stubVersionLookup = object : VersionLookup {
        override fun latestProductionVersion(group: String, artifact: String) = "1.0.0"
    }
    private val xmlRenderer = XmlRendererImpl(StringUtility.indent)
    private val mavenXmlNode = MavenXmlNodeImpl(stubVersionLookup)

    @Test
    fun rootPomOmitsDeveloperSectionsWhenDeveloperIsNull() {
        val project = Project(
            prefix = listOf("com", "example"),
            name = listOf("sample"),
            description = "Sample Project",
            version = "1.0.0",
            language = "kotlin",
            developer = null,
            dependencies = emptyMap(),
            versionOverrides = emptyList(),
            global = emptyList(),
            modules = emptyMap(),
            javaVersion = "21"
        )
        val xml = mavenXmlNode.generateRootXml(project)
        val content = xmlRenderer.toLines(xml).joinToString("\n")
        assertFalse(content.contains("<url>"), "url element should be absent when developer is null")
        assertFalse(content.contains("<developers>"), "developers element should be absent when developer is null")
        assertFalse(content.contains("<scm>"), "scm element should be absent when developer is null")
    }

    @Test
    fun rootPomIncludesDeveloperSectionsWhenDeveloperIsPresent() {
        val developer = Developer(
            name = "Test Dev",
            githubName = "testdev",
            mavenUserName = "testdev",
            organization = "Test Org",
            url = "https://example.com"
        )
        val project = Project(
            prefix = listOf("com", "example"),
            name = listOf("sample"),
            description = "Sample Project",
            version = "1.0.0",
            language = "kotlin",
            developer = developer,
            dependencies = emptyMap(),
            versionOverrides = emptyList(),
            global = emptyList(),
            modules = emptyMap(),
            javaVersion = "21"
        )
        val xml = mavenXmlNode.generateRootXml(project)
        val content = xmlRenderer.toLines(xml).joinToString("\n")
        assertTrue(content.contains("<url>"), "url element should be present when developer is set")
        assertTrue(content.contains("<developers>"), "developers element should be present when developer is set")
        assertTrue(content.contains("<scm>"), "scm element should be present when developer is set")
    }
}
