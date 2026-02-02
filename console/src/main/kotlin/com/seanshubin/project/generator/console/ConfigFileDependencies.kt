package com.seanshubin.project.generator.console

import com.seanshubin.project.generator.core.Project
import com.seanshubin.project.generator.di.contract.FilesContract
import com.seanshubin.project.generator.di.delegate.FilesDelegate
import com.seanshubin.project.generator.dynamic.core.KeyValueStore
import com.seanshubin.project.generator.dynamic.json.JsonFileKeyValueStore
import com.seanshubin.project.generator.generator.KeyValueStoreRunner
import java.nio.file.Path

class ConfigFileDependencies(
    private val configFile: Path,
    private val baseDirectory: Path,
    private val integrations: Integrations
) {
    val files: FilesContract = integrations.files
    val keyValueStore: KeyValueStore = JsonFileKeyValueStore(files, configFile)
    val projectRunnerFactory = ProjectRunnerFactory(integrations)
    val runner: Runnable = KeyValueStoreRunner(keyValueStore, baseDirectory, files, projectRunnerFactory::createRunner)
}
