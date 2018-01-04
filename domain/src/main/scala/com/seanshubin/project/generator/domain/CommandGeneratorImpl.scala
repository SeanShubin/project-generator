package com.seanshubin.project.generator.domain

import java.nio.file.Path

import com.seanshubin.project.generator.domain.Command._
import com.seanshubin.project.generator.domain.Specification.Project

class CommandGeneratorImpl(project: Project, destinationDirectory: Path) extends CommandGenerator {
  override def generate(): Iterable[Command] = {
    val moduleCommands = project.modules.keys.toSeq.sorted.flatMap(generateModuleCommands)
    val commands = Seq(
      EnsureDirectoryExists(destinationDirectory),
      CreateParentPom(project),
      CreateLicense,
      CreateSettings(project.developer.mavenUserName),
      CreateDetanglerConfig(project)
    ) ++ moduleCommands
    commands
  }

  private def generateModuleCommands(moduleName: String): Seq[Command] = {
    val moduleDirectory = destinationDirectory.resolve(moduleName)
    val scalaSourceDir = moduleDirectory.resolve("src").resolve("main").resolve("scala")
    val scalaTestDir = moduleDirectory.resolve("src").resolve("test").resolve("scala")
    val commands = Seq(
      EnsureDirectoryExists(scalaSourceDir),
      EnsureDirectoryExists(scalaTestDir),
      CreateModulePom(moduleDirectory, project, moduleName)
    )
    commands
  }
}
