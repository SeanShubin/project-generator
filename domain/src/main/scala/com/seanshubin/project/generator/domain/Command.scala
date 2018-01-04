package com.seanshubin.project.generator.domain

import java.nio.file.{Path, Paths}

import com.seanshubin.project.generator.domain.DetanglerConfig.StartsWithConfiguration
import com.seanshubin.project.generator.domain.GlobalConstants.{charset, devonMarshaller}
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
      val text =
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
      val path = commandEnvironment.baseDirectory.resolve("UNLICENSE.txt")
      writeText(commandEnvironment, path, text)
      Success(s"generated license file at $path")
    }
  }

  // deploy-to-maven-central-settings.xml
  case class CreateSettings(mavenUserName: String) extends Command {
    override def execute(commandEnvironment: CommandEnvironment): Result = {
      val text =
        s"""<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
           |          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           |          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
           |                              http://maven.apache.org/xsd/settings-1.0.0.xsd">
           |    <servers>
           |        <server>
           |            <id>maven-staging</id>
           |            <username>$mavenUserName</username>
           |            <password>$${env.MAVEN_STAGING_PASSWORD}</password>
           |        </server>
           |    </servers>
           |    <mirrors>
           |        <mirror>
           |            <id>maven-staging</id>
           |            <url>https://oss.sonatype.org/service/local/staging/deploy/maven2</url>
           |            <mirrorOf>maven-staging</mirrorOf>
           |        </mirror>
           |    </mirrors>
           |</settings>
           |""".stripMargin
      val path = commandEnvironment.baseDirectory.resolve("deploy-to-maven-central-settings.xml")
      writeText(commandEnvironment, path, text)
      Success(s"generated maven settings file at $path, for deployment to maven central")
    }
  }

  case class CreateDetanglerConfig(project: Specification.Project) extends Command {
    override def execute(commandEnvironment: CommandEnvironment): Result = {
      def createSearchPath(moduleName: String): Path = {
        val artifactNameParts =
          project.name ++
            Seq(moduleName) ++
            Seq(project.version + ".jar")
        val artifactName = artifactNameParts.mkString("-")
        Paths.get(".", moduleName, "target", artifactName)
      }

      val reportDir = Paths.get("target", "detangled")
      val searchPaths = project.detangler.map(createSearchPath)
      val level = Some(2)
      val include = Seq(project.prefix ++ project.name)
      val exclude = Seq()
      val drop = include
      val startsWith = StartsWithConfiguration(include, exclude, drop)
      val ignoreFiles = Seq()
      val canFailBuild = Some(true)
      val allowedInCycle = Seq()
      val detanglerConfig = DetanglerConfig.Configuration(reportDir, searchPaths, level, startsWith, ignoreFiles, canFailBuild, allowedInCycle)
      val lines = devonMarshaller.valueToPretty(detanglerConfig)
      val path = commandEnvironment.baseDirectory.resolve("detangler.txt")
      writeLines(commandEnvironment, path, lines)
      Success(s"generated detangler configuration file at $path")
    }
  }

  // stage.sh
  case class CreateStageScript(project: Specification.Project) extends Command {
    override def execute(commandEnvironment: CommandEnvironment): Result = {
      val localRepoRelativePath = (project.prefix ++ project.name).map(_ + "/").mkString
      val text =
        s"""#!/usr/bin/env bash
           |
          |# halt the script if we encounter any errors
           |set -e
           |
          |# make sure we don't inherit any state from our local repository
           |rm -rf ~/.m2/repository/$localRepoRelativePath
           |
          |# make sure we don't inherit any state from previous runs
           |mvn clean
           |
          |# deploy with the staging profile, depends on environment variables GPG_KEY_NAME, GPG_KEY_PASSWORD, and MAVEN_STAGING_PASSWORD
           |mvn deploy -P stage --settings=deploy-to-maven-central-settings.xml -Dgpg.keyname=$${GPG_KEY_NAME} -Dgpg.passphrase=$${GPG_KEY_PASSWORD}
           |
          |# all done, emit a link describing where to check the results
           |echo artifacts staged, see https://oss.sonatype.org/#stagingRepositories
           |""".stripMargin
      val path = commandEnvironment.baseDirectory.resolve("stage.sh")
      writeText(commandEnvironment, path, text)
      Success(s"created stage script at $path")
    }
  }

  // src/main/javadoc/overview.html
  case class CreateJavadocOverview(project: Specification.Project, moduleName: String) extends Command {
    override def execute(commandEnvironment: CommandEnvironment): Result = ???
  }

  // src/main/scala/.../javadoc/JavaDocStub.java
  case class CreateJavadocStub(project: Specification.Project, moduleName: String) extends Command {
    override def execute(commandEnvironment: CommandEnvironment): Result = ???
  }

  def writeText(commandEnvironment: CommandEnvironment, path: Path, text: String): Unit = {
    val files = commandEnvironment.files
    val bytes = text.getBytes(GlobalConstants.charset)
    files.write(path, bytes)
  }

  def writeLines(commandEnvironment: CommandEnvironment, path: Path, lines: Seq[String]): Unit = {
    val files = commandEnvironment.files
    files.write(path, lines.asJava, charset)
  }
}
