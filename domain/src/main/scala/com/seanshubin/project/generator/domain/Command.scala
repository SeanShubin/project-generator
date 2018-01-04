package com.seanshubin.project.generator.domain

import java.nio.file.Path

import com.seanshubin.project.generator.domain.GlobalConstants.charset
import com.seanshubin.project.generator.domain.Result.Success

import scala.collection.JavaConverters._

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

  case class CreateParentPom(project: Specification.Project) extends Command {
    override def execute(commandEnvironment: CommandEnvironment): Result = {
      val pomGenerator = commandEnvironment.pomGenerator
      val files = commandEnvironment.files
      val pomFile = commandEnvironment.baseDirectory.resolve("pom.xml")
      val pomLines = pomGenerator.generateParent(project)
      files.write(pomFile, pomLines.asJava, charset)
      Success(s"generated parent pom $pomFile")
    }
  }

  case class CreateModulePom(destinationDirectory: Path,
                             project: Specification.Project,
                             moduleName: String) extends Command {
    override def execute(commandEnvironment: CommandEnvironment): Result = {
      val pomGenerator = commandEnvironment.pomGenerator
      val files = commandEnvironment.files
      val pomFile = destinationDirectory.resolve("pom.xml")
      val pomLines = pomGenerator.generateModule(project, moduleName)
      files.write(pomFile, pomLines.asJava, charset)
      Success(s"generated module pom $pomFile")
    }
  }

  case object CreateLicense extends Command {
    override def execute(commandEnvironment: CommandEnvironment): Result = {
      val unlicenseText =
        """This is free and unencumbered software released into the public domain.
          |
          |Anyone is free to copy, modify, publish, use, compile, sell, or
          |distribute this software, either in source code form or as a compiled
          |binary, for any purpose, commercial or non-commercial, and by any
          |means.
          |
          |In jurisdictions that recognize copyright laws, the author or authors
          |of this software dedicate any and all copyright interest in the
          |software to the public domain. We make this dedication for the benefit
          |of the public at large and to the detriment of our heirs and
          |successors. We intend this dedication to be an overt act of
          |relinquishment in perpetuity of all present and future rights to this
          |software under copyright law.
          |
          |THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
          |EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
          |MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
          |IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
          |OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
          |ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
          |OTHER DEALINGS IN THE SOFTWARE.
          |
          |For more information, please refer to <http://unlicense.org/>
          |""".stripMargin
      val files = commandEnvironment.files
      val path = commandEnvironment.baseDirectory.resolve("UNLICENSE.txt")
      val bytes = unlicenseText.getBytes(GlobalConstants.charset)
      files.write(path, bytes)
      Success(s"generated license file at $path")
    }
  }

  // deploy-to-maven-central-settings.xml
  case class CreateSettings(mavenUserName: String) extends Command {
    override def execute(commandEnvironment: CommandEnvironment): Result = ???
  }

  // detangler.txt
  case class CreateDetanglerConfig(project: Specification.Project) extends Command {
    override def execute(commandEnvironment: CommandEnvironment): Result = ???
  }

  // stage.sh
  case class CreateStageScript(projectName: String) extends Command {
    override def execute(commandEnvironment: CommandEnvironment): Result = ???
  }

  // src/main/javadoc/overview.html
  case class CreateJavadocOverview(project: Specification.Project, moduleName: String) extends Command {
    override def execute(commandEnvironment: CommandEnvironment): Result = ???
  }

  // src/main/scala/.../javadoc/JavaDocStub.java
  case class CreateJavadocStub(project: Specification.Project, moduleName: String) extends Command {
    override def execute(commandEnvironment: CommandEnvironment): Result = ???
  }

}
