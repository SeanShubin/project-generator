package com.seanshubin.project.generator.console

import com.seanshubin.project.generator.generator.ArgsRunner

class ArgsDependencies(
    private val args: Array<String>,
    private val integrations: Integrations
) {
    val configRunnerFactory = ConfigRunnerFactory(integrations)
    val runner: Runnable = ArgsRunner(args, configRunnerFactory::createRunner)
}
