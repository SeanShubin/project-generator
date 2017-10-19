package com.seanshubin.project.generator.domain

import com.seanshubin.project.generator.domain.Specification.Project

trait SpecificationLoader {
  def load(name: String): Project
}
