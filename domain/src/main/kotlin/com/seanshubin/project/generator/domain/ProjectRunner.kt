package com.seanshubin.project.generator.domain

class ProjectRunner(
    private val generator: Generator,
    private val project: Project,
    private val environment: Environment
) : Runnable {
    override fun run() {
        val commands = generator.generate(project)
        commands.forEach { it.execute(environment) }
    }
}
