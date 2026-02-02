package com.seanshubin.project.generator.commands

import java.nio.file.Path

data class CreateDirectory(val path: Path) : Command {
    override fun execute(environment: Environment) {
        val existed = environment.files.exists(path)
        environment.files.createDirectories(path)
        if (!existed) {
            environment.onDirectoryCreated(path)
        }
    }
}
