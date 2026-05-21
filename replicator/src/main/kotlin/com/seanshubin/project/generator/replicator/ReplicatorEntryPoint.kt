package com.seanshubin.project.generator.replicator

import java.nio.file.Path

object ReplicatorEntryPoint {
    @JvmStatic
    fun main(args: Array<String>) {
        val integrations: ReplicatorIntegrations = ProductionIntegrations
        ReplicatorArgsRunner(args) { configFile: Path, destination: Path ->
            ReplicatorDependencies(configFile, destination, integrations).runner
        }.run()
    }
}
