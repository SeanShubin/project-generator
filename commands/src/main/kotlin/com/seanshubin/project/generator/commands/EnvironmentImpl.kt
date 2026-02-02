package com.seanshubin.project.generator.commands

import com.seanshubin.project.generator.di.contract.FilesContract
import com.seanshubin.project.generator.dynamic.core.KeyValueStore
import com.seanshubin.project.generator.source.SourceFileNotifications
import java.nio.file.Path

class EnvironmentImpl(
    override val files: FilesContract,
    override val createKeyStore: (Path) -> KeyValueStore,
    override val sourceFileNotifications: SourceFileNotifications,
    override val fileOperationNotifications: FileOperationNotifications
) : Environment
