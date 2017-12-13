package com.seanshubin.project.generator.domain

trait PomGenerator {
  def generateParent(project: Specification.Project): Seq[String]

  def generateModule(project: Specification.Project, moduleName: String): Seq[String]
}
