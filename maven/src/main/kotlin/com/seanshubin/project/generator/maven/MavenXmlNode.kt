package com.seanshubin.project.generator.maven

import com.seanshubin.project.generator.core.GradlePluginSpec
import com.seanshubin.project.generator.core.Project
import com.seanshubin.project.generator.xml.XmlNode

interface MavenXmlNode {
    fun generateRootXml(project: Project): XmlNode
    fun generateModuleXml(project: Project, moduleName: String, dependencies: List<String>): XmlNode
    fun generateGradlePluginXml(project: Project, spec: GradlePluginSpec): XmlNode
}
