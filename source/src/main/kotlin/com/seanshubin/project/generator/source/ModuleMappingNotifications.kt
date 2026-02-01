package com.seanshubin.project.generator.source

/**
 * Notifications for module mapping validation.
 *
 * Emits structured events for module mapping issues during project generation,
 * enabling testability and redirection of warnings.
 */
interface ModuleMappingNotifications {
    /**
     * Emitted when source modules in the mapping are not found in the source project.
     *
     * @param modules The list of source module names that were not found
     */
    fun sourceModulesNotFound(modules: List<String>)

    /**
     * Emitted when target modules in the mapping are not found in the target project.
     *
     * @param modules The list of target module names that were not found
     */
    fun targetModulesNotFound(modules: List<String>)

    /**
     * Emitted when multiple source modules map to the same target module.
     *
     * @param modules The set of target module names that have duplicates
     */
    fun duplicateTargetModules(modules: Set<String>)
}
