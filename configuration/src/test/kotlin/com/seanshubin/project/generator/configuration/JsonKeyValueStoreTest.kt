package com.seanshubin.project.generator.configuration

import com.seanshubin.project.generator.contract.FilesDelegate
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals

class JsonKeyValueStoreTest {
    @Test
    fun intValue(){
        withTemporaryFile { path ->
            val keyValueStore = FixedPathJsonFileKeyValueStore(path, FilesDelegate)
            val documentationPrefix = listOf("documentation")
            val keyValueStoreWithDocumentation = KeyValueStoreWithDocumentationDelegate(keyValueStore, documentationPrefix)
            val key = listOf("a", "b", "c")
            val documentationKey = listOf("documentation") + key
            val documentation = listOf("this is a number")
            val expectedDocumentation = listOf(
                "path: a.b.c",
                "default value: 456",
                "default value type: Integer"
            ) + documentation
            val value = 456
            val actualValue = keyValueStoreWithDocumentation.load(key, value, documentation)
            val actualDocumentation = keyValueStore.load(documentationKey)
            assertEquals(expectedDocumentation, actualDocumentation)
            assertEquals(value, actualValue)
        }
    }

    @Test
    fun arrays(){
        withTemporaryFile { path ->
            val keyValueStore = FixedPathJsonFileKeyValueStore(path,FilesDelegate)
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

    private fun withTemporaryFile(f:(Path)->Unit){
        val path = Files.createTempFile("test", ".json")
        path.toFile().deleteOnExit()
        f(path)
        Files.delete(path)
    }
}
