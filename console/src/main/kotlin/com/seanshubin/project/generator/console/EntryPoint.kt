package com.seanshubin.project.generator.console

object EntryPoint {
    @JvmStatic
    fun main(args: Array<String>) {
        val integrations: Integrations = ProductionIntegrations
        ArgsDependencies(args, integrations).runner.run()
    }
}