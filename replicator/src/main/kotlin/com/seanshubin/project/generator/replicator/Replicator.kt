package com.seanshubin.project.generator.replicator

import com.seanshubin.project.generator.commands.Command
import java.nio.file.Path

interface Replicator {
    fun replicate(spec: ReplicationSpec, destination: Path): List<Command>
}
