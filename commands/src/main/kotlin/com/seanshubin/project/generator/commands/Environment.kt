package com.seanshubin.project.generator.commands

import com.seanshubin.project.generator.di.contract.FilesContract
import com.seanshubin.project.generator.dynamic.core.KeyValueStore
import java.nio.file.Path

interface Environment {
    val files: FilesContract
    val createKeyStore: (Path) -> KeyValueStore
    val onPathNotDirectory: (Path) -> Unit
    val onSourceFileNotFound: (Path) -> Unit
    val onFileTransformationError: (Path, Path, String) -> Unit
    val onFileCreated: (Path) -> Unit
    val onFileModified: (Path) -> Unit
    val onFileUnchanged: (Path) -> Unit
    val onDirectoryCreated: (Path) -> Unit
}
