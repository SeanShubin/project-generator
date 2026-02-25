package com.seanshubin.project.generator.maven

import com.seanshubin.project.generator.core.DependencySpec
import com.seanshubin.project.generator.core.GroupArtifact
import com.seanshubin.project.generator.core.GroupArtifactVersionScope
import com.seanshubin.project.generator.core.Project
import com.seanshubin.project.generator.xml.XmlNode

class MavenXmlNodeImpl(private val versionLookup: VersionLookup) : MavenXmlNode {
    override fun generateRootXml(project: Project): XmlNode {
        return projectNode(rootChildren(project))
    }

    override fun generateModuleXml(project: Project, moduleName: String, dependencies: List<String>): XmlNode {
        return projectNode(moduleChildren(project, moduleName))
    }

    override fun generateGradlePluginXml(project: Project, spec: com.seanshubin.project.generator.core.GradlePluginSpec): XmlNode {
        return projectNode(gradlePluginChildren(project, spec))
    }

    private fun gradlePluginChildren(project: Project, spec: com.seanshubin.project.generator.core.GradlePluginSpec): List<XmlNode> {
        val artifactId = artifactId(project, spec.module)
        return listOf(
            simpleElement("modelVersion", "4.0.0"),
            simpleElement("artifactId", artifactId),
            parentReference(project),
            simpleElement("packaging", "pom"),
            simpleElement("name", "\${project.groupId}:\${project.artifactId}"),
            description(project),
            url(project),
            licenses(),
            developers(project),
            scm(project),
            gradlePluginBuild()
        )
    }

    private fun parentReference(project: Project): XmlNode {
        return element(
            "parent", listOf(
                simpleElement("groupId", groupId(project)),
                simpleElement("artifactId", artifactId(project, "parent")),
                simpleElement("version", project.version)
            )
        )
    }

    private fun gradlePluginBuild(): XmlNode {
        val plugins = listOf(
            skipKotlinPlugin(),
            execMavenPlugin(),
            skipInstallPlugin(),
            skipDeployPlugin(),
            skipCentralPublishingPlugin()
        )
        return element("build", listOf(element("plugins", plugins)))
    }

    private fun skipKotlinPlugin(): XmlNode {
        val executions = listOf(
            gradlePluginExecution("compile", "none"),
            gradlePluginExecution("test-compile", "none")
        )
        return element(
            "plugin", listOf(
                simpleElement("groupId", "org.jetbrains.kotlin"),
                simpleElement("artifactId", "kotlin-maven-plugin"),
                element("executions", executions)
            )
        )
    }

    private fun gradlePluginExecution(id: String, phase: String): XmlNode {
        return element(
            "execution", listOf(
                simpleElement("id", id),
                simpleElement("phase", phase)
            )
        )
    }

    private fun execMavenPlugin(): XmlNode {
        val execMavenPluginVersion = versionLookup.latestProductionVersion("org.codehaus.mojo", "exec-maven-plugin")
        val executions = listOf(
            gradleBuildExecution(),
            gradlePublishLocalExecution()
        )
        return element(
            "plugin", listOf(
                simpleElement("groupId", "org.codehaus.mojo"),
                simpleElement("artifactId", "exec-maven-plugin"),
                simpleElement("version", execMavenPluginVersion),
                element("executions", executions)
            )
        )
    }

    private fun gradleBuildExecution(): XmlNode {
        val configuration = element(
            "configuration", listOf(
                simpleElement("executable", "\${basedir}/gradlew"),
                element(
                    "arguments", listOf(
                        simpleElement("argument", "build"),
                        simpleElement("argument", "--no-daemon")
                    )
                )
            )
        )
        return element(
            "execution", listOf(
                simpleElement("id", "gradle-build"),
                simpleElement("phase", "compile"),
                element("goals", listOf(simpleElement("goal", "exec"))),
                configuration
            )
        )
    }

    private fun gradlePublishLocalExecution(): XmlNode {
        val configuration = element(
            "configuration", listOf(
                simpleElement("executable", "\${basedir}/gradlew"),
                element(
                    "arguments", listOf(
                        simpleElement("argument", "publishToMavenLocal"),
                        simpleElement("argument", "--no-daemon")
                    )
                )
            )
        )
        return element(
            "execution", listOf(
                simpleElement("id", "gradle-publish-local"),
                simpleElement("phase", "install"),
                element("goals", listOf(simpleElement("goal", "exec"))),
                configuration
            )
        )
    }

    private fun skipInstallPlugin(): XmlNode {
        return element(
            "plugin", listOf(
                simpleElement("groupId", "org.apache.maven.plugins"),
                simpleElement("artifactId", "maven-install-plugin"),
                element("configuration", listOf(simpleElement("skip", "true")))
            )
        )
    }

    private fun skipDeployPlugin(): XmlNode {
        return element(
            "plugin", listOf(
                simpleElement("groupId", "org.apache.maven.plugins"),
                simpleElement("artifactId", "maven-deploy-plugin"),
                element("configuration", listOf(simpleElement("skip", "true")))
            )
        )
    }

    private fun skipCentralPublishingPlugin(): XmlNode {
        val execution = element(
            "execution", listOf(
                simpleElement("id", "injected-central-publishing"),
                simpleElement("phase", "none")
            )
        )
        return element(
            "plugin", listOf(
                simpleElement("groupId", "org.sonatype.central"),
                simpleElement("artifactId", "central-publishing-maven-plugin"),
                element("configuration", listOf(simpleElement("skipPublishing", "true"))),
                element("executions", listOf(execution))
            )
        )
    }


    private fun rootChildren(project: Project): List<XmlNode> {
        return listOf(simpleElement("modelVersion", "4.0.0")) + parentNodes(project)
    }

    private fun parentNodes(project: Project): List<XmlNode> {
        val projectDependency = GroupArtifactVersionScope(
            groupId(project),
            artifactId(project, "parent"),
            project.version,
            scope = null
        )
        val dependencyNodes = projectDependency.toDependencyChildNodes(includeVersion = true, includeScope = true)
        return dependencyNodes + listOf(
            simpleElement("packaging", "pom"),
            globalDependencies(project),
            dependencyManagement(project),
            modules(project),
            properties(),
            build(project),
            simpleElement("name", "\${project.groupId}:\${project.artifactId}"),
            description(project),
            url(project),
            licenses(),
            developers(project),
            scm(project),
            profiles(project)
        )
    }

    private fun build(project: Project): XmlNode {
        val buildNodeChildren = listOf(
            sourceDirectory(project.language),
            testSourceDirectory(project.language),
            plugins(project),
        )
        val buildNode = element("build", buildNodeChildren)
        return buildNode
    }

    private fun sourceDirectory(language: String): XmlNode {
        val sourceDirectory = simpleElement("sourceDirectory", "\${project.basedir}/$SRC_DIR/$MAIN_DIR/$language")
        return sourceDirectory
    }

    private fun testSourceDirectory(language: String): XmlNode {
        val testSourceDirectory =
            simpleElement("testSourceDirectory", "\${project.basedir}/$SRC_DIR/$TEST_DIR/$language")
        return testSourceDirectory
    }

    private fun plugins(project: Project): XmlNode {
        val pluginsNodeChildren = listOf(
            compilerPlugin(project),
            sourcePlugin(project),
            languagePlugin(project),
            codeStructurePlugin(project),
            centralPublishingPlugin(project)
        )

        val pluginsNode = element("plugins", pluginsNodeChildren)
        return pluginsNode
    }

    private fun languagePlugin(project: Project): XmlNode {
        val languagePluginFunction =
            languagePluginFunctionMap[project.language]
                ?: throw RuntimeException("Unsupported language '${project.language}'")
        return languagePluginFunction(project)
    }

    private fun codeStructurePlugin(project: Project): XmlNode {
        val groupId = "com.seanshubin.code.structure"
        val artifactId = "code-structure-maven"
        val version = versionLookup.latestProductionVersion(groupId, artifactId)
        val coordinates = createPluginCoordinates(groupId, artifactId, version)
        val inherited = simpleElement("inherited", "false")
        val goals = listOf("analyze")
        val phase = "verify"
        val execution = createExecutionWithGoals(goals, phase)
        val executions = element("executions", listOf(execution))
        val configEntries = mapOf("configBaseName" to "code-structure")
        val configuration = createConfigurationFromEntries(configEntries)
        return element("plugin", coordinates + listOf(inherited, executions, configuration))
    }

    private fun centralPublishingPlugin(project: Project): XmlNode {
        val dependency = lookup(project, "org.sonatype.central", "central-publishing-maven-plugin", scope = null)
        val coordinates = dependency.toDependencyChildNodes(includeVersion = true, includeScope = false)

        val extensionsNode = simpleElement("extensions", "true")
        val configuration = createConfigurationFromEntries(mapOf("publishingServerId" to "central"))

        return element("plugin", coordinates + listOf(extensionsNode, configuration))
    }

    private fun gpgPlugin(project: Project): XmlNode {
        val dependency = lookup(project, "org.apache.maven.plugins", "maven-gpg-plugin", scope = null)
        val coordinates = dependency.toDependencyChildNodes(includeVersion = true, includeScope = false)

        val execution = createExecutionWithIdPhaseAndGoals("sign-artifacts", "verify", listOf("sign"))
        val executions = element("executions", listOf(execution))

        return element("plugin", coordinates + listOf(executions))
    }

    private fun documentationPlugin(project: Project): XmlNode {
        return when (project.language.lowercase()) {
            "kotlin" -> dokkaPlugin(project)
            "scala" -> scaladocPlugin(project)
            else -> javadocPlugin(project)
        }
    }

    private fun dokkaPlugin(project: Project): XmlNode {
        val dependency = lookup(project, "org.jetbrains.dokka", "dokka-maven-plugin", scope = null)
        val coordinates = dependency.toDependencyChildNodes(includeVersion = true, includeScope = false)

        val execution = createExecutionWithIdPhaseAndGoals(
            "generate-dokka-javadoc",
            "package",
            listOf("javadocJar")
        )
        val executions = element("executions", listOf(execution))

        return element("plugin", coordinates + listOf(executions))
    }

    private fun scaladocPlugin(project: Project): XmlNode {
        val dependency = lookup(project, "net.alchim31.maven", "scala-maven-plugin", scope = null)
        val coordinates = dependency.toDependencyChildNodes(includeVersion = true, includeScope = false)

        val execution = createExecutionWithIdPhaseAndGoals(
            "generate-scaladoc",
            "package",
            listOf("doc-jar")
        )
        val executions = element("executions", listOf(execution))

        return element("plugin", coordinates + listOf(executions))
    }

    private fun javadocPlugin(project: Project): XmlNode {
        val dependency = lookup(project, "org.apache.maven.plugins", "maven-javadoc-plugin", scope = null)
        val coordinates = dependency.toDependencyChildNodes(includeVersion = true, includeScope = false)

        val execution = createExecutionWithIdPhaseAndGoals(
            "generate-javadoc",
            "package",
            listOf("jar")
        )
        val executions = element("executions", listOf(execution))

        return element("plugin", coordinates + listOf(executions))
    }

    private fun createPluginWithGoalsAndConfig(
        groupId: String,
        artifactId: String,
        version: String,
        goals: List<String>,
        configEntries: Map<String, String>
    ): XmlNode {
        val coordinates = createPluginCoordinates(groupId, artifactId, version)
        val executions = createExecutionsWithGoals(goals)
        val configuration = createConfigurationFromEntries(configEntries)
        return element("plugin", coordinates + listOf(executions, configuration))
    }

    private fun createPluginCoordinates(groupId: String, artifactId: String, version: String): List<XmlNode> {
        return listOf(
            simpleElement("groupId", groupId),
            simpleElement("artifactId", artifactId),
            simpleElement("version", version)
        )
    }

    private fun createExecutionsWithGoals(goals: List<String>): XmlNode {
        val goalNodes = goals.map { goal -> simpleElement("goal", goal) }
        val goalsNode = element("goals", goalNodes)
        val executionNode = element("execution", listOf(goalsNode))
        return element("executions", listOf(executionNode))
    }

    private fun createPluginWithGoalsPhaseAndConfig(
        groupId: String,
        artifactId: String,
        version: String,
        goals: List<String>,
        phase: String,
        configEntries: Map<String, String>
    ): XmlNode {
        val coordinates = createPluginCoordinates(groupId, artifactId, version)
        val execution = createExecutionWithGoals(goals, phase)
        val executions = element("executions", listOf(execution))
        val configuration = createConfigurationFromEntries(configEntries)
        return element("plugin", coordinates + listOf(executions, configuration))
    }

    private fun createExecutionWithGoals(goals: List<String>, phase: String): XmlNode {
        val goalNodes = goals.map { goal -> simpleElement("goal", goal) }
        val goalsNode = element("goals", goalNodes)
        val phaseNode = simpleElement("phase", phase)
        return element("execution", listOf(goalsNode, phaseNode))
    }

    private fun createConfigurationFromEntries(entries: Map<String, String>): XmlNode {
        val configNodes = entries.map { (key, value) -> simpleElement(key, value) }
        return element("configuration", configNodes)
    }

    private fun languagePluginKotlin(project: Project): XmlNode {
        val dependency = lookup(project, "org.jetbrains.kotlin", "kotlin-maven-plugin", scope = null)
        val coordinates = dependency.toDependencyChildNodes(includeVersion = true, includeScope = true)
        val compileExecution = createExecutionWithIdAndGoals("compile", listOf("compile"))
        val testCompileExecution = createExecutionWithIdAndGoals("test-compile", listOf("test-compile"))
        val executions = element("executions", listOf(compileExecution, testCompileExecution))
        val configuration = createConfigurationFromEntries(mapOf("jvmTarget" to project.javaVersion))
        return element("plugin", coordinates + listOf(executions, configuration))
    }

    private fun languagePluginScala(project: Project): XmlNode {
        val dependency = lookup(project, "net.alchim31.maven", "scala-maven-plugin", scope = null)
        val coordinates = dependency.toDependencyChildNodes(includeVersion = true, includeScope = true)

        val compileGoal = simpleElement("goal", "compile")
        val testCompileGoal = simpleElement("goal", "testCompile")
        val goals = element("goals", listOf(compileGoal, testCompileGoal))
        val execution = element("execution", listOf(goals))
        val executions = element("executions", listOf(execution))

        val jvmArg1 = simpleElement("jvmArg", "-Xms64m")
        val jvmArg2 = simpleElement("jvmArg", "-Xmx1024m")
        val jvmArgs = element("jvmArgs", listOf(jvmArg1, jvmArg2))

        val arg1 = simpleElement("arg", "-unchecked")
        val arg2 = simpleElement("arg", "-deprecation")
        val arg3 = simpleElement("arg", "-feature")
        val args = element("args", listOf(arg1, arg2, arg3))

        val configuration = element("configuration", listOf(jvmArgs, args))

        return element("plugin", coordinates + listOf(executions, configuration))
    }

    private fun createExecutionWithIdAndGoals(id: String, goals: List<String>): XmlNode {
        val goalNodes = goals.map { goal -> simpleElement("goal", goal) }
        val goalsNode = element("goals", goalNodes)
        return element("execution", listOf(simpleElement("id", id), goalsNode))
    }

    private fun sourcePlugin(project: Project): XmlNode {
        val dependency = lookup(project, "org.apache.maven.plugins", "maven-source-plugin", scope = null)
        val coordinates = dependency.toDependencyChildNodes(includeVersion = true, includeScope = true)
        val goals = listOf("jar-no-fork", "test-jar-no-fork")
        val execution = createExecutionWithIdPhaseAndGoals("attach-sources", "verify", goals)
        val executions = element("executions", listOf(execution))
        return element("plugin", coordinates + listOf(executions))
    }

    private fun createExecutionWithIdPhaseAndGoals(id: String, phase: String, goals: List<String>): XmlNode {
        val goalNodes = goals.map { goal -> simpleElement("goal", goal) }
        val goalsNode = element("goals", goalNodes)
        val executionChildren = listOf(
            simpleElement("id", id),
            simpleElement("phase", phase),
            goalsNode
        )
        return element("execution", executionChildren)
    }

    private fun compilerPlugin(project: Project): XmlNode {
        val dependency = lookup(project, "org.apache.maven.plugins", "maven-compiler-plugin", scope = null)
        val coordinates = dependency.toDependencyChildNodes(includeVersion = true, includeScope = true)
        val configEntries = mapOf(
            "source" to project.javaVersion,
            "target" to project.javaVersion
        )
        val configuration = createConfigurationFromEntries(configEntries)
        return element("plugin", coordinates + listOf(configuration))
    }

    private fun assemblyPlugin(project: Project, moduleName: String, entryPoint: String): XmlNode {
        val dependency = lookup(project, ASSEMBLY_PLUGIN_GROUP, ASSEMBLY_PLUGIN_ARTIFACT, scope = null)
        val coordinates = dependency.toDependencyChildNodes(includeVersion = true, includeScope = false)
        val configuration = createAssemblyConfiguration(project, moduleName, entryPoint)
        val execution = createExecutionWithIdPhaseAndGoals(
            ASSEMBLY_EXECUTION_ID,
            ASSEMBLY_PHASE_PACKAGE,
            listOf(ASSEMBLY_GOAL_SINGLE)
        )
        val executions = element("executions", listOf(execution))
        return element("plugin", coordinates + listOf(configuration, executions))
    }

    private fun createAssemblyConfiguration(project: Project, moduleName: String, entryPoint: String): XmlNode {
        val finalName = artifactId(project, moduleName)
        val appendAssemblyId = simpleElement("appendAssemblyId", "false")
        val finalNameNode = simpleElement("finalName", finalName)
        val manifest = element("manifest", listOf(simpleElement("mainClass", entryPoint)))
        val archive = element("archive", listOf(manifest))
        val descriptorRef = simpleElement("descriptorRef", ASSEMBLY_DESCRIPTOR_JAR_WITH_DEPENDENCIES)
        val descriptorRefs = element("descriptorRefs", listOf(descriptorRef))
        return element("configuration", listOf(finalNameNode, appendAssemblyId, descriptorRefs, archive))
    }

    private fun mavenPluginPlugin(project: Project): XmlNode {
        val dependency = lookup(project, "org.apache.maven.plugins", "maven-plugin-plugin", scope = null)
        val coordinates = dependency.toDependencyChildNodes(includeVersion = true, includeScope = false)

        // Goal prefix derived from project name
        val goalPrefix = project.name.joinToString("-")
        val configuration = createConfigurationFromEntries(mapOf("goalPrefix" to goalPrefix))

        // Two executions: default-descriptor and help-descriptor
        val descriptorExecution = createExecutionWithIdPhaseAndGoals(
            "default-descriptor",
            "process-classes",
            listOf("descriptor")
        )
        val helpExecution = createExecutionWithIdPhaseAndGoals(
            "help-descriptor",
            "process-classes",
            listOf("helpmojo")
        )
        val executions = element("executions", listOf(descriptorExecution, helpExecution))

        return element("plugin", coordinates + listOf(configuration, executions))
    }

    private fun moduleBuild(project: Project, moduleName: String): XmlNode? {
        val entryPoint = project.entryPoints[moduleName]
        val isMavenPlugin = moduleName in project.mavenPlugin

        return if (entryPoint != null || isMavenPlugin) {
            val plugins = buildList {
                if (entryPoint != null) {
                    add(assemblyPlugin(project, moduleName, entryPoint))
                }
                if (isMavenPlugin) {
                    add(mavenPluginPlugin(project))
                }
            }
            val pluginsNode = element("plugins", plugins)
            element("build", listOf(pluginsNode))
        } else {
            null
        }
    }

    private fun properties(): XmlNode {
        val sourceEncoding = simpleElement("project.build.sourceEncoding", "UTF-8")
        val propertiesNodeChildren = listOf(sourceEncoding)
        val propertiesNode = element("properties", propertiesNodeChildren)
        return propertiesNode
    }

    private fun description(project: Project): XmlNode {
        return simpleElement("description", project.description)
    }

    private fun url(project: Project): XmlNode {
        val githubUrl = "https://github.com/${project.developer.githubName}/${project.name.joinToString("-")}"
        return simpleElement("url", githubUrl)
    }

    private fun licenses(): XmlNode {
        val licenseChildren = listOf(
            simpleElement("name", "Unlicense"),
            simpleElement("url", "http://unlicense.org/")
        )
        val licenseNode = element("license", licenseChildren)
        return element("licenses", listOf(licenseNode))
    }

    private fun developers(project: Project): XmlNode {
        val developerChildren = listOf(
            simpleElement("name", project.developer.name),
            simpleElement("organization", project.developer.organization),
            simpleElement("organizationUrl", project.developer.url)
        )
        val developerNode = element("developer", developerChildren)
        return element("developers", listOf(developerNode))
    }

    private fun scm(project: Project): XmlNode {
        val repoName = project.name.joinToString("-")
        val githubName = project.developer.githubName
        val connection = "git@github.com:$githubName/$repoName.git"
        val url = "https://github.com/$githubName/$repoName"

        val scmChildren = listOf(
            simpleElement("connection", connection),
            simpleElement("developerConnection", connection),
            simpleElement("url", url)
        )
        return element("scm", scmChildren)
    }

    private fun profiles(project: Project): XmlNode {
        val stagePlugins = listOf(
            gpgPlugin(project),
            documentationPlugin(project)
        )
        val pluginsNode = element("plugins", stagePlugins)
        val buildNode = element("build", listOf(pluginsNode))
        val profile = element(
            "profile", listOf(
                simpleElement("id", "stage"),
                buildNode
            )
        )
        return element("profiles", listOf(profile))
    }

    private fun modules(project: Project): XmlNode {
        val modulesNodeChildren = project.modules.map { (name, _) -> simpleElement("module", name) }
        val modulesNode = element("modules", modulesNodeChildren)
        return modulesNode
    }

    private fun dependencyManagement(project: Project): XmlNode {
        // Only External dependencies go in dependencyManagement
        // Internal dependencies are managed by the parent pom version
        val dependencyNodeChildren = project.dependencies.mapNotNull { (dependencyName, dependency) ->
            when (dependency) {
                is DependencySpec.External -> {
                    val latestDependency = lookup(project, dependency.group, dependency.artifact, dependency.scope)
                    latestDependency.toDependencyNode(includeVersion = true, includeScope = true)
                }
                is DependencySpec.Internal -> null // Internal dependencies don't go in dependencyManagement
            }
        }
        val dependencyNode = element("dependencies", dependencyNodeChildren)
        val dependencyManagementNode = element("dependencyManagement", listOf(dependencyNode))
        return dependencyManagementNode
    }

    private fun externalDependency(project: Project, dependencyName: String): XmlNode {
        val dependency = project.dependencies[dependencyName]
            ?: throw RuntimeException("Unable to find dependency named '$dependencyName'")

        return when (dependency) {
            is DependencySpec.External -> {
                val latestDependency = lookup(project, dependency.group, dependency.artifact, dependency.scope)
                latestDependency.toDependencyNode(includeVersion = false, includeScope = false)
            }
            is DependencySpec.Internal -> {
                throw RuntimeException("Expected external dependency but found internal module '$dependencyName'")
            }
        }
    }

    private fun internalDependency(project: Project, moduleName: String, scope: String?): XmlNode {
        val dependency =
            GroupArtifactVersionScope(
                groupId(project),
                artifactId(project, moduleName),
                "\${project.version}",
                scope = scope
            )
        val dependencyNode = dependency.toDependencyNode(includeVersion = true, includeScope = true)
        return dependencyNode
    }

    private fun globalDependencies(project: Project): XmlNode {
        // Global dependencies must be external (from Maven)
        // Internal module dependencies should be declared per-module, not globally
        val dependencyNodes = project.global.map { dependencyName ->
            val dependency = project.dependencies[dependencyName]
                ?: throw RuntimeException("Global dependency '$dependencyName' not found in dependencies section")

            when (dependency) {
                is DependencySpec.External -> externalDependency(project, dependencyName)
                is DependencySpec.Internal -> throw RuntimeException(
                    "Global dependency '$dependencyName' cannot be an internal module. " +
                    "Internal dependencies should be declared per-module in the modules section."
                )
            }
        }
        return element("dependencies", dependencyNodes)
    }

    private fun moduleChildren(project: Project, moduleName: String): List<XmlNode> {
        val parentDependency =
            GroupArtifactVersionScope(groupId(project), artifactId(project, "parent"), project.version, scope = null)
        val parentNodeChildren = parentDependency.toDependencyChildNodes(includeVersion = true, includeScope = false)
        val parentNode = element("parent", parentNodeChildren)
        val nameNode = simpleElement("name", "\${project.groupId}:\${project.artifactId}")
        val buildNode = moduleBuild(project, moduleName)

        // Add packaging element if this is a Maven plugin module
        val packagingNode = if (moduleName in project.mavenPlugin) {
            simpleElement("packaging", "maven-plugin")
        } else {
            null
        }

        val baseNodes = listOfNotNull(
            simpleElement("modelVersion", "4.0.0"),
            simpleElement("artifactId", artifactId(project, moduleName)),
            packagingNode,  // Inserted after artifactId if present
            moduleDependencies(project, moduleName),
            parentNode,
            nameNode
        )
        return if (buildNode != null) {
            baseNodes + buildNode
        } else {
            baseNodes
        }
    }

    private fun moduleDependencies(project: Project, moduleName: String): XmlNode? {
        val dependencyNames = project.modules.getValue(moduleName)

        // Partition dependencies into internal (modules) and external (maven artifacts)
        val internalDeps = mutableListOf<XmlNode>()
        val externalDeps = mutableListOf<XmlNode>()

        for (dependencyName in dependencyNames) {
            when {
                // Check if it's defined in dependencies section
                project.dependencies.containsKey(dependencyName) -> {
                    val spec = project.dependencies.getValue(dependencyName)
                    when (spec) {
                        is DependencySpec.External -> {
                            externalDeps.add(externalDependency(project, dependencyName))
                        }
                        is DependencySpec.Internal -> {
                            internalDeps.add(internalDependency(project, dependencyName, spec.scope))
                        }
                    }
                }
                // Check if it's a module name (not in dependencies section, but in modules section)
                project.modules.containsKey(dependencyName) -> {
                    internalDeps.add(internalDependency(project, dependencyName, scope = null))
                }
                else -> {
                    throw RuntimeException("Dependency '$dependencyName' not found in dependencies or modules")
                }
            }
        }

        // Process internal first, then external
        val moduleDependenciesNodeChildren = internalDeps + externalDeps

        return if (moduleDependenciesNodeChildren.isEmpty()) {
            null
        } else {
            element("dependencies", moduleDependenciesNodeChildren)
        }
    }

    private fun element(name: String, children: List<XmlNode>): XmlNode.Element {
        return XmlNode.Element(name, emptyList(), children)
    }

    private fun simpleElement(name: String, text: String): XmlNode.Element {
        return XmlNode.Element(name, emptyList(), listOf(XmlNode.Text(text)))
    }

    private fun groupId(project: Project): String {
        return (project.prefix + project.name).joinToString(".")
    }

    private fun artifactId(project: Project, name: String): String {
        return (project.name + listOf(name)).joinToString("-")
    }

    private fun projectNode(children: List<XmlNode>): XmlNode {
        return XmlNode.Element("project", projectAttributes, children)
    }

    private val projectAttributes = listOf(
        "xmlns" to "http://maven.apache.org/POM/4.0.0",
        "xmlns:xsi" to "http://www.w3.org/2001/XMLSchema-instance",
        "xsi:schemaLocation" to "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd"
    )

    private val languagePluginFunctionMap = mapOf(
        "kotlin" to ::languagePluginKotlin,
        "scala" to ::languagePluginScala
    )

    private fun lookup(project: Project, group: String, artifact: String, scope: String?): GroupArtifactVersionScope {
        val versionByGroupArtifact = project.versionOverrides.associate { (group, artifact, version) ->
            val groupArtifact = GroupArtifact(group, artifact)
            groupArtifact to version
        }
        val version = if (versionByGroupArtifact.containsKey(GroupArtifact(group, artifact))) {
            versionByGroupArtifact.getValue(GroupArtifact(group, artifact))
        } else {
            versionLookup.latestProductionVersion(group, artifact)
        }
        return GroupArtifactVersionScope(group, artifact, version, scope)
    }

    private fun GroupArtifactVersionScope.toDependencyNode(includeVersion: Boolean, includeScope: Boolean): XmlNode {
        val dependencyChildNodes = toDependencyChildNodes(includeVersion, includeScope)
        return XmlNode.Element("dependency", emptyList(), dependencyChildNodes)
    }

    private fun GroupArtifactVersionScope.toDependencyChildNodes(
        includeVersion: Boolean,
        includeScope: Boolean
    ): List<XmlNode> {
        val dependencyChildNodesWithoutScope = listOf(
            simpleElement("groupId", this.group),
            simpleElement("artifactId", this.artifact)
        )
        val versionNodes = if (includeVersion) listOf(simpleElement("version", this.version)) else emptyList()
        val scope = this.scope
        val scopeNodes =
            if (scope == null || !includeScope) emptyList() else listOf(simpleElement("scope", scope))
        val dependencyChildNodes = dependencyChildNodesWithoutScope + versionNodes + scopeNodes
        return dependencyChildNodes
    }

    companion object {
        private const val SRC_DIR = "src"
        private const val MAIN_DIR = "main"
        private const val TEST_DIR = "test"
        private const val ASSEMBLY_PLUGIN_GROUP = "org.apache.maven.plugins"
        private const val ASSEMBLY_PLUGIN_ARTIFACT = "maven-assembly-plugin"
        private const val ASSEMBLY_DESCRIPTOR_JAR_WITH_DEPENDENCIES = "jar-with-dependencies"
        private const val ASSEMBLY_GOAL_SINGLE = "single"
        private const val ASSEMBLY_PHASE_PACKAGE = "package"
        private const val ASSEMBLY_EXECUTION_ID = "make-assembly"
    }
}
