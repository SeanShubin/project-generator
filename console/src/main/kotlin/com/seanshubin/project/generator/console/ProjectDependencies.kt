package com.seanshubin.project.generator.console

import com.seanshubin.project.generator.commands.Environment
import com.seanshubin.project.generator.commands.EnvironmentImpl
import com.seanshubin.project.generator.core.Project
import com.seanshubin.project.generator.di.contract.FilesContract
import com.seanshubin.project.generator.generator.Generator
import com.seanshubin.project.generator.generator.GeneratorImpl
import com.seanshubin.project.generator.generator.ProjectRunner
import com.seanshubin.project.generator.http.Http
import com.seanshubin.project.generator.http.HttpClientFactory
import com.seanshubin.project.generator.http.HttpImpl
import com.seanshubin.project.generator.maven.MavenXmlNode
import com.seanshubin.project.generator.maven.MavenXmlNodeImpl
import com.seanshubin.project.generator.maven.VersionLookup
import com.seanshubin.project.generator.maven.VersionLookupImpl
import com.seanshubin.project.generator.source.SourceFileFinder
import com.seanshubin.project.generator.source.SourceFileFinderImpl
import com.seanshubin.project.generator.source.SourceProjectLoader
import com.seanshubin.project.generator.source.SourceProjectLoaderImpl
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
    private val mavenEventConsumer = MavenEventConsumer(integrations.emit)
    private val sourceFileEventConsumer = SourceFileEventConsumer(integrations.emitError)
    private val moduleMappingEventConsumer = ModuleMappingEventConsumer(integrations.emitError)
    private val fileOperationEventConsumer = FileOperationEventConsumer(integrations.emit)
    private val versionLookup: VersionLookup =
        VersionLookupImpl(http, xmlParserFactory, mavenEventConsumer::onLookupVersion)
    private val mavenXmlNode: MavenXmlNode = MavenXmlNodeImpl(versionLookup)
    private val files: FilesContract = integrations.files
    private val sourceProjectLoader: SourceProjectLoader = SourceProjectLoaderImpl(files)
    private val sourceFileFinder: SourceFileFinder =
        SourceFileFinderImpl(files, sourceFileEventConsumer::onPathNotDirectory)
    private val generator: Generator = GeneratorImpl(
        xmlRenderer,
        baseDirectory,
        mavenXmlNode,
        sourceProjectLoader,
        sourceFileFinder,
        moduleMappingEventConsumer::onSourceModulesNotFound,
        moduleMappingEventConsumer::onTargetModulesNotFound,
        moduleMappingEventConsumer::onDuplicateTargetModules
    )
    private val keyValueStoreFactory = JsonFileKeyValueStoreFactory(files)
    private val environment: Environment =
        EnvironmentImpl(
            files,
            keyValueStoreFactory::create,
            sourceFileEventConsumer::onPathNotDirectory,
            sourceFileEventConsumer::onSourceFileNotFound,
            sourceFileEventConsumer::onFileTransformationError,
            fileOperationEventConsumer::onFileCreated,
            fileOperationEventConsumer::onFileModified,
            fileOperationEventConsumer::onFileUnchanged,
            fileOperationEventConsumer::onDirectoryCreated
        )
    val runner: ProjectRunner = ProjectRunner(generator, project, environment)
}
