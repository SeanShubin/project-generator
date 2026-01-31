package com.seanshubin.project.generator.domain

class MavenXmlNodeImpl(private val versionLookup: VersionLookup) : MavenXmlNode {
    override fun generateRootXml(project: Project): XmlNode {
        return projectNode(rootChildren(project))
    }

    override fun generateModuleXml(project: Project, moduleName: String, dependencies: List<String>): XmlNode {
        return projectNode(moduleChildren(project, moduleName))
    }

    private enum class DependencyType {
        INTERNAL,
        EXTERNAL
    }

    private fun getDependencyType(project: Project, dependencyName: String): DependencyType {
        return if (project.dependencies.containsKey(dependencyName)) {
            DependencyType.EXTERNAL
        } else if (project.modules.containsKey(dependencyName)) {
            DependencyType.INTERNAL
        } else {
            throw RuntimeException("Dependency not found '$dependencyName'")
        }
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
        val baseNodes = dependencyNodes + listOf(
            simpleElement("packaging", "pom"),
            globalDependencies(project),
            dependencyManagement(project),
            modules(project),
            properties(),
            build(project)
        )

        return if (project.deployableToMavenCentral) {
            baseNodes + listOf(
                simpleElement("name", "\${project.groupId}:\${project.artifactId}"),
                description(project),
                url(project),
                licenses(),
                developers(project),
                scm(project),
                profiles(project)
            )
        } else {
            baseNodes
        }
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
        val testSourceDirectory = simpleElement("testSourceDirectory", "\${project.basedir}/$SRC_DIR/$TEST_DIR/$language")
        return testSourceDirectory
    }

    private fun plugins(project: Project): XmlNode {
        val basePlugins = listOf(
            compilerPlugin(project),
            sourcePlugin(project),
            languagePlugin(project),
            codeStructurePlugin(project)
        )

        val pluginsNodeChildren = if (project.deployableToMavenCentral) {
            basePlugins + listOf(
                centralPublishingPlugin(project)
            )
        } else {
            basePlugins
        }

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

    private fun javadocPlugin(project: Project): XmlNode {
        val dependency = lookup(project, "org.apache.maven.plugins", "maven-javadoc-plugin", scope = null)
        val coordinates = dependency.toDependencyChildNodes(includeVersion = true, includeScope = false)

        val execution = createExecutionWithIdPhaseAndGoals(
            "generate-dummy-javadoc-per-maven-central-requirements",
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
            javadocPlugin(project)
        )
        val pluginsNode = element("plugins", stagePlugins)
        val buildNode = element("build", listOf(pluginsNode))
        val profile = element("profile", listOf(
            simpleElement("id", "stage"),
            buildNode
        ))
        return element("profiles", listOf(profile))
    }

    private fun modules(project: Project): XmlNode {
        val modulesNodeChildren = project.modules.map { (name, _) -> simpleElement("module", name) }
        val modulesNode = element("modules", modulesNodeChildren)
        return modulesNode
    }

    private fun dependencyManagement(project: Project): XmlNode {
        val dependencyNodeChildren = project.dependencies.map { (dependencyName, dependency) ->
            val latestDependency = lookup(project, dependency.group, dependency.artifact, dependency.scope)
            latestDependency.toDependencyNode(includeVersion = true, includeScope = true)
        }
        val dependencyNode = element("dependencies", dependencyNodeChildren)
        val dependencyManagementNode = element("dependencyManagement", listOf(dependencyNode))
        return dependencyManagementNode
    }

    private fun externalDependency(project: Project, dependencyName: String): XmlNode {
        val dependency = project.dependencies[dependencyName]
            ?: throw RuntimeException("Unable to find dependency named '$dependencyName'")
        val latestDependency = lookup(project, dependency.group, dependency.artifact, dependency.scope)
        val dependencyNode = latestDependency.toDependencyNode(includeVersion = false, includeScope = false)
        return dependencyNode
    }

    private fun internalDependency(project: Project, dependencyName: String): XmlNode {
        val dependency =
            GroupArtifactVersionScope(
                groupId(project),
                artifactId(project, dependencyName),
                "\${project.version}",
                scope = null
            )
        val dependencyNode = dependency.toDependencyNode(includeVersion = true, includeScope = false)
        return dependencyNode
    }

    private fun globalDependencies(project: Project): XmlNode {
        val dependencyNodes = project.global.map { dependencyName ->
            externalDependency(project, dependencyName)
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
        val dependencies = project.modules.getValue(moduleName)

        // Partition into internal and external
        val (internal, external) = dependencies.partition { dependencyName ->
            getDependencyType(project, dependencyName) == DependencyType.INTERNAL
        }

        // Process internal first, then external
        val internalNodes = internal.map { dependencyName ->
            internalDependency(project, dependencyName)
        }
        val externalNodes = external.map { dependencyName ->
            externalDependency(project, dependencyName)
        }
        val moduleDependenciesNodeChildren = internalNodes + externalNodes

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
        "kotlin" to ::languagePluginKotlin
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
        val scopeNodes =
            if (this.scope == null || !includeScope) emptyList() else listOf(simpleElement("scope", this.scope))
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
