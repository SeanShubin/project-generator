package com.seanshubin.project.generator.console

import com.seanshubin.project.generator.configuration.FixedPathJsonFileKeyValueStore
import com.seanshubin.project.generator.configuration.KeyValueStore
import com.seanshubin.project.generator.contract.FilesContract
import com.seanshubin.project.generator.contract.FilesDelegate
import com.seanshubin.project.generator.domain.KeyValueStoreRunner
import com.seanshubin.project.generator.domain.Project
import java.nio.file.Path

class ConfigFileDependencies(
    private val configFile: Path,
    private val baseDirectory: Path
) {
    val files: FilesContract = FilesDelegate
    val keyValueStore: KeyValueStore = FixedPathJsonFileKeyValueStore(configFile, files)
    val createRunner: (Project, Path) -> Runnable =
        { project, baseDirectory -> ProjectDependencies(project, baseDirectory).runner }
    val runner: Runnable = KeyValueStoreRunner(keyValueStore, baseDirectory, files, createRunner)
}
