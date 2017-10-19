package com.seanshubin.project.generator.domain

import java.nio.file.Path

import com.seanshubin.project.generator.domain.Command.{CreateParentPom, EnsureDirectoryExists}
import com.seanshubin.project.generator.domain.Specification.Project

class CommandGeneratorImpl(specification: Project, destinationDirectory: Path) extends CommandGenerator {
  override def generate(): Iterable[Command] = {
    Seq(
      EnsureDirectoryExists(destinationDirectory),
      CreateParentPom(destinationDirectory, specification.groupPrefix, specification.name, specification.description)
    )
  }
}
