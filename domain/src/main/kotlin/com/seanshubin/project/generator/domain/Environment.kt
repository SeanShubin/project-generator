package com.seanshubin.project.generator.domain

import com.seanshubin.project.generator.configuration.KeyValueStore
import com.seanshubin.project.generator.contract.FilesContract
import java.nio.file.Path

interface Environment {
    val files: FilesContract
    val createKeyStore: (Path)-> KeyValueStore
}
