package com.seanshubin.project.generator.domain

import java.nio.file.Path

data class WriteFile(val path: Path, val lines:List<String>):Command {
    override fun execute(environment: Environment) {
        val parent = path.parent
        if(parent != null) {
            environment.files.createDirectories(parent)
        }
        environment.files.write(path, lines)
    }
}