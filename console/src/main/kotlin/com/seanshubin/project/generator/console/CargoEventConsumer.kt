package com.seanshubin.project.generator.console

/**
 * Consumes crates.io lookup events and formats them for output.
 */
class CargoEventConsumer(private val emit: (String) -> Unit) {
    fun onLookupVersion(uri: String, crate: String, version: String) {
        emit("crate:$crate version:$version uri:$uri")
    }
}
