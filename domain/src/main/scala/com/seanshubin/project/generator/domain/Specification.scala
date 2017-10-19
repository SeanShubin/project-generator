package com.seanshubin.project.generator.domain

object Specification {

  case class ProjectSpecification(pattern: String,
                                  name: Seq[String],
                                  description: String,
                                  groupPrefix: Seq[String],
                                  version: String,
                                  dependencies: Map[String, DependencySpecification],
                                  modules: Map[String, ModuleSpecification])

  case class DependencySpecification(group: String, artifact: String)

  case class ModuleSpecification(dependencies: Seq[String])

}
