package com.seanshubin.project.generator.source

/**
 * Implementation of ModuleMappingNotifications that emits warnings as lines.
 *
 * Formats structured event data into human-readable messages and passes them to
 * an injected emit function (typically System.err::println in production).
 */
class LineEmittingModuleMappingNotifications(
    private val emit: (String) -> Unit
) : ModuleMappingNotifications {
    override fun sourceModulesNotFound(modules: List<String>) {
        emit("Warning: Source modules not found in source project: $modules")
    }

    override fun targetModulesNotFound(modules: List<String>) {
        emit("Warning: Target modules not found in target project: $modules")
    }

    override fun duplicateTargetModules(modules: Set<String>) {
        emit("Warning: Multiple source modules mapping to same target module: $modules")
    }
}
