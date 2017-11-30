package com.seanshubin.project.generator.domain

object Specification {

  case class Project(prefix: Seq[String],
                     name: Seq[String],
                     description: String,
                     namespace: Seq[String],
                     version: String,
                     developer: Developer,
                     dependencies: Map[String, Dependency],
                     modules: Map[String, Seq[String]]) {
    def baseDirectoryName: String = name.mkString("-")
  }

  case class Dependency(group: String, artifact: String, version: String, scope: Option[String])

  case class Developer(name: String, githubName: String, organization: String, url: String)
}
