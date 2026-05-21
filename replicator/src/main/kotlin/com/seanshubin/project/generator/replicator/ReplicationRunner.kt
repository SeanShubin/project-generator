package com.seanshubin.project.generator.replicator

import com.seanshubin.project.generator.commands.Environment
import java.nio.file.Path

class ReplicationRunner(
    private val replicator: Replicator,
    private val spec: ReplicationSpec,
    private val destination: Path,
    private val environment: Environment
) : Runnable {
    override fun run() {
        val commands = replicator.replicate(spec, destination)
        commands.forEach { it.execute(environment) }
    }
}
