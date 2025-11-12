package com.seanshubin.project.generator.console

import com.seanshubin.project.generator.contract.FilesContract
import com.seanshubin.project.generator.contract.FilesDelegate
import com.seanshubin.project.generator.domain.*
import java.net.http.HttpClient
import java.nio.file.Path

class ProjectDependencies(
    project: Project,
    baseDirectory: Path
) {
    private val indent: (String) -> String = StringUtility.indent
    private val xmlRenderer: XmlRenderer = XmlRendererImpl(indent)
    private val httpClient: HttpClient = HttpClient.newHttpClient()
    private val http: Http = HttpImpl(httpClient)
    private val notifications: Notifications = LineEmittingNotifications(System.out::println)
    private val versionLookup: VersionLookup = VersionLookupImpl(http,notifications::lookupVersionEvent)
    private val mavenXmlNode: MavenXmlNode = MavenXmlNodeImpl(versionLookup)
    private val generator: Generator = GeneratorImpl(xmlRenderer, baseDirectory, mavenXmlNode)
    private val files: FilesContract = FilesDelegate
    private val environment: Environment = EnvironmentImpl(files)
    val runner: ProjectRunner = ProjectRunner(generator, project, environment)
}
