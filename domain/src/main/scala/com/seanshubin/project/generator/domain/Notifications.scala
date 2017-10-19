package com.seanshubin.project.generator.domain

import com.seanshubin.project.generator.domain.Specification.Project

trait Notifications {
  def effectiveSpecification(specification: Project): Unit
}
