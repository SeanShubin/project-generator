package com.seanshubin.project.generator.console

import com.seanshubin.project.generator.core.Project
import java.nio.file.Path

/**
 * Factory for creating ProjectDependencies runners.
 */
class ProjectRunnerFactory(private val integrations: Integrations) {
    fun createRunner(project: Project, baseDirectory: Path): Runnable {
        return ProjectDependencies(project, baseDirectory, integrations).runner
    }
}
