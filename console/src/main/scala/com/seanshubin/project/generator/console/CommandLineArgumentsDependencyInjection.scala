package com.seanshubin.project.generator.console

import java.nio.file.Path

import com.seanshubin.project.generator.domain.Specification.Project
import com.seanshubin.project.generator.domain._

trait CommandLineArgumentsDependencyInjection {
  def commandLineArguments: Array[String]

  val files: FilesContract = FilesDelegate
  val emit: String => Unit = println
  val notifications: Notifications = new LineEmittingNotifications(emit)
  val specificationLoader: SpecificationLoader = new SpecificationLoaderImpl(files, notifications.effectiveSpecification _)
  val createSpecificationRunner: (Project, Path) => Runnable = (theProjectSpecification, theDestinationDirectory) => new SpecificationDependencyInjection {
    override def specification: Project = theProjectSpecification

    override def destinationDirectory: Path = theDestinationDirectory
  }.runner
  val runner: Runnable = new CommandLineRunner(commandLineArguments, specificationLoader, createSpecificationRunner)
}

