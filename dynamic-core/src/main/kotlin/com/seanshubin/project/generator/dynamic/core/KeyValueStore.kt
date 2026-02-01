package com.seanshubin.project.generator.dynamic.core

//
// This file was imported from: ../kotlin-reusable
// Module: dynamic-core
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

interface KeyValueStore {
    fun load(key: List<Any>): Any?
    fun store(key: List<Any>, value: Any?)
    fun exists(key: List<Any>): Boolean
    fun arraySize(key: List<Any>): Int
    fun loadOrDefault(key: List<Any>, default: Any?): Any? =
        if (exists(key)) load(key) else default

    fun loadOrCreateDefault(key: List<Any>, default: Any?): Any? =
        if (exists(key)) load(key) else default.also { store(key, it) }
}
