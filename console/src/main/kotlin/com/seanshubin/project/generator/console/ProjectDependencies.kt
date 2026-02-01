package com.seanshubin.project.generator.console

import com.seanshubin.project.generator.commands.Environment
import com.seanshubin.project.generator.commands.EnvironmentImpl
import com.seanshubin.project.generator.core.*
import com.seanshubin.project.generator.di.contract.FilesContract
import com.seanshubin.project.generator.di.delegate.FilesDelegate
import com.seanshubin.project.generator.dynamic.core.KeyValueStore
import com.seanshubin.project.generator.dynamic.json.JsonFileKeyValueStore
import com.seanshubin.project.generator.generator.Generator
import com.seanshubin.project.generator.generator.GeneratorImpl
import com.seanshubin.project.generator.generator.ProjectRunner
import com.seanshubin.project.generator.http.Http
import com.seanshubin.project.generator.http.HttpClientFactory
import com.seanshubin.project.generator.http.HttpClientFactoryImpl
import com.seanshubin.project.generator.http.HttpImpl
import com.seanshubin.project.generator.maven.LineEmittingNotifications
import com.seanshubin.project.generator.maven.MavenXmlNode
import com.seanshubin.project.generator.maven.MavenXmlNodeImpl
import com.seanshubin.project.generator.maven.Notifications
import com.seanshubin.project.generator.maven.VersionLookup
import com.seanshubin.project.generator.maven.VersionLookupImpl
import com.seanshubin.project.generator.source.LineEmittingModuleMappingNotifications
import com.seanshubin.project.generator.source.LineEmittingSourceFileNotifications
import com.seanshubin.project.generator.source.ModuleMappingNotifications
import com.seanshubin.project.generator.source.SourceFileFinder
import com.seanshubin.project.generator.source.SourceFileFinderImpl
import com.seanshubin.project.generator.source.SourceFileNotifications
import com.seanshubin.project.generator.source.SourceProjectLoader
import com.seanshubin.project.generator.source.SourceProjectLoaderImpl
import com.seanshubin.project.generator.xml.SaxParserFactoryImpl
import com.seanshubin.project.generator.xml.StringUtility
import com.seanshubin.project.generator.xml.XmlParserFactory
import com.seanshubin.project.generator.xml.XmlRenderer
import com.seanshubin.project.generator.xml.XmlRendererImpl
import java.nio.file.Path

class ProjectDependencies(
    project: Project,
    baseDirectory: Path
) {
    private val indent: (String) -> String = StringUtility.indent
    private val xmlRenderer: XmlRenderer = XmlRendererImpl(indent)
    private val httpClientFactory: HttpClientFactory = HttpClientFactoryImpl()
    private val httpClient = httpClientFactory.createHttpClient()
    private val http: Http = HttpImpl(httpClient)
    private val xmlParserFactory: XmlParserFactory = SaxParserFactoryImpl()
    private val notifications: Notifications = LineEmittingNotifications(System.out::println)
    private val sourceFileNotifications: SourceFileNotifications = LineEmittingSourceFileNotifications(System.err::println)
    private val moduleMappingNotifications: ModuleMappingNotifications = LineEmittingModuleMappingNotifications(System.err::println)
    private val versionLookup: VersionLookup = VersionLookupImpl(http, xmlParserFactory, notifications::lookupVersionEvent)
    private val mavenXmlNode: MavenXmlNode = MavenXmlNodeImpl(versionLookup)
    private val files: FilesContract = FilesDelegate.defaultInstance()
    private val sourceProjectLoader: SourceProjectLoader = SourceProjectLoaderImpl(files)
    private val sourceFileFinder: SourceFileFinder = SourceFileFinderImpl(files, sourceFileNotifications)
    private val generator: Generator = GeneratorImpl(xmlRenderer, baseDirectory, mavenXmlNode, sourceProjectLoader, sourceFileFinder, moduleMappingNotifications)
    private val createKeyStore:(Path)-> KeyValueStore = {path:Path -> JsonFileKeyValueStore(files, path) }
    private val environment: Environment = EnvironmentImpl(files, createKeyStore, sourceFileNotifications)
    val runner: ProjectRunner = ProjectRunner(generator, project, environment)
}
