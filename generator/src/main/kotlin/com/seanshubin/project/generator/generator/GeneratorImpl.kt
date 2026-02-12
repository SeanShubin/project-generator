package com.seanshubin.project.generator.generator

import com.seanshubin.project.generator.commands.*
import com.seanshubin.project.generator.core.GradlePluginSpec
import com.seanshubin.project.generator.core.Project
import com.seanshubin.project.generator.core.SourceDependency
import com.seanshubin.project.generator.gradle.GradleFileNode
import com.seanshubin.project.generator.gradle.GradleKotlinDslRenderer
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
    private val gradleFileNode: GradleFileNode,
    private val gradleRenderer: GradleKotlinDslRenderer,
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
        val gradlePluginCommands = generateGradlePluginCommands(project)
        return listOf(rootCommand) + moduleCommands + helperFileCommands + codeStructureConfigCommands + sourceDependencyCommands + gradlePluginCommands
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

        // Basic Maven operation scripts
        val buildScript = generateScript("_build.sh")
        val cleanScript = generateScript("_clean.sh")
        val testScript = generateScript("_test.sh")
        val buildSkipTestsScript = generateScript("_build-skip-tests.sh")
        val installSkipTestsScript = generateScript("_install-skip-tests.sh")

        // Wrapper scripts with timing/notifications
        val buildWrapperScript = generateScript("build.sh")
        val cleanWrapperScript = generateScript("clean.sh")
        val testWrapperScript = generateScript("test.sh")

        // Publishing scripts
        val publishScript = generatePublishScript(project)
        val publishWrapperScript = generateScript("publish.sh")

        // Combined operation scripts
        val cleanPublishScript = generateScript("clean-publish.sh")
        val cleanInstallSkipTestsScript = generateScript("clean-install-skip-tests.sh")

        // Utility scripts
        val generateDocsScript = generateScript("generate-docs.sh")
        val fetchScript = generateFetchScript(project)
        val fetchWrapperScript = generateScript("fetch-from-maven-repo-url.sh")

        // Deploy script (with local repo cleaning)
        val deployScript = generateDeployScript(project)

        return listOf(
            gitignoreCommand,
            unlicenseCommand,
            scriptsDir,
            buildScript,
            cleanScript,
            testScript,
            buildSkipTestsScript,
            installSkipTestsScript,
            buildWrapperScript,
            cleanWrapperScript,
            testWrapperScript,
            publishScript,
            publishWrapperScript,
            cleanPublishScript,
            cleanInstallSkipTestsScript,
            generateDocsScript,
            fetchScript,
            fetchWrapperScript,
            deployScript
        )
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

    private fun generateScript(scriptName: String): Command {
        val content = loadResource("generated-project-files/$scriptName")
        val path = baseDirectory.resolve("scripts").resolve(scriptName)
        return WriteTextFile(path, content, executable = true)
    }

    private fun generatePublishScript(project: Project): Command {
        val groupId = (project.prefix + project.name).joinToString(".")
        val template = loadResource("generated-project-files/_publish.sh")
        val content = template.replace("{{GROUP_ID}}", groupId)
        val path = baseDirectory.resolve("scripts").resolve("_publish.sh")
        return WriteTextFile(path, content, executable = true)
    }

    private fun generateFetchScript(project: Project): Command {
        val groupId = (project.prefix + project.name).joinToString(".")
        val artifactId = project.name.joinToString("-") + "-parent"
        val version = project.version
        val template = loadResource("generated-project-files/_fetch-from-maven-repo-url.sh")
        val content = template
            .replace("{{GROUP_ID}}", groupId)
            .replace("{{ARTIFACT_ID}}", artifactId)
            .replace("{{VERSION}}", version)
        val path = baseDirectory.resolve("scripts").resolve("_fetch-from-maven-repo-url.sh")
        return WriteTextFile(path, content, executable = true)
    }

    private fun generateDeployScript(project: Project): Command {
        val localRepoRelativePath = (project.prefix + project.name).joinToString("/", postfix = "/")
        val groupId = (project.prefix + project.name).joinToString(".")
        val template = loadResource("generated-project-files/deploy-to-maven-central.sh")
        val content = template
            .replace("{{LOCAL_REPO_PATH}}", localRepoRelativePath)
            .replace("{{GROUP_ID}}", groupId)
        val path = baseDirectory.resolve("scripts").resolve("deploy-to-maven-central.sh")
        return WriteTextFile(path, content, executable = true)
    }

    private fun generateSourceDependencyCommands(project: Project): List<Command> {
        return project.sourceDependencies.flatMap { sourceDependency ->
            generateCommandsForSourceDependency(project, sourceDependency)
        }
    }

    private fun generateCommandsForSourceDependency(
        project: Project,
        sourceDependency: SourceDependency
    ): List<Command> {
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
        // Phase 1: Validate that source modules are exported
        val exports = sourceProject.exports
        val nonExportedModules = moduleMapping.keys.filterNot { it in exports }
        if (nonExportedModules.isNotEmpty()) {
            val sourceProjectName = (sourceProject.prefix + sourceProject.name).joinToString(".")
            val availableExports = if (exports.isEmpty()) "none" else exports.joinToString(", ")
            throw IllegalArgumentException(
                """
                Cannot import non-exported modules from source project: $sourceProjectName

                Non-exported modules: ${nonExportedModules.joinToString(", ")}
                Available exports: $availableExports

                Resolution:
                  1. Remove these modules from your moduleMapping, OR
                  2. Add them to the "exports" section in $sourceProjectName's project-specification.json

                Note: Modules must be explicitly marked as exportable for other projects to import them.
                """.trimIndent()
            )
        }

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
        // Direct module transformations (e.g., jvmspec.analysis -> inversion-guard.jvmspec.analysis)
        val directTransformations = moduleMapping.map { (sourceModule, targetModule) ->
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

        // Automatic transformations for common source dependencies
        // (e.g., both projects import di-contract from kotlin-reusable)
        val automaticTransformations = buildAutomaticTransformations(sourceProject, targetProject)

        return directTransformations + automaticTransformations
    }

    private fun buildAutomaticTransformations(
        sourceProject: Project,
        targetProject: Project
    ): List<PackageTransformation> {
        val transformations = mutableListOf<PackageTransformation>()

        // Build a map of source project path to module mappings for both projects
        val sourceDepsByPath = sourceProject.sourceDependencies.associateBy { it.sourceProjectPath }
        val targetDepsByPath = targetProject.sourceDependencies.associateBy { it.sourceProjectPath }

        // Find common source dependencies (e.g., both import from kotlin-reusable)
        val commonSourcePaths = sourceDepsByPath.keys.intersect(targetDepsByPath.keys)

        for (commonSourcePath in commonSourcePaths) {
            val sourceDep = sourceDepsByPath[commonSourcePath]!!
            val targetDep = targetDepsByPath[commonSourcePath]!!

            // Load the common source project (e.g., kotlin-reusable)
            val commonSourceProject = sourceProjectLoader.loadProject(commonSourcePath)

            // Find modules that both projects import from this common source
            val sourceModules = sourceDep.moduleMapping.values.toSet()
            val targetModules = targetDep.moduleMapping.values.toSet()
            val commonModules = sourceModules.intersect(targetModules)

            // Create transformations for each common module
            for (commonModule in commonModules) {
                val sourceModuleParts = parseModuleName(commonModule)
                val targetModuleParts = parseModuleName(commonModule)

                val sourcePackage = sourceProject.prefix +
                        sourceProject.name +
                        sourceModuleParts

                val targetPackage = targetProject.prefix +
                        targetProject.name +
                        targetModuleParts

                transformations.add(PackageTransformation(sourcePackage, targetPackage))
            }
        }

        return transformations
    }

    private fun generateGradlePluginCommands(project: Project): List<Command> {
        return project.gradlePlugin.flatMap { spec ->
            generateCommandsForGradlePlugin(project, spec)
        }
    }

    private fun generateCommandsForGradlePlugin(project: Project, spec: GradlePluginSpec): List<Command> {
        val buildGradleNode = gradleFileNode.generateBuildGradle(project, spec)
        val settingsGradleNode = gradleFileNode.generateSettingsGradle(project, spec)
        val pomXmlNode = mavenXmlNode.generateGradlePluginXml(project, spec)

        val buildGradleLines = gradleRenderer.toLines(buildGradleNode)
        val settingsGradleLines = gradleRenderer.toLines(settingsGradleNode)
        val pomXmlLines = xmlRenderer.toLines(pomXmlNode)

        val buildGradlePath = baseDirectory.resolve(spec.module).resolve("build.gradle.kts")
        val settingsGradlePath = baseDirectory.resolve(spec.module).resolve("settings.gradle.kts")
        val pomXmlPath = baseDirectory.resolve(spec.module).resolve("pom.xml")
        val sourcePath = gradleModuleSourcePath(spec.module, project)
        val testPath = gradleModuleTestPath(spec.module, project)

        return listOf(
            CreateDirectory(sourcePath),
            CreateDirectory(testPath),
            WriteFile(buildGradlePath, buildGradleLines),
            WriteFile(settingsGradlePath, settingsGradleLines),
            WriteFile(pomXmlPath, pomXmlLines)
        )
    }

    private fun gradleModuleSourcePath(module: String, project: Project): Path {
        val moduleParts = parseModuleName(module)
        val pathParts = listOf(module, SRC_DIR, MAIN_DIR, project.language) +
                project.prefix + project.name + moduleParts
        return pathParts.fold(baseDirectory, Path::resolve)
    }

    private fun gradleModuleTestPath(module: String, project: Project): Path {
        val moduleParts = parseModuleName(module)
        val pathParts = listOf(module, SRC_DIR, TEST_DIR, project.language) +
                project.prefix + project.name + moduleParts
        return pathParts.fold(baseDirectory, Path::resolve)
    }

    companion object {
        private const val SRC_DIR = "src"
        private const val MAIN_DIR = "main"
        private const val TEST_DIR = "test"
    }
}
