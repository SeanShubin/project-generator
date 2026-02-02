package com.seanshubin.project.generator.commands

import com.seanshubin.project.generator.di.contract.FilesContract
import com.seanshubin.project.generator.dynamic.core.KeyValueStore
import java.nio.file.Path

class EnvironmentImpl(
    override val files: FilesContract,
    override val createKeyStore: (Path) -> KeyValueStore,
    override val onPathNotDirectory: (Path) -> Unit,
    override val onSourceFileNotFound: (Path) -> Unit,
    override val onFileTransformationError: (Path, Path, String) -> Unit,
    override val onFileCreated: (Path) -> Unit,
    override val onFileModified: (Path) -> Unit,
    override val onFileUnchanged: (Path) -> Unit,
    override val onDirectoryCreated: (Path) -> Unit
) : Environment
