package com.seanshubin.project.generator.domain

import java.nio.file.Path

import com.seanshubin.project.generator.domain.Command.{CreateModulePom, CreateParentPom, EnsureDirectoryExists}
import com.seanshubin.project.generator.domain.Specification.Project

class CommandGeneratorImpl(project: Project, destinationDirectory: Path) extends CommandGenerator {
  override def generate(): Iterable[Command] = {
    val projectDirectory = destinationDirectory.resolve(project.baseDirectoryName)
    val moduleCommands = project.modules.keys.flatMap(generateModuleCommands)
    Seq(
      EnsureDirectoryExists(projectDirectory),
      CreateParentPom(projectDirectory, project.namespace, project.name, project.description)
    ) ++ moduleCommands
  }


  private def generateModuleCommands(moduleName:String): Seq[Command] = {
    val moduleDirectory = destinationDirectory.resolve(project.baseDirectoryName).resolve(moduleName)
    val scalaSourceDir = moduleDirectory.resolve("src").resolve("main").resolve("scala")
    val scalaTestDir = moduleDirectory.resolve("src").resolve("test").resolve("scala")
    Seq(
      EnsureDirectoryExists(scalaSourceDir),
      EnsureDirectoryExists(scalaTestDir),
      CreateModulePom(moduleDirectory, project.namespace, project.name, moduleName, project.description)
    )
  }
}
