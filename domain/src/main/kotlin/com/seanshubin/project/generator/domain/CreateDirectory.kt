package com.seanshubin.project.generator.domain

import java.nio.file.Path

data class CreateDirectory(val path: Path) : Command {
    override fun execute(environment: Environment) {
        environment.files.createDirectories(path)
    }
}
