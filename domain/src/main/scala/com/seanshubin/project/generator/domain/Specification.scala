package com.seanshubin.project.generator.domain

import scala.collection.immutable.ListMap

object Specification {

  case class Project(prefix: Seq[String],
                     name: Seq[String],
                     description: String,
                     version: String,
                     developer: Developer,
                     dependencies: ListMap[String, Dependency],
                     global: Seq[String],
                     modules: ListMap[String, Seq[String]],
                     detangler: Seq[String],
                     consoleEntryPoint: Map[String, String],
                     mavenPlugin: Seq[String],
                     primary: Option[String],
                     javaVersion: Option[String]) {
    def baseDirectoryName: String = name.mkString("-")

    def nullSafe: Project = copy(
      global = Option(global).getOrElse(Seq()),
      detangler = Option(detangler).getOrElse(Seq()),
      consoleEntryPoint = Option(consoleEntryPoint).getOrElse(Map()),
      mavenPlugin = Option(mavenPlugin).getOrElse(Seq()),
    )
  }

  case class Dependency(group: String, artifact: String, lockedAtVersion: Option[String], scope: Option[String])

  case class Developer(name: String, githubName: String, mavenUserName: String, organization: String, url: String)

}
