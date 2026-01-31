package com.seanshubin.project.generator.console

import com.seanshubin.project.generator.dynamic.json.JsonFileKeyValueStore
import com.seanshubin.project.generator.dynamic.core.KeyValueStore
import com.seanshubin.project.generator.di.contract.FilesContract
import com.seanshubin.project.generator.di.delegate.FilesDelegate
import com.seanshubin.project.generator.domain.*
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
