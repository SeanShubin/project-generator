package com.seanshubin.project.generator.commands

interface Command {
    fun execute(environment: Environment)
}