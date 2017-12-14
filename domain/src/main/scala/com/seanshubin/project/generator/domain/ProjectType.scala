package com.seanshubin.project.generator.domain

sealed trait ProjectType

object ProjectType {

  case object DisjointLibrary extends ProjectType

  case object HierarchicalLibrary extends ProjectType

  case object ConsoleApplication extends ProjectType

  case object WebApplication extends ProjectType

}