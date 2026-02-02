package com.seanshubin.project.generator.console

import com.seanshubin.project.generator.generator.ArgsRunner
import java.nio.file.Path

class ArgsDependencies(
    private val args: Array<String>,
    private val integrations: Integrations
) {
    val createRunner: (Path, Path) -> Runnable =
        { configFile, baseDirectory -> ConfigFileDependencies(configFile, baseDirectory, integrations).runner }
    val runner: Runnable = ArgsRunner(args, createRunner)
}
