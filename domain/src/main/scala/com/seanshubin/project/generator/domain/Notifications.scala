package com.seanshubin.project.generator.domain

import com.seanshubin.project.generator.domain.Specification.ProjectSpecification

trait Notifications {
  def effectiveSpecification(specification: ProjectSpecification): Unit
}
