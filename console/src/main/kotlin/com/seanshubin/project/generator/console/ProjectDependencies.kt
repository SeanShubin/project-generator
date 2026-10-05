package com.seanshubin.project.generator.console

import com.seanshubin.project.generator.commands.Environment
import com.seanshubin.project.generator.commands.EnvironmentImpl
import com.seanshubin.project.generator.core.Project
import com.seanshubin.project.generator.core.StringUtility
import com.seanshubin.project.generator.di.contract.FilesContract
import com.seanshubin.project.generator.cargo.CargoTomlNode
import com.seanshubin.project.generator.cargo.CargoTomlNodeImpl
import com.seanshubin.project.generator.cargo.CrateVersionLookup
import com.seanshubin.project.generator.cargo.CrateVersionLookupImpl
import com.seanshubin.project.generator.cargo.TomlRenderer
import com.seanshubin.project.generator.cargo.TomlRendererImpl
import com.seanshubin.project.generator.generator.Generator
import com.seanshubin.project.generator.generator.GeneratorFactory
import com.seanshubin.project.generator.generator.ProjectRunner
import com.seanshubin.project.generator.gradle.GradleFileNode
import com.seanshubin.project.generator.gradle.GradleFileNodeImpl
import com.seanshubin.project.generator.gradle.GradleKotlinDslRenderer
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
    private val project: Project,
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
    private val cargoEventConsumer = CargoEventConsumer(integrations.emit)
    private val sourceFileEventConsumer = SourceFileEventConsumer(integrations.emitError)
    private val moduleMappingEventConsumer = ModuleMappingEventConsumer(integrations.emitError)
    private val fileOperationEventConsumer = FileOperationEventConsumer(integrations.emit)
    private val versionLookup: VersionLookup =
        VersionLookupImpl(http, xmlParserFactory, mavenEventConsumer::onLookupVersion)
    private val mavenXmlNode: MavenXmlNode = MavenXmlNodeImpl(versionLookup)
    private val gradleRenderer: GradleKotlinDslRenderer = GradleKotlinDslRenderer()
    private val gradleFileNode: GradleFileNode = GradleFileNodeImpl(versionLookup)
    private val files: FilesContract = integrations.files
    private val sourceProjectLoader: SourceProjectLoader = SourceProjectLoaderImpl(files)
    private val sourceFileFinder: SourceFileFinder =
        SourceFileFinderImpl(files, sourceFileEventConsumer::onPathNotDirectory)
    private val tomlRenderer: TomlRenderer = TomlRendererImpl(indent)
    private val crateVersionLookup: CrateVersionLookup =
        CrateVersionLookupImpl(http, cargoEventConsumer::onLookupVersion)
    private val cargoTomlNode: CargoTomlNode = CargoTomlNodeImpl(crateVersionLookup)
    private val generatorFactory = GeneratorFactory(
        xmlRenderer,
        mavenXmlNode,
        gradleFileNode,
        gradleRenderer,
        tomlRenderer,
        cargoTomlNode,
        sourceProjectLoader,
        sourceFileFinder,
        moduleMappingEventConsumer::onSourceModulesNotFound,
        moduleMappingEventConsumer::onTargetModulesNotFound,
        moduleMappingEventConsumer::onDuplicateTargetModules
    )
    private val generator: Generator = generatorFactory.create(project, baseDirectory)
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
