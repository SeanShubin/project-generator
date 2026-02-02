package com.seanshubin.project.generator.console

import com.seanshubin.project.generator.commands.Environment
import com.seanshubin.project.generator.commands.EnvironmentImpl
import com.seanshubin.project.generator.commands.FileOperationNotifications
import com.seanshubin.project.generator.commands.LineEmittingFileOperationNotifications
import com.seanshubin.project.generator.core.Project
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
import com.seanshubin.project.generator.maven.*
import com.seanshubin.project.generator.source.*
import com.seanshubin.project.generator.xml.*
import java.nio.file.Path

class ProjectDependencies(
    project: Project,
    baseDirectory: Path,
    private val integrations: Integrations
) {
    private val indent: (String) -> String = StringUtility.indent
    private val xmlRenderer: XmlRenderer = XmlRendererImpl(indent)
    private val httpClientFactory: HttpClientFactory = integrations.httpClientFactory
    private val httpClient = httpClientFactory.createHttpClient()
    private val http: Http = HttpImpl(httpClient)
    private val xmlParserFactory: XmlParserFactory = SaxParserFactoryImpl()
    private val notifications: Notifications = LineEmittingNotifications(integrations.emit)
    private val sourceFileNotifications: SourceFileNotifications =
        LineEmittingSourceFileNotifications(integrations.emitError)
    private val moduleMappingNotifications: ModuleMappingNotifications =
        LineEmittingModuleMappingNotifications(integrations.emitError)
    private val fileOperationNotifications: FileOperationNotifications =
        LineEmittingFileOperationNotifications(integrations.emit)
    private val versionLookup: VersionLookup =
        VersionLookupImpl(http, xmlParserFactory, notifications::lookupVersionEvent)
    private val mavenXmlNode: MavenXmlNode = MavenXmlNodeImpl(versionLookup)
    private val files: FilesContract = integrations.files
    private val sourceProjectLoader: SourceProjectLoader = SourceProjectLoaderImpl(files)
    private val sourceFileFinder: SourceFileFinder = SourceFileFinderImpl(files, sourceFileNotifications)
    private val generator: Generator = GeneratorImpl(
        xmlRenderer,
        baseDirectory,
        mavenXmlNode,
        sourceProjectLoader,
        sourceFileFinder,
        moduleMappingNotifications
    )
    private val createKeyStore: (Path) -> KeyValueStore = { path: Path -> JsonFileKeyValueStore(files, path) }
    private val environment: Environment =
        EnvironmentImpl(files, createKeyStore, sourceFileNotifications, fileOperationNotifications)
    val runner: ProjectRunner = ProjectRunner(generator, project, environment)
}
