package com.seanshubin.project.generator.domain

interface Command {
    fun execute(environment: Environment)
}