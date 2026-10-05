package com.seanshubin.project.generator.generator

import com.seanshubin.project.generator.commands.Command
import com.seanshubin.project.generator.commands.CreateDirectory
import com.seanshubin.project.generator.commands.SetJsonConfig
import com.seanshubin.project.generator.commands.WriteFile
import com.seanshubin.project.generator.core.GradlePluginSpec
import com.seanshubin.project.generator.core.ModuleName
import com.seanshubin.project.generator.core.Preset
import com.seanshubin.project.generator.core.Project
import com.seanshubin.project.generator.gradle.GradleFileNode
import com.seanshubin.project.generator.gradle.GradleKotlinDslRenderer
import com.seanshubin.project.generator.maven.MavenXmlNode
import com.seanshubin.project.generator.xml.XmlRenderer
import java.nio.file.Path

/**
 * Emits a Maven project: a parent `pom.xml`, a `pom.xml` per module, JVM source
 * directories laid out to mirror the package name, and the `mvn`-based helper
 * scripts.
 *
 * Gradle plugin modules and the code-structure config live here rather than in
 * [GeneratorImpl] because both are JVM-specific: one produces a second build
 * file for the Gradle plugin portal, and the other configures a tool that reads
 * JVM bytecode.
 */
class MavenBuildSystemGenerator(
    private val baseDirectory: Path,
    private val xmlRenderer: XmlRenderer,
    private val mavenXmlNode: MavenXmlNode,
    private val gradleFileNode: GradleFileNode,
    private val gradleRenderer: GradleKotlinDslRenderer,
    private val scriptEmitter: ScriptEmitter
) : BuildSystemGenerator {
    override fun validate(project: Project) {
        if (project.preset != Preset.NONE) {
            throw IllegalArgumentException(
                "preset '${project.preset.name.lowercase()}' is not supported for maven projects. " +
                        "Presets currently only apply to cargo projects."
            )
        }
    }

    override fun rootCommands(project: Project): List<Command> {
        val xml = mavenXmlNode.generateRootXml(project)
        val lines = xmlRenderer.toLines(xml)
        val path = baseDirectory.resolve("pom.xml")
        return listOf(WriteFile(path, lines))
    }

    override fun moduleCommands(project: Project, module: String, dependencies: List<String>): List<Command> {
        val xml = mavenXmlNode.generateModuleXml(project, module, dependencies)
        val lines = xmlRenderer.toLines(xml)
        val path = baseDirectory.resolve(module).resolve("pom.xml")
        val createSourceDir = CreateDirectory(sourcePath(project, module, MAIN_DIR))
        val createTestDir = CreateDirectory(sourcePath(project, module, TEST_DIR))
        val writePomFile = WriteFile(path, lines)
        return listOf(createSourceDir, createTestDir, writePomFile)
    }

    override fun supplementalCommands(project: Project): List<Command> {
        val codeStructureCommands =
            if (project.generateCodeStructure) codeStructureConfigCommands(project) else emptyList()
        return helperFileCommands(project) + codeStructureCommands + gradlePluginCommands(project)
    }

    /**
     * A module's source root, mirroring the package name as JVM build tools
     * expect: `<module>/src/<main|test>/<language>/<prefix>/<name>/<module parts>`.
     */
    private fun sourcePath(project: Project, module: String, sourceSet: String): Path {
        val pathParts = listOf(module, SRC_DIR, sourceSet, project.language) +
                project.prefix + project.name + ModuleName.parts(module)
        return pathParts.fold(baseDirectory, Path::resolve)
    }

    private fun codeStructureConfigCommands(project: Project): List<Command> {
        val path = baseDirectory.resolve("code-structure-config.json")
        val githubRepoName = project.name.joinToString("-")
        val githubDeveloperName = project.developer?.githubName ?: githubRepoName

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

    private fun helperFileCommands(project: Project): List<Command> {
        val localRepoRelativePath = (project.prefix + project.name).joinToString("/", postfix = "/")
        val groupId = (project.prefix + project.name).joinToString(".")
        val publishReplacements = mapOf(
            "{{LOCAL_REPO_PATH}}" to localRepoRelativePath,
            "{{GROUP_ID}}" to groupId
        )
        return listOf(
            scriptEmitter.fileAtRoot("gitignore.txt", ".gitignore"),
            scriptEmitter.scriptsDirectory(),

            // Basic Maven operation scripts
            scriptEmitter.script("_build.sh"),
            scriptEmitter.script("_clean.sh"),
            scriptEmitter.script("_test.sh"),
            scriptEmitter.script("_build-skip-tests.sh"),

            // Wrapper scripts with timing/notifications
            scriptEmitter.script("build.sh"),
            scriptEmitter.script("clean.sh"),
            scriptEmitter.script("test.sh"),

            // Publishing scripts (cleans local repo cache, runs mvn clean, then deploys)
            scriptEmitter.script("_publish.sh", publishReplacements),
            scriptEmitter.script("publish.sh"),

            // Combined operation scripts
            scriptEmitter.script("clean-publish.sh"),

            // Utility scripts
            scriptEmitter.script("generate-docs.sh")
        )
    }

    private fun gradlePluginCommands(project: Project): List<Command> =
        project.gradlePlugin.flatMap { spec ->
            commandsForGradlePlugin(project, spec)
        }

    private fun commandsForGradlePlugin(project: Project, spec: GradlePluginSpec): List<Command> {
        val buildGradleNode = gradleFileNode.generateBuildGradle(project, spec)
        val settingsGradleNode = gradleFileNode.generateSettingsGradle(project, spec)
        val pomXmlNode = mavenXmlNode.generateGradlePluginXml(project, spec)

        val buildGradleLines = gradleRenderer.toLines(buildGradleNode)
        val settingsGradleLines = gradleRenderer.toLines(settingsGradleNode)
        val pomXmlLines = xmlRenderer.toLines(pomXmlNode)

        val buildGradlePath = baseDirectory.resolve(spec.module).resolve("build.gradle.kts")
        val settingsGradlePath = baseDirectory.resolve(spec.module).resolve("settings.gradle.kts")
        val pomXmlPath = baseDirectory.resolve(spec.module).resolve("pom.xml")

        return listOf(
            CreateDirectory(sourcePath(project, spec.module, MAIN_DIR)),
            CreateDirectory(sourcePath(project, spec.module, TEST_DIR)),
            WriteFile(buildGradlePath, buildGradleLines),
            WriteFile(settingsGradlePath, settingsGradleLines),
            WriteFile(pomXmlPath, pomXmlLines)
        )
    }

    companion object {
        private const val SRC_DIR = "src"
        private const val MAIN_DIR = "main"
        private const val TEST_DIR = "test"
    }
}
