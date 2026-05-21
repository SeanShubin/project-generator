package com.seanshubin.project.generator.replicator

import com.seanshubin.project.generator.commands.Environment
import com.seanshubin.project.generator.commands.EnvironmentImpl
import com.seanshubin.project.generator.dynamic.core.KeyValueStore
import com.seanshubin.project.generator.dynamic.json.JsonFileKeyValueStore
import com.seanshubin.project.generator.dynamic.json.loadBooleanOrDefault
import com.seanshubin.project.generator.dynamic.json.loadListOrEmpty
import com.seanshubin.project.generator.dynamic.json.loadStringOrDefault
import com.seanshubin.project.generator.source.SourceProjectLoader
import com.seanshubin.project.generator.source.SourceProjectLoaderImpl
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
    private val spec = ReplicationSpec(sourceDirectory, newPrefix, generateCodeStructureOverride)
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
    val runner: Runnable = ReplicationRunner(replicator, spec, destination, environment)
}
