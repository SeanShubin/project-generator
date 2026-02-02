package com.seanshubin.project.generator.commands

import com.seanshubin.project.generator.di.contract.FilesContract
import com.seanshubin.project.generator.dynamic.core.KeyValueStore
import com.seanshubin.project.generator.source.SourceFileNotifications
import java.nio.file.Path

interface Environment {
    val files: FilesContract
    val createKeyStore: (Path) -> KeyValueStore
    val sourceFileNotifications: SourceFileNotifications
    val fileOperationNotifications: FileOperationNotifications
}
