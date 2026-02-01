package com.seanshubin.project.generator.console

import com.seanshubin.project.generator.generator.ArgsRunner
import java.nio.file.Path

class ArgsDependencies(private val args: Array<String>) {
    val createRunner: (Path, Path) -> Runnable = { configFile, baseDirectory -> ConfigFileDependencies(configFile, baseDirectory).runner }
    val runner: Runnable = ArgsRunner(args, createRunner)
}
