package com.seanshubin.project.generator.console

object EntryPoint {
    @JvmStatic
    fun main(args: Array<String>) {
        ArgsDependencies(args).runner.run()
    }
}