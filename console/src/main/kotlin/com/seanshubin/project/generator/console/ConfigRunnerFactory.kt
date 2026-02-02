package com.seanshubin.project.generator.console

import java.nio.file.Path

/**
 * Factory for creating ConfigFileDependencies runners.
 */
class ConfigRunnerFactory(private val integrations: Integrations) {
    fun createRunner(configFile: Path, baseDirectory: Path): Runnable {
        return ConfigFileDependencies(configFile, baseDirectory, integrations).runner
    }
}
