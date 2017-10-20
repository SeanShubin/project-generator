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

  case class CreateParentPom(destinationDirectory: Path, groupPrefix: Seq[String], name: Seq[String], description: String) extends Command {
    override def execute(commandEnvironment: CommandEnvironment): Result = {
      val files = commandEnvironment.files
      val charset = commandEnvironment.charset
      val classLoader = commandEnvironment.classLoader
      val pomFile = destinationDirectory.resolve("pom.xml")
      val groupId = (groupPrefix ++ name).mkString(".")
      val artifactId = (name ++ "parent").mkString("-")
      val templateStream = ClassLoaderUtil.getResourceAsStream(classLoader, "pom-template.xml")
      val template = IoUtil.inputStreamToString(templateStream, charset)
      val pomText = PomBuilder(
        groupId = groupId,
        artifactId = artifactId,
        name = name.mkString("-"),
        description = description).applyTemplate(template)
      files.write(pomFile, pomText.getBytes(charset))
      Success(s"generated parent pom $pomFile")
    }
  }

  case class CreateModulePom(destinationDirectory: Path, groupPrefix: Seq[String], name: Seq[String], description: String) extends Command {
    override def execute(commandEnvironment: CommandEnvironment): Result = {
      val files = commandEnvironment.files
      val charset = commandEnvironment.charset
      val classLoader = commandEnvironment.classLoader
      val pomFile = destinationDirectory.resolve("pom.xml")
      val groupId = (groupPrefix ++ name).mkString(".")
      val artifactId = name.mkString("-")
      val templateStream = ClassLoaderUtil.getResourceAsStream(classLoader, "module-template.xml")
      val template = IoUtil.inputStreamToString(templateStream, charset)
      val pomText = PomBuilder(
        groupId = groupId,
        artifactId = artifactId,
        name = name.mkString("-"),
        description = description).applyTemplate(template)
      files.write(pomFile, pomText.getBytes(charset))
      Success(s"generated module pom $pomFile")
    }
  }
}
