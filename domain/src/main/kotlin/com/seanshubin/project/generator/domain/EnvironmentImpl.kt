package com.seanshubin.project.generator.domain

import com.seanshubin.project.generator.dynamic.core.KeyValueStore
import com.seanshubin.project.generator.di.contract.FilesContract
import java.nio.file.Path

class EnvironmentImpl(
    override val files: FilesContract,
    override val createKeyStore: (Path) -> KeyValueStore,
    override val sourceFileNotifications: SourceFileNotifications
) : Environment
