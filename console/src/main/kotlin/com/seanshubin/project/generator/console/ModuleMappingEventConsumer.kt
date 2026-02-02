package com.seanshubin.project.generator.console

/**
 * Consumes module mapping validation events and formats them for output.
 */
class ModuleMappingEventConsumer(private val emitError: (String) -> Unit) {
    fun onSourceModulesNotFound(modules: List<String>) {
        emitError("Warning: Source modules not found in source project: ${modules.joinToString(", ")}")
    }

    fun onTargetModulesNotFound(modules: List<String>) {
        emitError("Warning: Target modules not found in target project: ${modules.joinToString(", ")}")
    }

    fun onDuplicateTargetModules(modules: Set<String>) {
        emitError(
            "Warning: Duplicate target modules (multiple source modules mapping to same target): ${
                modules.joinToString(
                    ", "
                )
            }"
        )
    }
}
