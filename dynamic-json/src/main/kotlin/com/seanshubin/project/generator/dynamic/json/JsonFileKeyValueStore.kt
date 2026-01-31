package com.seanshubin.project.generator.dynamic.json

import com.fasterxml.jackson.module.kotlin.readValue
import com.seanshubin.project.generator.di.contract.FilesContract;
import com.seanshubin.project.generator.dynamic.core.DynamicUtil
import com.seanshubin.project.generator.dynamic.core.KeyValueStore

import java.nio.charset.StandardCharsets
import java.nio.file.Path

//
// This file was imported from: /Users/seashubi/github.com/SeanShubin/kotlin-reusable
// Module: dynamic-json
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

class JsonFileKeyValueStore(val files: FilesContract, val path: Path) : KeyValueStore {
    override fun load(key: List<Any>): Any? {
        assertKeyValid(key)
        val jsonObject = loadJsonObject()
        return DynamicUtil.get(jsonObject, key)
    }

    override fun store(key: List<Any>, value: Any?) {
        assertKeyValid(key)
        val jsonObject = loadJsonObject()
        val newJsonObject = DynamicUtil.set(jsonObject, key, value)
        val newJsonText = JsonMappers.pretty.writeValueAsString(newJsonObject)
        files.writeString(path, newJsonText, jsonCharset)
    }

    override fun exists(key: List<Any>): Boolean {
        assertKeyValid(key)
        if (!files.exists(path)) return false
        val jsonObject = loadJsonObject()
        return DynamicUtil.exists(jsonObject, key)
    }

    override fun arraySize(key: List<Any>): Int {
        assertKeyValid(key)
        val jsonObject = loadJsonObject()
        val array = DynamicUtil.get(jsonObject, key) as List<*>
        return array.size
    }

    private fun loadJsonObject(): Any? {
        val text = if (files.exists(path)) {
            files.readString(path, jsonCharset)
        } else {
            defaultJsonText
        }
        val jsonText = text.ifBlank { "{}" }
        val jsonObject = JsonMappers.parser.readValue<Any?>(jsonText)
        return jsonObject
    }

    private fun assertKeyValid(key: List<Any>) {
        key.forEach(::assertKeyPartValid)
    }

    private fun assertKeyPartValid(keyPart: Any) {
        when (keyPart) {
            is String, is Int -> Unit
            else -> throw IllegalArgumentException("All key parts must be String or Int, got $keyPart")
        }
    }

    companion object Companion {
        private val jsonCharset = StandardCharsets.UTF_8
        private val defaultJsonText = "{}"
    }
}
