package com.seanshubin.project.generator.domain

import java.nio.file.Path

import com.seanshubin.project.generator.domain.Command.{CreateParentPom, EnsureDirectoryExists}
import com.seanshubin.project.generator.domain.Specification.Project

class CommandGeneratorImpl(project: Project, destinationDirectory: Path) extends CommandGenerator {
  override def generate(): Iterable[Command] = {
    val projectDirectory = destinationDirectory.resolve(project.baseDirectoryName)
    val generateModuleCommandsFunction: ((String, Specification.Module)) => Seq[Command] = x => {
      generateModuleCommands(project, x)
    }
    val moduleCommands = project.modules.flatMap(generateModuleCommandsFunction)
    Seq(
      EnsureDirectoryExists(projectDirectory),
      CreateParentPom(projectDirectory, project.namespace, project.name, project.description)
    ) ++ moduleCommands
  }

  private def generateModuleCommands(specification: Project, moduleEntry: (String, Specification.Module)): Seq[Command] = {
    Seq()
  }
}
