package com.seanshubin.project.generator.domain

import java.nio.file.{Path, Paths}

import com.seanshubin.project.generator.domain.Command._
import com.seanshubin.project.generator.domain.Specification.Project

class CommandGeneratorImpl(project: Project, destinationDirectory: Path) extends CommandGenerator {
  override def generate(): Iterable[Command] = {
    val moduleCommands = project.modules.keys.toSeq.sorted.flatMap(generateModuleCommands)
    val mavenDeployableCommands = if(project.deployableToMavenCentral.getOrElse(false)){
      Seq(CreateLicense, CreateStageScript(project), CreateSettings(project.developer.mavenUserName))
    } else Seq()
    val commands = Seq(
      CreateParentPom(project),
      CreateGitIgnore
    ) ++ mavenDeployableCommands ++ moduleCommands
    commands
  }

  private def generateModuleCommands(moduleName: String): Seq[Command] = {
    val moduleDirectory = destinationDirectory.resolve(moduleName)
    val moduleParts = moduleName.split("-")

    val sourceDirParts = Seq(moduleName, "src", "main", "scala") ++ project.prefix ++ project.name ++ moduleParts
    val testDirParts = Seq(moduleName, "src", "test", "scala") ++ project.prefix ++ project.name ++ moduleParts

    val scalaSourceDir = generatePath(sourceDirParts)
    val scalaTestDir = generatePath(testDirParts)
    val mavenDeployableCommands = if(project.deployableToMavenCentral.getOrElse(false)){
      Seq(CreateJavadocOverview(project, moduleName), CreateJavadocStub(project, moduleName))
    } else Seq()
    val commands = Seq(
      EnsureDirectoryExists(scalaSourceDir),
      EnsureDirectoryExists(scalaTestDir),
      CreateModulePom(moduleDirectory, project, moduleName),
      CreateDetanglerConfig(project, moduleName)
    ) ++ mavenDeployableCommands
    commands
  }

  private def generatePath(parts: Seq[String]): Path = {
    val relativePath = Paths.get(parts.head, parts.tail: _*)
    val path = destinationDirectory.resolve(relativePath)
    path
  }
}
