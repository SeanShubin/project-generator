package com.seanshubin.project.generator.domain

import com.seanshubin.project.generator.dynamic.core.KeyValueStore
import com.seanshubin.project.generator.di.contract.FilesContract
import java.nio.file.Path

interface Environment {
    val files: FilesContract
    val createKeyStore: (Path)-> KeyValueStore
    val sourceFileNotifications: SourceFileNotifications
}
