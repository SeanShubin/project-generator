package com.seanshubin.project.generator.domain

import java.nio.file.Path
import java.nio.file.Paths

class ArgsRunner(
    private val args: Array<String>,
    private val createRunner: (Path, Path) -> Runnable
) : Runnable {
    override fun run() {
        val configurationFileName = args.getOrNull(0) ?: "project-specification.json"
        val baseDirectoryName = args.getOrNull(1) ?: "."
        val baseDirectory = Paths.get(baseDirectoryName)
        val configurationFile = baseDirectory.resolve(configurationFileName)
        val runner = createRunner(configurationFile, baseDirectory)
        runner.run()
    }
}
