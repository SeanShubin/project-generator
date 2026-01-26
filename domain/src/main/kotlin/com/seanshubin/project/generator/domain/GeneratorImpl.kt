package com.seanshubin.project.generator.domain

import java.nio.file.Path

class GeneratorImpl(
    private val xmlRenderer: XmlRenderer,
    private val baseDirectory: Path,
    private val mavenXmlNode: MavenXmlNode,
    private val sourceProjectLoader: SourceProjectLoader,
    private val sourceFileFinder: SourceFileFinder
) : Generator {
    override fun generate(project: Project): List<Command> {
        val rootCommand = generateRootCommand(project)
        val moduleCommands = project.modules.flatMap { (name, dependencies) ->
            generateModuleCommand(project, name, dependencies)
        }
        val codeStructureConfigCommands = generateCodeStructureConfigCommands(project)
        val sourceDependencyCommands = generateSourceDependencyCommands(project)
        return listOf(rootCommand) + moduleCommands + codeStructureConfigCommands + sourceDependencyCommands
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
        val sourcePath = moduleSourcePath(module, project)
        val testPath = moduleTestPath(module, project)
        val createSourceDir = CreateDirectory(sourcePath)
        val createTestDir = CreateDirectory(testPath)
        val writePomFile = WriteFile(path, lines)
        return listOf(createSourceDir, createTestDir, writePomFile)
    }

    private fun moduleSourcePath(module: String, project: Project): Path {
        val moduleParts = parseModuleName(module)
        val pathParts = listOf(module, SRC_DIR, MAIN_DIR, project.language) +
                project.prefix + project.name + moduleParts
        return pathParts.fold(baseDirectory, Path::resolve)
    }

    private fun moduleTestPath(module: String, project: Project): Path {
        val moduleParts = parseModuleName(module)
        val pathParts = listOf(module, SRC_DIR, TEST_DIR, project.language) +
                project.prefix + project.name + moduleParts
        return pathParts.fold(baseDirectory, Path::resolve)
    }

    private fun parseModuleName(module: String): List<String> {
        val moduleSeparator = "-"
        return module.split(moduleSeparator)
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

    private fun generateSourceDependencyCommands(project: Project): List<Command> {
        val sourceDependency = project.sourceDependency ?: return emptyList()

        // Load source project metadata to determine package structure
        val sourceProject = sourceProjectLoader.loadProject(sourceDependency.sourceProjectPath)

        // Validate module mappings
        validateModuleMappings(sourceProject, project, sourceDependency.moduleMapping)

        // Build transformation map
        val transformations = buildTransformations(
            sourceProject,
            project,
            sourceDependency.moduleMapping
        )

        // Find all source files in mapped modules
        val sourceFiles = sourceFileFinder.findSourceFiles(
            sourceDependency.sourceProjectPath,
            baseDirectory,
            sourceProject,
            project,
            sourceDependency.moduleMapping,
            project.language
        )

        // Generate copy commands for each source file
        return sourceFiles.map { sourceFileInfo ->
            CopyAndTransformSourceFile(
                sourceFileInfo.sourcePath,
                sourceFileInfo.targetPath,
                transformations
            )
        }
    }

    private fun validateModuleMappings(
        sourceProject: Project,
        targetProject: Project,
        moduleMapping: Map<String, String>
    ) {
        // Validate that source modules exist in source project
        val sourceModules = sourceProject.modules.keys
        val unmappedSourceModules = moduleMapping.keys.filterNot { it in sourceModules }
        if (unmappedSourceModules.isNotEmpty()) {
            System.err.println("Warning: Source modules not found in source project: $unmappedSourceModules")
        }

        // Validate that target modules exist in target project
        val targetModules = targetProject.modules.keys
        val unmappedTargetModules = moduleMapping.values.filterNot { it in targetModules }
        if (unmappedTargetModules.isNotEmpty()) {
            System.err.println("Warning: Target modules not found in target project: $unmappedTargetModules")
        }

        // Check for duplicate target modules (multiple source modules mapping to same target)
        val duplicateTargets = moduleMapping.values.groupingBy { it }.eachCount().filter { it.value > 1 }
        if (duplicateTargets.isNotEmpty()) {
            System.err.println("Warning: Multiple source modules mapping to same target module: ${duplicateTargets.keys}")
        }
    }

    private fun buildTransformations(
        sourceProject: Project,
        targetProject: Project,
        moduleMapping: Map<String, String>
    ): List<PackageTransformation> {
        return moduleMapping.map { (sourceModule, targetModule) ->
            val sourceModuleParts = parseModuleName(sourceModule)
            val targetModuleParts = parseModuleName(targetModule)

            val sourcePackage = sourceProject.prefix +
                    sourceProject.name +
                    sourceModuleParts

            val targetPackage = targetProject.prefix +
                    targetProject.name +
                    targetModuleParts

            PackageTransformation(sourcePackage, targetPackage)
        }
    }

    companion object {
        private const val SRC_DIR = "src"
        private const val MAIN_DIR = "main"
        private const val TEST_DIR = "test"
    }
}
