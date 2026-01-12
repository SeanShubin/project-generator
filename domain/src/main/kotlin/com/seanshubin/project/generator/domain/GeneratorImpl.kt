package com.seanshubin.project.generator.domain

import java.nio.file.Path

class GeneratorImpl(
    private val xmlRenderer: XmlRenderer,
    private val baseDirectory: Path,
    private val mavenXmlNode: MavenXmlNode
) : Generator {
    override fun generate(project: Project): List<Command> {
        val rootCommand = generateRootCommand(project)
        val moduleCommands = project.modules.flatMap { (name, dependencies) ->
            generateModuleCommand(project, name, dependencies)
        }
        val codeStructureConfigCommands = generateCodeStructureConfigCommands(project)
        return listOf(rootCommand) + moduleCommands + codeStructureConfigCommands
    }

    private fun generateRootCommand(project: Project): Command {
        val xml = mavenXmlNode.generateRootXml(project)
        val lines = xmlRenderer.toLines(xml)
        val path = baseDirectory.resolve("pom.xml")
        return WriteFile(path, lines)
    }

    private fun generateModuleCommand(project: Project, module: String, dependencies: List<String>): List<Command> {
        val xml = mavenXmlNode.generateModuleXml(project, module, dependencies)
        val lines = xmlRenderer.toLines(xml)
        val path = baseDirectory.resolve(module).resolve("pom.xml")
        val moduleParts = module.split("-")
        val sourcePathParts = listOf(module, "src", "main",project.language) + project.prefix + project.name + moduleParts
        val sourcePath = sourcePathParts.fold(baseDirectory, Path::resolve)
        val testPathParts = listOf(module, "src", "test",project.language) + project.prefix + project.name + moduleParts
        val testPath = testPathParts.fold(baseDirectory, Path::resolve)
        val createSourceDir = CreateDirectory(sourcePath)
        val createTestDir = CreateDirectory(testPath)
        val writePomFile = WriteFile(path, lines)
        return listOf(createSourceDir, createTestDir, writePomFile)
    }

    private fun generateCodeStructureConfigCommands(project: Project): List<Command> {
        val path = baseDirectory.resolve("code-structure-config.json")
        val githubDeveloperName= project.developer.githubName
        val githubRepoName = project.name.joinToString("-")

        return listOf(
            setJsonConfig(path, true, "countAsErrors", "inDirectCycle"),
            setJsonConfig(path, true, "countAsErrors", "inGroupCycle"),
            setJsonConfig(path, true, "countAsErrors", "ancestorDependsOnDescendant"),
            setJsonConfig(path, true, "countAsErrors", "descendantDependsOnAncestor"),
            setJsonConfig(path, 0, "maximumAllowedErrorCount"),
            setJsonConfig(path, ".", "inputDir"),
            setJsonConfig(path, "generated/code-structure", "outputDir"),
            setJsonConfig(path, false, "useObservationsCache"),
            setJsonConfig(path, false, "includeJvmDynamicInvocations"),
            setJsonConfig(path, "https://github.com/$githubDeveloperName/$githubRepoName/blob/master/", "sourcePrefix"),
            setJsonConfig(path, listOf(".*/src/main/kotlin/.*\\.kt"), "sourceFileRegexPatterns", "include"),
            setJsonConfig(path, emptyList<String>(), "sourceFileRegexPatterns", "exclude"),
            setJsonConfig(path, 100, "nodeLimitForGraph"),
            setJsonConfig(path, listOf(".*/target/.*\\.class"), "binaryFileRegexPatterns", "include"),
            setJsonConfig(path, emptyList<String>(), "binaryFileRegexPatterns", "exclude")
        )
    }

    private fun setJsonConfig(path:Path, value:Any, vararg keys:String) = SetJsonConfig(path, value, keys.toList())
}
