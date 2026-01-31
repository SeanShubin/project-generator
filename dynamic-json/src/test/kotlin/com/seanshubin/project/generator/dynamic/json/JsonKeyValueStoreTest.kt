package com.seanshubin.project.generator.dynamic.json

import com.seanshubin.project.generator.di.contract.FilesContract
import com.seanshubin.project.generator.di.delegate.FilesDelegate
import com.seanshubin.project.generator.dynamic.core.KeyValueStoreWithDocumentationDelegate
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals

//
// This file was imported from: /Users/seashubi/github.com/SeanShubin/kotlin-reusable
// Module: dynamic-json
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

class JsonKeyValueStoreTest {
    val files: FilesContract = FilesDelegate.defaultInstance()
    @Test
    fun intValue(){
        withTemporaryFiles { path, documentationPath ->
            val keyValueStore = JsonFileKeyValueStore(files, path)
            val documentationKeyValueStore = JsonFileKeyValueStore(files, documentationPath)
            val keyValueStoreWithDocumentation =
                KeyValueStoreWithDocumentationDelegate(keyValueStore, documentationKeyValueStore)
            val key = listOf("a", "b", "c")
            val documentation = listOf("this is a number")
            val expectedDocumentation = listOf(
                "path: a.b.c",
                "default value: 456",
                "default value type: Integer"
            ) + documentation
            val value = 456
            val actualValue = keyValueStoreWithDocumentation.load(key, value, documentation)
            val actualDocumentation = documentationKeyValueStore.load(key)
            assertEquals(expectedDocumentation, actualDocumentation)
            assertEquals(value, actualValue)
        }
    }

    @Test
    fun arrays(){
        withTemporaryFile { path ->
            val keyValueStore = JsonFileKeyValueStore(files, path)
            keyValueStore.store(listOf("the-array", 0, "name"), "a")
            keyValueStore.store(listOf("the-array", 0, "value"), 1)
            keyValueStore.store(listOf("the-array", 1, "name"), "b")
            keyValueStore.store(listOf("the-array", 1, "value"), 2)
            keyValueStore.store(listOf("the-array", 2, "name"), "c")
            keyValueStore.store(listOf("the-array", 2, "value"), 3)
            assertEquals(keyValueStore.arraySize(listOf("the-array")), 3)
            assertEquals(keyValueStore.load(listOf("the-array", 0, "name")), "a")
            assertEquals(keyValueStore.load(listOf("the-array", 0, "value")), 1)
            assertEquals(keyValueStore.load(listOf("the-array", 1, "name")), "b")
            assertEquals(keyValueStore.load(listOf("the-array", 1, "value")), 2)
            assertEquals(keyValueStore.load(listOf("the-array", 2, "name")), "c")
            assertEquals(keyValueStore.load(listOf("the-array", 2, "value")), 3)
        }
    }

    private fun withTemporaryFiles(f:(Path, Path)->Unit){
        withTemporaryFile { path1 ->
            withTemporaryFile { path2 ->
                f(path1, path2)
            }
        }
    }
    private fun withTemporaryFile(f:(Path)->Unit){
        val path = Files.createTempFile("test", ".json")
        path.toFile().deleteOnExit()
        f(path)
        Files.delete(path)
    }
}
