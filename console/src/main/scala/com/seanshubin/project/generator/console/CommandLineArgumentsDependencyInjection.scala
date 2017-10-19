package com.seanshubin.project.generator.console

import java.nio.charset.{Charset, StandardCharsets}

import com.seanshubin.project.generator.domain.Specification.Project
import com.seanshubin.project.generator.domain._

trait CommandLineArgumentsDependencyInjection {
  def commandLineArguments: Array[String]

  val files: FilesContract = FilesDelegate
  val charset: Charset = StandardCharsets.UTF_8
  val emit: String => Unit = println
  val notifications: Notifications = new LineEmittingNotifications(emit)
  val specificationLoader: SpecificationLoader = new SpecificationLoaderImpl(files, charset, notifications.effectiveSpecification _)
  val createSpecificationRunner: Project => Runnable = theProjectSpecification => new SpecificationDependencyInjection {
    override def projectSpecification: Project = theProjectSpecification
  }.runner
  val runner: Runnable = new CommandLineRunner(commandLineArguments, specificationLoader, createSpecificationRunner)
}

