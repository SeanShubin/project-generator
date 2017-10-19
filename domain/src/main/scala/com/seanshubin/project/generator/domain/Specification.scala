package com.seanshubin.project.generator.domain

object Specification {

  case class Project(pattern: String,
                     name: Seq[String],
                     description: String,
                     groupPrefix: Seq[String],
                     version: String,
                     dependencies: Map[String, Dependency],
                     modules: Map[String, Module])

  case class Dependency(group: String, artifact: String)

  case class Module(dependencies: Seq[String])

}
