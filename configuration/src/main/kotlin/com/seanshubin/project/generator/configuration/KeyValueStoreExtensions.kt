package com.seanshubin.project.generator.configuration

fun KeyValueStore.loadStringOrDefault(key: List<String>, default: String): String {
    return loadOrCreateDefault(key, default) as String
}

fun KeyValueStore.loadMapOrEmpty(key: List<String>): Map<*, *> {
    return if (exists(key)) {
        load(key) as Map<*, *>
    } else {
        emptyMap<Any?, Any?>()
    }
}

fun KeyValueStore.loadListOrEmpty(key: List<String>): List<*> {
    return if (exists(key)) {
        load(key) as List<*>
    } else {
        emptyList<Any?>()
    }
}

fun Any?.asStringOrDefault(default: String): String {
    return (this as? String) ?: default
}

fun Any?.asMapOrEmpty(): Map<*, *> {
    return (this as? Map<*, *>) ?: emptyMap<Any?, Any?>()
}
