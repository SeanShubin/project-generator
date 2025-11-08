package com.seanshubin.project.generator.domain

import java.nio.file.Path

class GeneratorImpl(
    private val xmlRenderer: XmlRenderer,
    private val baseDirectory: Path
) : Generator {
    override fun generate(project: Project): List<Command> {
        val rootCommand = generateRootCommand(project)
        val moduleCommands = project.modules.map { (name, dependencies) ->
            generateModuleCommand(project, name, dependencies)
        }
        return listOf(rootCommand) + moduleCommands
    }

    private fun generateRootCommand(project: Project): Command {
        val xml = MavenXmlNode.generateRootXml(project)
        val lines = xmlRenderer.toLines(xml)
        val path = baseDirectory.resolve("pom.xml")
        return WriteFile(path, lines)
    }

    private fun generateModuleCommand(project: Project, module: String, dependencies: List<String>): Command {
        val xml = MavenXmlNode.generateModuleXml(project, module, dependencies)
        val lines = xmlRenderer.toLines(xml)
        val path = baseDirectory.resolve(module).resolve("pom.xml")
        return WriteFile(path, lines)
    }
}
