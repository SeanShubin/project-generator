package com.seanshubin.project.generator.console

import com.seanshubin.project.generator.dynamic.json.JsonFileKeyValueStore
import com.seanshubin.project.generator.dynamic.core.KeyValueStore
import com.seanshubin.project.generator.di.contract.FilesContract
import com.seanshubin.project.generator.di.delegate.FilesDelegate
import com.seanshubin.project.generator.generator.KeyValueStoreRunner
import com.seanshubin.project.generator.core.Project
import java.nio.file.Path

class ConfigFileDependencies(
    private val configFile: Path,
    private val baseDirectory: Path
) {
    val files: FilesContract = FilesDelegate.defaultInstance()
    val keyValueStore: KeyValueStore = JsonFileKeyValueStore(files, configFile)
    val createRunner: (Project, Path) -> Runnable =
        { project, baseDirectory -> ProjectDependencies(project, baseDirectory).runner }
    val runner: Runnable = KeyValueStoreRunner(keyValueStore, baseDirectory, files, createRunner)
}
