package com.seanshubin.project.generator.domain

import java.nio.file.Path

import com.seanshubin.project.generator.domain.Specification.Project

trait SpecificationLoader {
  def load(path: Path): Project
}
