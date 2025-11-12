package com.seanshubin.project.generator.console

import com.seanshubin.project.generator.contract.FilesContract
import com.seanshubin.project.generator.contract.FilesDelegate
import com.seanshubin.project.generator.domain.Environment
import com.seanshubin.project.generator.domain.EnvironmentImpl
import com.seanshubin.project.generator.domain.Generator
import com.seanshubin.project.generator.domain.GeneratorImpl
import com.seanshubin.project.generator.domain.MavenXmlNode
import com.seanshubin.project.generator.domain.MavenXmlNodeImpl
import com.seanshubin.project.generator.domain.Project
import com.seanshubin.project.generator.domain.ProjectRunner
import com.seanshubin.project.generator.domain.StringUtility
import com.seanshubin.project.generator.domain.XmlRenderer
import com.seanshubin.project.generator.domain.XmlRendererImpl
import java.nio.file.Path

class ProjectDependencies(
    project: Project,
    baseDirectory: Path
) {
    private val indent:(String)->String = StringUtility.indent
    private val xmlRenderer: XmlRenderer = XmlRendererImpl(indent)
    private val mavenXmlNode: MavenXmlNode = MavenXmlNodeImpl()
    private val generator: Generator = GeneratorImpl(xmlRenderer, baseDirectory, mavenXmlNode)
    private val files: FilesContract = FilesDelegate
    private val environment: Environment = EnvironmentImpl(files)
    val runner: ProjectRunner = ProjectRunner(generator, project, environment)
}
