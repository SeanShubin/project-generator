package com.seanshubin.project.generator.generator

import com.seanshubin.project.generator.commands.Command
import com.seanshubin.project.generator.commands.Environment
import com.seanshubin.project.generator.core.Project

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
