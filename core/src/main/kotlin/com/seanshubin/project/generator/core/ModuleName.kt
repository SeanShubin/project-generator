package com.seanshubin.project.generator.core

/**
 * How a module name decomposes.
 *
 * A hyphenated module name maps to nested package directories on the JVM
 * ("dynamic-json" to "dynamic/json") and is used verbatim as a crate name for
 * Cargo.  Both readings are needed, so the split lives here rather than being
 * repeated in each emitter.
 */
object ModuleName {
    private const val separator = "-"

    fun parts(module: String): List<String> = module.split(separator)
}
