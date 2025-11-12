package com.seanshubin.project.generator.domain

interface MavenXmlNode {
    fun generateRootXml(project: Project): XmlNode
    fun generateModuleXml(project: Project, moduleName: String, dependencies: List<String>): XmlNode
}
