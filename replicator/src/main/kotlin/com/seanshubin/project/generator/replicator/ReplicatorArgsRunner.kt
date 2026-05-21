package com.seanshubin.project.generator.replicator

import java.nio.file.Path
import java.nio.file.Paths

class ReplicatorArgsRunner(
    private val args: Array<String>,
    private val createRunner: (Path, Path) -> Runnable
) : Runnable {
    override fun run() {
        val configFileName = args.getOrNull(0) ?: "replication-spec.json"
        val destinationName = args.getOrNull(1) ?: "."
        val destination = Paths.get(destinationName)
        val configFile = Paths.get(configFileName)
        val runner = createRunner(configFile, destination)
        runner.run()
    }
}
