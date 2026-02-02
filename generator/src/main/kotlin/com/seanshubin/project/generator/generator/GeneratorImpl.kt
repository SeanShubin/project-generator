package com.seanshubin.project.generator.generator

import com.seanshubin.project.generator.commands.*
import com.seanshubin.project.generator.core.Project
import com.seanshubin.project.generator.maven.MavenXmlNode
import com.seanshubin.project.generator.source.PackageTransformation
import com.seanshubin.project.generator.source.SourceFileFinder
import com.seanshubin.project.generator.source.SourceProjectLoader
import com.seanshubin.project.generator.xml.XmlRenderer
import java.nio.file.Path

class GeneratorImpl(
    private val xmlRenderer: XmlRenderer,
    private val baseDirectory: Path,
    private val mavenXmlNode: MavenXmlNode,
    private val sourceProjectLoader: SourceProjectLoader,
    private val sourceFileFinder: SourceFileFinder,
    private val onSourceModulesNotFound: (List<String>) -> Unit,
    private val onTargetModulesNotFound: (List<String>) -> Unit,
    private val onDuplicateTargetModules: (Set<String>) -> Unit
) : Generator {
    override fun generate(project: Project): List<Command> {
        val rootCommand = generateRootCommand(project)
        val moduleCommands = project.modules.flatMap { (name, dependencies) ->
            generateModuleCommand(project, name, dependencies)
        }
        val helperFileCommands = generateHelperFiles(project)
        val codeStructureConfigCommands = generateCodeStructureConfigCommands(project)
        val sourceDependencyCommands = generateSourceDependencyCommands(project)
        return listOf(rootCommand) + moduleCommands + helperFileCommands + codeStructureConfigCommands + sourceDependencyCommands
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
        val githubDeveloperName = project.developer.githubName
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
            setJsonConfig(
                path,
                listOf(".*/src/main/(kotlin|java)/.*\\.(kt|java)"),
                "sourceFileRegexPatterns",
                "include"
            ),
            setJsonConfig(path, emptyList<String>(), "sourceFileRegexPatterns", "exclude"),
            setJsonConfig(path, 100, "nodeLimitForGraph"),
            setJsonConfig(path, listOf(".*/target/.*\\.class"), "binaryFileRegexPatterns", "include"),
            setJsonConfig(path, listOf(".*/testdata/.*", ".*/generated/.*"), "binaryFileRegexPatterns", "exclude")
        )
    }

    private fun setJsonConfig(path: Path, value: Any, vararg keys: String) = SetJsonConfig(path, value, keys.toList())

    private fun generateHelperFiles(project: Project): List<Command> {
        val gitignoreCommand = generateGitIgnore()
        val unlicenseCommand = generateUnlicense()
        val scriptsDir = CreateDirectory(baseDirectory.resolve("scripts"))
        val generateDocsScript = generateDocsScript()
        val deployScript = generateDeployScript(project)
        return listOf(gitignoreCommand, unlicenseCommand, scriptsDir, generateDocsScript, deployScript)
    }

    private fun loadResource(resourcePath: String): String {
        val classLoader = this.javaClass.classLoader
        val inputStream = classLoader.getResourceAsStream(resourcePath)
            ?: throw RuntimeException("Resource not found: $resourcePath")
        return inputStream.bufferedReader().use { it.readText() }
    }

    private fun generateGitIgnore(): Command {
        val content = loadResource("generated-project-files/gitignore.txt")
        val path = baseDirectory.resolve(".gitignore")
        return WriteTextFile(path, content)
    }

    private fun generateUnlicense(): Command {
        val content = loadResource("generated-project-files/UNLICENSE.txt")
        val path = baseDirectory.resolve("UNLICENSE.txt")
        return WriteTextFile(path, content)
    }

    private fun generateDocsScript(): Command {
        val content = loadResource("generated-project-files/generate-docs.sh")
        val path = baseDirectory.resolve("scripts/generate-docs.sh")
        return WriteTextFile(path, content, executable = true)
    }

    private fun generateDeployScript(project: Project): Command {
        val localRepoRelativePath = (project.prefix + project.name).joinToString("/", postfix = "/")
        val template = loadResource("generated-project-files/deploy-to-maven-central.sh")
        val content = template.replace("{{LOCAL_REPO_PATH}}", localRepoRelativePath)
        val path = baseDirectory.resolve("scripts/deploy-to-maven-central.sh")
        return WriteTextFile(path, content, executable = true)
    }

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
            sourceDependency.moduleMapping
        )

        // Generate copy commands for each source file
        return sourceFiles.map { sourceFileInfo ->
            CopyAndTransformSourceFile(
                sourceFileInfo.sourcePath,
                sourceFileInfo.targetPath,
                transformations,
                sourceDependency.sourceProjectPath,
                sourceFileInfo.sourceModule
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
            onSourceModulesNotFound(unmappedSourceModules)
        }

        // Validate that target modules exist in target project
        val targetModules = targetProject.modules.keys
        val unmappedTargetModules = moduleMapping.values.filterNot { it in targetModules }
        if (unmappedTargetModules.isNotEmpty()) {
            onTargetModulesNotFound(unmappedTargetModules)
        }

        // Check for duplicate target modules (multiple source modules mapping to same target)
        val duplicateTargets = moduleMapping.values.groupingBy { it }.eachCount().filter { it.value > 1 }
        if (duplicateTargets.isNotEmpty()) {
            onDuplicateTargetModules(duplicateTargets.keys)
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
