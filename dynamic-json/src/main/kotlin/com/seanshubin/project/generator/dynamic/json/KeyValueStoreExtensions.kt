package com.seanshubin.project.generator.dynamic.json

import com.seanshubin.project.generator.dynamic.core.KeyValueStore

//
// This file was imported from: ../kotlin-reusable
// Module: dynamic-json
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

fun KeyValueStore.loadStringOrDefault(key: List<String>, default: String): String =
    loadOrCreateDefault(key, default) as String

fun KeyValueStore.loadBooleanOrDefault(key: List<String>, default: Boolean): Boolean =
    loadOrCreateDefault(key, default) as Boolean

fun KeyValueStore.loadMapOrEmpty(key: List<String>): Map<*, *> =
    loadOrCreateDefault(key, emptyMap<Any?, Any?>()) as Map<*, *>

fun KeyValueStore.loadListOrEmpty(key: List<String>): List<*> =
    loadOrCreateDefault(key, emptyList<Any?>()) as List<*>

fun Any?.asStringOrDefault(default: String): String = (this as? String) ?: default
fun Any?.asMapOrEmpty(): Map<*, *> = (this as? Map<*, *>) ?: emptyMap<Any?, Any?>()
