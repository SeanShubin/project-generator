package com.seanshubin.project.generator.replicator

import com.seanshubin.project.generator.commands.Environment
import com.seanshubin.project.generator.commands.EnvironmentImpl
import com.seanshubin.project.generator.dynamic.core.KeyValueStore
import com.seanshubin.project.generator.dynamic.json.JsonFileKeyValueStore
import com.seanshubin.project.generator.dynamic.json.loadBooleanOrDefault
import com.seanshubin.project.generator.dynamic.json.loadListOrEmpty
import com.seanshubin.project.generator.dynamic.json.loadStringOrDefault
import com.seanshubin.project.generator.core.GroupArtifactVersionScope
import com.seanshubin.project.generator.generator.GeneratorImpl
import com.seanshubin.project.generator.gradle.GradleFileNodeImpl
import com.seanshubin.project.generator.gradle.GradleKotlinDslRenderer
import com.seanshubin.project.generator.http.HttpImpl
import com.seanshubin.project.generator.maven.MavenXmlNodeImpl
import com.seanshubin.project.generator.maven.VersionLookupImpl
import com.seanshubin.project.generator.source.SourceFileFinderImpl
import com.seanshubin.project.generator.source.SourceProjectLoader
import com.seanshubin.project.generator.source.SourceProjectLoaderImpl
import com.seanshubin.project.generator.xml.SaxParserFactoryImpl
import com.seanshubin.project.generator.xml.StringUtility
import com.seanshubin.project.generator.xml.XmlRendererImpl
import java.nio.file.Path
import java.nio.file.Paths

class ReplicatorDependencies(
    private val configFile: Path,
    private val destination: Path,
    private val integrations: ReplicatorIntegrations
) {
    private val files = integrations.files
    private val keyValueStore: KeyValueStore = JsonFileKeyValueStore(files, configFile)
    private val sourceDirectoryString: String = keyValueStore.loadStringOrDefault(listOf("sourceDirectory"), "")
    private val sourceDirectory: Path = Paths.get(sourceDirectoryString)
    private val newPrefix: List<String> = keyValueStore.loadListOrEmpty(listOf("newPrefix")).map { it as String }
    private val generateCodeStructureOverride: Boolean? =
        if (keyValueStore.exists(listOf("generateCodeStructure")))
            keyValueStore.loadBooleanOrDefault(listOf("generateCodeStructure"), true)
        else null
    private val verbatimPaths: List<String> = keyValueStore.loadListOrEmpty(listOf("verbatimPaths")).map { it as String }
    private val developerOverride: Any? =
        if (keyValueStore.exists(listOf("developer")))
            keyValueStore.load(listOf("developer"))
        else ReplicationSpec.DEVELOPER_ABSENT
    private val spec = ReplicationSpec(sourceDirectory, newPrefix, generateCodeStructureOverride, verbatimPaths, developerOverride)
    private val keyValueStoreFactory: (Path) -> KeyValueStore = { path -> JsonFileKeyValueStore(files, path) }
    private val environment: Environment = EnvironmentImpl(
        files,
        keyValueStoreFactory,
        { path -> integrations.emitError("path not directory: $path") },
        { path -> integrations.emitError("source file not found: $path") },
        { source, target, message -> integrations.emitError("transformation error $source -> $target: $message") },
        { path -> integrations.emit("created: $path") },
        { path -> integrations.emit("modified: $path") },
        { path -> integrations.emit("unchanged: $path") },
        { path -> integrations.emit("directory created: $path") }
    )
    private val sourceProjectLoader: SourceProjectLoader = SourceProjectLoaderImpl(files)
    private val fileClassifier: FileClassifier = FileClassifierImpl()
    private val replicator: Replicator = ReplicatorImpl(sourceProjectLoader, fileClassifier)

    private val httpClient = integrations.httpClientFactory.createHttpClient()
    private val http = HttpImpl(httpClient)
    private val xmlParserFactory = SaxParserFactoryImpl()
    private val versionLookup = VersionLookupImpl(http, xmlParserFactory) { uri: String, dep: GroupArtifactVersionScope ->
        integrations.emit("group:${dep.group} artifact:${dep.artifact} version:${dep.version} uri:$uri")
    }
    private val xmlRenderer = XmlRendererImpl(StringUtility.indent)
    private val mavenXmlNode = MavenXmlNodeImpl(versionLookup)
    private val gradleRenderer = GradleKotlinDslRenderer()
    private val gradleFileNode = GradleFileNodeImpl(versionLookup)
    private val sourceFileFinder = SourceFileFinderImpl(files) { path ->
        integrations.emitError("path not directory: $path")
    }

    private val postReplicationStep: (Path, Environment) -> Unit = { destPath, env ->
        val destProject = sourceProjectLoader.loadProject(destPath)
        val generator = GeneratorImpl(
            xmlRenderer = xmlRenderer,
            baseDirectory = destPath,
            mavenXmlNode = mavenXmlNode,
            gradleFileNode = gradleFileNode,
            gradleRenderer = gradleRenderer,
            sourceProjectLoader = sourceProjectLoader,
            sourceFileFinder = sourceFileFinder,
            onSourceModulesNotFound = { modules ->
                integrations.emitError("source modules not found: $modules")
            },
            onTargetModulesNotFound = { modules ->
                integrations.emitError("target modules not found: $modules")
            },
            onDuplicateTargetModules = { modules ->
                integrations.emitError("duplicate target modules: $modules")
            }
        )
        val genCommands = generator.generate(destProject)
        genCommands.forEach { it.execute(env) }
    }

    val runner: Runnable = ReplicationRunner(replicator, spec, destination, environment, postReplicationStep)
}
