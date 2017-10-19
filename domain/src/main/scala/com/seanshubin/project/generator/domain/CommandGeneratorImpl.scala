package com.seanshubin.project.generator.domain

import com.seanshubin.project.generator.domain.Specification.Project

class CommandGeneratorImpl(projectSpecification: Project) extends CommandGenerator {
  override def generate(): Iterable[Command] = {
    Seq()
  }
}
