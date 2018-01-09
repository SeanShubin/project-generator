package com.seanshubin.project.generator.domain

import scala.collection.immutable.ListMap

object Specification {

  case class Project(prefix: Seq[String],
                     name: Seq[String],
                     description: String,
                     version: String,
                     developer: Developer,
                     dependencies: ListMap[String, Dependency],
                     modules: ListMap[String, Seq[String]],
                     detangler: Seq[String],
                     consoleEntryPoint: Map[String, String],
                     mavenPlugin: Seq[String]) {
    def baseDirectoryName: String = name.mkString("-")

    def nullSafe: Project = copy(
      detangler = Option(detangler).getOrElse(Seq()),
      consoleEntryPoint = Option(consoleEntryPoint).getOrElse(Map()),
      mavenPlugin = Option(mavenPlugin).getOrElse(Seq()),
    )
  }

  case class Dependency(group: String, artifact: String, scope: Option[String])

  case class Developer(name: String, githubName: String, mavenUserName: String, organization: String, url: String)

}
