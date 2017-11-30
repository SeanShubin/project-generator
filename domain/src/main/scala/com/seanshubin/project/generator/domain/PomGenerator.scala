package com.seanshubin.project.generator.domain

trait PomGenerator {
  def generateParent(project: Specification.Project): String

  def generateModule(project: Specification.Project, moduleName: String): String
}
