package com.seanshubin.project.generator.domain

import scala.collection.immutable.ListMap

object Specification {

  case class Project(`type`: ProjectType,
                     prefix: Seq[String],
                     name: Seq[String],
                     description: String,
                     version: String,
                     developer: Developer,
                     dependencies: ListMap[String, Dependency],
                     modules: ListMap[String, Seq[String]],
                     primaryModule: Option[String]) {
    def baseDirectoryName: String = name.mkString("-")
  }

  case class Dependency(group: String, artifact: String, version: String, scope: Option[String])

  case class Developer(name: String, githubName: String, organization: String, url: String)
}
