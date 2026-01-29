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
        return dependencyNodes + listOf(
            simpleElement("packaging", "pom"),
            globalDependencies(project),
            dependencyManagement(project),
            modules(project),
            properties(),
            build(project)
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
        val testSourceDirectory = simpleElement("testSourceDirectory", "\${project.basedir}/$SRC_DIR/$TEST_DIR/$language")
        return testSourceDirectory
    }

    private fun plugins(project: Project): XmlNode {
        val pluginsNodeChildren = listOf(
            compilerPlugin(project),
            sourcePlugin(project),
            languagePlugin(project),
            codeStructurePlugin(project)
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
        val goals = listOf("code-structure")
        val configEntries = mapOf("configBaseName" to "code-structure")
        return createPluginWithGoalsAndConfig(groupId, artifactId, version, goals, configEntries)
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

    private fun assemblyPlugin(project: Project, entryPoint: String): XmlNode {
        val dependency = lookup(project, ASSEMBLY_PLUGIN_GROUP, ASSEMBLY_PLUGIN_ARTIFACT, scope = null)
        val coordinates = dependency.toDependencyChildNodes(includeVersion = true, includeScope = false)
        val configuration = createAssemblyConfiguration(entryPoint)
        val execution = createExecutionWithIdPhaseAndGoals(
            ASSEMBLY_EXECUTION_ID,
            ASSEMBLY_PHASE_PACKAGE,
            listOf(ASSEMBLY_GOAL_SINGLE)
        )
        val executions = element("executions", listOf(execution))
        return element("plugin", coordinates + listOf(configuration, executions))
    }

    private fun createAssemblyConfiguration(entryPoint: String): XmlNode {
        val manifest = element("manifest", listOf(simpleElement("mainClass", entryPoint)))
        val archive = element("archive", listOf(manifest))
        val descriptorRef = simpleElement("descriptorRef", ASSEMBLY_DESCRIPTOR_JAR_WITH_DEPENDENCIES)
        val descriptorRefs = element("descriptorRefs", listOf(descriptorRef))
        return element("configuration", listOf(descriptorRefs, archive))
    }

    private fun moduleBuild(project: Project, moduleName: String): XmlNode? {
        val entryPoint = project.entryPoints[moduleName]
        return if (entryPoint != null) {
            val pluginsNode = element("plugins", listOf(assemblyPlugin(project, entryPoint)))
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
        val baseNodes = listOfNotNull(
            simpleElement("modelVersion", "4.0.0"),
            simpleElement("artifactId", artifactId(project, moduleName)),
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
        val moduleDependenciesNodeChildren = project.modules.getValue(moduleName).map { dependencyName ->
            val dependencyType = getDependencyType(project, dependencyName)
            when (dependencyType) {
                DependencyType.INTERNAL -> internalDependency(project, dependencyName)
                DependencyType.EXTERNAL -> externalDependency(project, dependencyName)
            }
        }
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
