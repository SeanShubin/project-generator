package com.seanshubin.project.generator.generator

import com.seanshubin.project.generator.commands.Command
import com.seanshubin.project.generator.core.Project

/**
 * Orchestrates generation, owning only what holds for every build system.
 *
 * The sequence of work is the same whether the output is Maven or Cargo: check
 * the spec, emit the root build file, emit each module, emit the supplemental
 * fixed files, then replicate any imported sources. Everything whose *content*
 * depends on the build system is delegated to [BuildSystemGenerator], and the
 * dependency semantics both build systems share live in
 * [com.seanshubin.project.generator.core.ModuleDependencyResolver].
 */
class GeneratorImpl(
    private val buildSystemGenerator: BuildSystemGenerator,
    private val sourceDependencyPlanner: SourceDependencyPlanner
) : Generator {
    override fun generate(project: Project): List<Command> {
        validate(project)
        val rootCommands = buildSystemGenerator.rootCommands(project)
        val moduleCommands = project.modules.flatMap { (module, dependencies) ->
            buildSystemGenerator.moduleCommands(project, module, dependencies)
        }
        val supplementalCommands = buildSystemGenerator.supplementalCommands(project)
        val sourceDependencyCommands = sourceDependencyPlanner.commands(project)
        return rootCommands + moduleCommands + supplementalCommands + sourceDependencyCommands
    }

    private fun validate(project: Project) {
        if (project.publishToMavenCentral && project.developer == null) {
            throw IllegalArgumentException("developer is required when publishToMavenCentral is true")
        }
        buildSystemGenerator.validate(project)
    }
}
