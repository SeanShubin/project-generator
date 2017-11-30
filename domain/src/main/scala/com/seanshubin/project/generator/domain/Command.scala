package com.seanshubin.project.generator.domain

import java.nio.file.Path

import com.seanshubin.project.generator.domain.Result.Success

sealed trait Command {
  def execute(commandEnvironment: CommandEnvironment): Result
}

object Command {
  case class EnsureDirectoryExists(destinationDirectory: Path) extends Command {
    override def execute(commandEnvironment: CommandEnvironment): Result = {
      val files = commandEnvironment.files
      if (files.exists(destinationDirectory)) {
        Success(s"directory $destinationDirectory already exists")
      } else {
        files.createDirectories(destinationDirectory)
        Success(s"created directory $destinationDirectory")
      }
    }
  }

  case class CreateParentPom(destinationDirectory: Path, project: Specification.Project) extends Command {
    override def execute(commandEnvironment: CommandEnvironment): Result = {
      val pomGenerator = commandEnvironment.pomGenerator
      val files = commandEnvironment.files
      val charset = commandEnvironment.charset
      val pomFile = destinationDirectory.resolve("pom.xml")
      val pomText = pomGenerator.generateParent(project)
      files.write(pomFile, pomText.getBytes(charset))
      Success(s"generated parent pom $pomFile")
    }
  }

  case class CreateModulePom(destinationDirectory: Path,
                             project: Specification.Project,
                             moduleName: String) extends Command {
    override def execute(commandEnvironment: CommandEnvironment): Result = {
      val pomGenerator = commandEnvironment.pomGenerator
      val files = commandEnvironment.files
      val charset = commandEnvironment.charset
      val pomFile = destinationDirectory.resolve("pom.xml")
      val pomText = pomGenerator.generateModule(project, moduleName)
      files.write(pomFile, pomText.getBytes(charset))
      Success(s"generated module pom $pomFile")
    }
  }
}
