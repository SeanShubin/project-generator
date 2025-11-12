package com.seanshubin.project.generator.domain

class MavenXmlNodeImpl(private val versionLookup:VersionLookup):MavenXmlNode {
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

    private fun getDependencyType(project: Project, dependencyName:String):DependencyType{
        return if(project.dependencies.containsKey(dependencyName)){
            DependencyType.EXTERNAL
        }else if(project.modules.containsKey(dependencyName)){
            DependencyType.INTERNAL
        } else {
            throw RuntimeException("Dependency not found '$dependencyName'")
        }
    }

    private fun rootChildren(project: Project): List<XmlNode> {
        return listOf(simpleElement("modelVersion", "4.0.0")) + parentNodes(project)
    }

    private fun parentNodes(project: Project): List<XmlNode> {
        val projectDependency = GroupArtifactVersion(
            groupId(project),
            artifactId(project, "parent"),
            project.version
        )
        val dependencyNodes = projectDependency.toDependencyChildNodes()
        return dependencyNodes + listOf(
            simpleElement("packaging", "pom"),
            globalDependencies(project),
            dependencyManagement(project),
            modules(project),
            properties(),
            build(project.language, project.javaVersion)
        )
    }

    private fun build(language:String, javaVersion:String):XmlNode {
        val buildNodeChildren = listOf(
            sourceDirectory(language),
            testSourceDirectory(language),
            plugins(language, javaVersion),
        )
        val buildNode = element("build", buildNodeChildren)
        return buildNode
    }

    private fun sourceDirectory(language:String):XmlNode {
        val sourceDirectory = simpleElement("sourceDirectory", "\${project.basedir}/src/main/$language")
        return sourceDirectory
    }

    private fun testSourceDirectory(language:String):XmlNode {
        val testSourceDirectory = simpleElement("testSourceDirectory", "\${project.basedir}/src/test/$language")
        return testSourceDirectory
    }

    private fun plugins(language:String, javaVersion:String):XmlNode {
        val pluginsNodeChildren = listOf(
            compilerPlugin(javaVersion),
            sourcePlugin(),
            languagePlugin(language, javaVersion)

        )
        val pluginsNode = element("plugins", pluginsNodeChildren)
        return pluginsNode
    }

    private fun languagePlugin(language:String, javaVersion:String):XmlNode {
        val languagePluginFunction = languagePluginFunctionMap[language] ?: throw RuntimeException("Unsupported language '$language'")
        return languagePluginFunction(javaVersion)
    }

    private fun languagePluginKotlin(javaVersion:String):XmlNode {
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
            simpleElement("jvmTarget", javaVersion)
        )
        val configurationNode = element("configuration", configurationNodeChildren)
        val kotlinMavenPlugin = lookup("org.jetbrains.kotlin", "kotlin-maven-plugin")
        val dependencyNodes = kotlinMavenPlugin.toDependencyChildNodes()
        val kotlinPluginNodeChildren = dependencyNodes + listOf(
            executionsNode,
            configurationNode
        )
        val kotlinPluginNode = element("plugin", kotlinPluginNodeChildren)
        return kotlinPluginNode
    }

    private fun sourcePlugin():XmlNode {
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
        val mavenSourcePluginDependency = lookup("org.apache.maven.plugins", "maven-source-plugin")
        val mavenSourcePluginDependencyNodes = mavenSourcePluginDependency.toDependencyChildNodes()
        val sourcePluginNodeChildren = mavenSourcePluginDependencyNodes + listOf(
            executionNode
        )
        val sourcePluginNode = element("plugin", sourcePluginNodeChildren)
        return sourcePluginNode
    }

    private fun compilerPlugin(javaVersion:String):XmlNode {
        val configurationNodeChildren = listOf(
            simpleElement("source", javaVersion),
            simpleElement("target", javaVersion)
        )
        val configurationNode = element("configuration", configurationNodeChildren)
        val compilerGroup = "org.apache.maven.plugins"
        val compilerArtifact = "maven-compiler-plugin"
        val mavenCompilerPluginDependency = lookup(compilerGroup, compilerArtifact)
        val mavenCompilerPluginDependencyNodes = mavenCompilerPluginDependency.toDependencyChildNodes()
        val compilerPluginChildren = mavenCompilerPluginDependencyNodes + listOf(
            configurationNode
        )
        val compilerPluginNode = element("plugin", compilerPluginChildren)
        return compilerPluginNode
    }

    private fun properties():XmlNode{
        val sourceEncoding = simpleElement("project.build.sourceEncoding", "UTF-8")
        val propertiesNodeChildren = listOf(sourceEncoding)
        val propertiesNode =  element("properties", propertiesNodeChildren)
        return propertiesNode
    }

    private fun modules(project: Project): XmlNode {
        val modulesNodeChildren = project.modules.map { (name, _) -> simpleElement("module", name) }
        val modulesNode = element("modules", modulesNodeChildren)
        return modulesNode
    }

    private fun dependencyManagement(project:Project):XmlNode {
        val dependencyNodeChildren = project.dependencies.map { (dependencyName, dependency) ->
            val latestDependency = lookup(dependency.group, dependency.artifact)
            latestDependency.toDependencyNode()
        }
        val dependencyNode = element("dependencies", dependencyNodeChildren)
        val dependencyManagementNode = element("dependencyManagement", listOf(dependencyNode))
        return dependencyManagementNode
    }

    private fun externalDependency(project:Project, dependencyName:String):XmlNode{
        val dependency = project.dependencies[dependencyName] ?: throw RuntimeException("Unable to find dependency named '$dependencyName'")
        val latestDependency = lookup(dependency.group, dependency.artifact)
        val dependencyNode = latestDependency.toDependencyNode()
        return dependencyNode
    }

    private fun internalDependency(project:Project, dependencyName:String):XmlNode{
        val dependency = GroupArtifactVersion(groupId(project), artifactId(project, dependencyName), "\${project.version}")
        val dependencyNode = dependency.toDependencyNode()
        return dependencyNode
    }

    private fun globalDependencies(project:Project):XmlNode {
        val dependencyNodes = project.global.map { dependencyName ->
            externalDependency(project, dependencyName)
        }
        return element("dependencies", dependencyNodes)
    }

    private fun moduleChildren(project: Project, moduleName: String): List<XmlNode> {
        val parentDependency = GroupArtifactVersion(groupId(project), artifactId(project, "parent"), project.version)
        val parentNodeChildren = parentDependency.toDependencyChildNodes()
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
            when(dependencyType){
                DependencyType.INTERNAL -> internalDependency(project, dependencyName)
                DependencyType.EXTERNAL -> externalDependency(project, dependencyName)
            }
        }
        val moduleDependenciesNode= element("dependencies", moduleDependenciesNodeChildren)
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
    private fun lookup(group:String, artifact:String):GroupArtifactVersion{
        val version = versionLookup.latestProductionVersion(group, artifact)
        return GroupArtifactVersion(group, artifact, version)
    }

    private fun GroupArtifactVersion.toDependencyNode():XmlNode{
        val dependencyChildNodes = listOf(
            simpleElement("groupId", this.group),
            simpleElement("artifactId", this.artifact),
            simpleElement("version", this.version)
        )
        return XmlNode.Element("dependency", emptyList(), dependencyChildNodes)
    }

    private fun GroupArtifactVersion.toDependencyChildNodes():List<XmlNode>{
        val dependencyChildNodes = listOf(
            simpleElement("groupId", this.group),
            simpleElement("artifactId", this.artifact),
            simpleElement("version", this.version)
        )
        return dependencyChildNodes
    }
}
