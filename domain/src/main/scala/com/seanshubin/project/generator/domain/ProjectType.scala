package com.seanshubin.project.generator.domain

import scala.collection.mutable.ArrayBuffer

sealed abstract case class ProjectType(name: String) {
  ProjectType.valuesBuffer += this
}

object ProjectType {
  private val valuesBuffer = new ArrayBuffer[ProjectType]
  lazy val values: Seq[ProjectType] = valuesBuffer
  val DisjointLibrary: ProjectType = new ProjectType("disjoint-library") {}
  val HierarchicalLibrary: ProjectType = new ProjectType("hierarchical-library") {}
  val ConsoleApplication: ProjectType = new ProjectType("console-application") {}
  val WebApplication: ProjectType = new ProjectType("web-application") {}

  def fromName(name: String): ProjectType = values.find(_.name == name) match {
    case Some(projectType) => projectType
    case None =>
      val validList = values.map(_.name).mkString(", ")
      throw new RuntimeException(s"Unsupported project type '$name', expected one of: $validList")
  }
}
