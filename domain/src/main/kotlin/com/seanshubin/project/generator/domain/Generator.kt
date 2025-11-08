package com.seanshubin.project.generator.domain

interface Generator {
    fun generate(project: Project):List<Command>
}
