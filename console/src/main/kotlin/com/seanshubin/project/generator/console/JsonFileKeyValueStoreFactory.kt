package com.seanshubin.project.generator.console

import com.seanshubin.project.generator.di.contract.FilesContract
import com.seanshubin.project.generator.dynamic.core.KeyValueStore
import com.seanshubin.project.generator.dynamic.json.JsonFileKeyValueStore
import java.nio.file.Path

/**
 * Factory for creating JsonFileKeyValueStore instances.
 */
class JsonFileKeyValueStoreFactory(private val files: FilesContract) {
    fun create(path: Path): KeyValueStore {
        return JsonFileKeyValueStore(files, path)
    }
}
