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
        val sourceDirectory = simpleElement("sourceDirectory", "\${project.basedir}/src/main/$language")
        return sourceDirectory
    }

    private fun testSourceDirectory(language: String): XmlNode {
        val testSourceDirectory = simpleElement("testSourceDirectory", "\${project.basedir}/src/test/$language")
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

    private fun codeStructurePlugin(project: Project):XmlNode {
        val groupId = "com.seanshubin.code.structure"
        val artifactId = "code-structure-maven"
        val version = versionLookup.latestProductionVersion(groupId, artifactId)
        val groupNode = simpleElement("groupId", groupId)
        val artifactNode = simpleElement("artifactId", artifactId)
        val versionNode = simpleElement("version", version)
        val goalNode = simpleElement("goal", "code-structure")
        val goalsChildren = listOf(goalNode)
        val goalsNode = element("goals", goalsChildren)
        val executionNodeChildren = listOf(goalsNode)
        val executionNode = element("execution", executionNodeChildren)
        val executionsNodeChildren = listOf(executionNode)
        val executionsNode = element("executions", executionsNodeChildren)
        val configBaseNameNode = simpleElement("configBaseName", "code-structure")
        val configurationNodeChildren = listOf(configBaseNameNode)
        val configurationNode = element("configuration", configurationNodeChildren)
        val pluginNodeChildren = listOf(
            groupNode,
            artifactNode,
            versionNode,
            executionsNode,
            configurationNode
        )
        val pluginNode = element("plugin", pluginNodeChildren)
        return pluginNode
    }

    private fun languagePluginKotlin(project: Project): XmlNode {
        val compileGoalsChildren = listOf(
            simpleElement("goal", "compile")
        )
        val compileGoals = element("goals", compileGoalsChildren)
        val compileNodeChildren = listOf(
            simpleElement("id", "compile"),
            compileGoals
        )
        val compileNode = element("execution", compileNodeChildren)
        val testCompileGoalsChildren = listOf(
            simpleElement("goal", "test-compile")
        )
        val testCompileGoals = element("goals", testCompileGoalsChildren)
        val testCompileNodeChildren = listOf(
            simpleElement("id", "test-compile"),
            testCompileGoals
        )
        val testCompileNode = element("execution", testCompileNodeChildren)
        val executionsNodeChildren = listOf(
            compileNode,
            testCompileNode
        )
        val executionsNode = element("executions", executionsNodeChildren)
        val configurationNodeChildren = listOf(
            simpleElement("jvmTarget", project.javaVersion)
        )
        val configurationNode = element("configuration", configurationNodeChildren)
        val kotlinMavenPlugin = lookup(project, "org.jetbrains.kotlin", "kotlin-maven-plugin", scope = null)
        val dependencyNodes = kotlinMavenPlugin.toDependencyChildNodes(includeVersion = true, includeScope = true)
        val kotlinPluginNodeChildren = dependencyNodes + listOf(
            executionsNode,
            configurationNode
        )
        val kotlinPluginNode = element("plugin", kotlinPluginNodeChildren)
        return kotlinPluginNode
    }

    private fun sourcePlugin(project: Project): XmlNode {
        val attachSourcesGoalsChildren = listOf(
            simpleElement("goal", "jar-no-fork"),
            simpleElement("goal", "test-jar-no-fork")
        )
        val attachSourcesGoals = element("goals", attachSourcesGoalsChildren)
        val attachSourcesChildren = listOf(
            simpleElement("id", "attach-sources"),
            simpleElement("phase", "verify"),
            attachSourcesGoals
        )
        val attachSourcesNode = element("execution", attachSourcesChildren)
        val executionNodeChildren = listOf(
            attachSourcesNode
        )
        val executionNode = element("executions", executionNodeChildren)
        val mavenSourcePluginDependency =
            lookup(project, "org.apache.maven.plugins", "maven-source-plugin", scope = null)
        val mavenSourcePluginDependencyNodes =
            mavenSourcePluginDependency.toDependencyChildNodes(includeVersion = true, includeScope = true)
        val sourcePluginNodeChildren = mavenSourcePluginDependencyNodes + listOf(
            executionNode
        )
        val sourcePluginNode = element("plugin", sourcePluginNodeChildren)
        return sourcePluginNode
    }

    private fun compilerPlugin(project: Project): XmlNode {
        val configurationNodeChildren = listOf(
            simpleElement("source", project.javaVersion),
            simpleElement("target", project.javaVersion)
        )
        val configurationNode = element("configuration", configurationNodeChildren)
        val compilerGroup = "org.apache.maven.plugins"
        val compilerArtifact = "maven-compiler-plugin"
        val mavenCompilerPluginDependency = lookup(project, compilerGroup, compilerArtifact, scope = null)
        val mavenCompilerPluginDependencyNodes =
            mavenCompilerPluginDependency.toDependencyChildNodes(includeVersion = true, includeScope = true)
        val compilerPluginChildren = mavenCompilerPluginDependencyNodes + listOf(
            configurationNode
        )
        val compilerPluginNode = element("plugin", compilerPluginChildren)
        return compilerPluginNode
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
        return listOf(
            simpleElement("modelVersion", "4.0.0"),
            simpleElement("artifactId", artifactId(project, moduleName)),
            moduleDependencies(project, moduleName),
            parentNode,
            nameNode
        )
    }

    private fun moduleDependencies(project: Project, moduleName: String): XmlNode {
        val moduleDependenciesNodeChildren = project.modules.getValue(moduleName).map { dependencyName ->
            val dependencyType = getDependencyType(project, dependencyName)
            when (dependencyType) {
                DependencyType.INTERNAL -> internalDependency(project, dependencyName)
                DependencyType.EXTERNAL -> externalDependency(project, dependencyName)
            }
        }
        val moduleDependenciesNode = element("dependencies", moduleDependenciesNodeChildren)
        return moduleDependenciesNode
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
}
