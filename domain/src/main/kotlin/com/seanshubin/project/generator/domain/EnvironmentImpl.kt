package com.seanshubin.project.generator.domain

import com.seanshubin.project.generator.configuration.KeyValueStore
import com.seanshubin.project.generator.contract.FilesContract
import java.nio.file.Path

class EnvironmentImpl(
    override val files: FilesContract,
    override val createKeyStore: (Path) -> KeyValueStore
) : Environment
