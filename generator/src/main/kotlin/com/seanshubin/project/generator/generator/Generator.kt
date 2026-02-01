package com.seanshubin.project.generator.generator

import com.seanshubin.project.generator.commands.Command
import com.seanshubin.project.generator.core.Project

interface Generator {
    fun generate(project: Project): List<Command>
}
