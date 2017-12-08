package com.seanshubin.project.generator.domain

sealed trait DependencyType

object DependencyType {

  case object LibraryRequiredByProjectGenerator extends DependencyType

  case object Library extends DependencyType

  case object Module extends DependencyType

}
