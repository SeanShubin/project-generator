package com.seanshubin.project.generator.domain

import com.seanshubin.project.generator.domain.Specification.ProjectSpecification

trait SpecificationLoader {
  def load(name: String): ProjectSpecification
}
