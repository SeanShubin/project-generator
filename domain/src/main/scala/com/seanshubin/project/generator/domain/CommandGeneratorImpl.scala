package com.seanshubin.project.generator.domain

import java.nio.file.Path

import com.seanshubin.project.generator.domain.Command.{CreateParentPom, EnsureDirectoryExists}
import com.seanshubin.project.generator.domain.Specification.Project

class CommandGeneratorImpl(project: Project, destinationDirectory: Path) extends CommandGenerator {
  override def generate(): Iterable[Command] = {
    val projectDirectory = destinationDirectory.resolve(project.baseDirectoryName)
    val moduleCommands = project.modules.flatMap(generateModuleCommands)
    Seq(
      EnsureDirectoryExists(projectDirectory),
      CreateParentPom(projectDirectory, project.namespace, project.name, project.description)
    ) ++ moduleCommands
  }


  private def generateModuleCommands(moduleEntry: (String, Specification.Module)): Seq[Command] = {
    val (name, module) = moduleEntry
    val moduleDirectory = destinationDirectory.resolve(project.baseDirectoryName).resolve(name)
    val scalaSourceDir = moduleDirectory.resolve("src").resolve("main").resolve("scala")
    val scalaTestDir = moduleDirectory.resolve("src").resolve("test").resolve("scala")
    Seq(
      EnsureDirectoryExists(scalaSourceDir),
      EnsureDirectoryExists(scalaTestDir),
    )
  }
}
