package com.seanshubin.project.generator.domain

import java.nio.file.Path

import com.seanshubin.project.generator.domain.Command.{CreateParentPom, EnsureDirectoryExists}
import com.seanshubin.project.generator.domain.Specification.Project

class CommandGeneratorImpl(specification: Project, destinationDirectory: Path) extends CommandGenerator {
  override def generate(): Iterable[Command] = {
    val projectDirectory = destinationDirectory.resolve(specification.name.mkString("-"))
    Seq(
      EnsureDirectoryExists(projectDirectory),
      CreateParentPom(projectDirectory, specification.namespace, specification.name, specification.description)
    )
  }
}
