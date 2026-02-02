package com.seanshubin.project.generator.commands

import java.nio.file.Path

data class WriteFile(val path: Path, val lines: List<String>) : Command {
    override fun execute(environment: Environment) {
        val parent = path.parent
        if (parent != null) {
            environment.files.createDirectories(parent)
        }

        val existed = environment.files.exists(path)
        if (existed) {
            val existingLines = environment.files.readAllLines(path)
            if (existingLines == lines) {
                environment.onFileUnchanged(path)
                return
            }
            environment.files.write(path, lines)
            environment.onFileModified(path)
        } else {
            environment.files.write(path, lines)
            environment.onFileCreated(path)
        }
    }
}