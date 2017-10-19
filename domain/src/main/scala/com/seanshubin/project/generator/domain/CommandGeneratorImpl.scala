package com.seanshubin.project.generator.domain

import com.seanshubin.project.generator.domain.Specification.ProjectSpecification

class CommandGeneratorImpl(projectSpecification: ProjectSpecification) extends CommandGenerator {
  override def generate(): Iterable[Command] = {
    Seq()
  }
}
