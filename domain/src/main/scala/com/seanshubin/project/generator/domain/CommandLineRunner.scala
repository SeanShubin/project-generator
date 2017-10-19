package com.seanshubin.project.generator.domain

import java.nio.file.{Path, Paths}

import com.seanshubin.project.generator.domain.Specification.Project

class CommandLineRunner(commandLineArguments: Array[String],
                        specificationLoader: SpecificationLoader,
                        createSpecificationRunner: (Project, Path) => Runnable) extends Runnable {
  override def run(): Unit = {
    val configurationFileName = commandLineArguments(0)
    val destinationDirectory = Paths.get(commandLineArguments(1))
    val projectSpecification = specificationLoader.load(configurationFileName)
    val runner = createSpecificationRunner(projectSpecification, destinationDirectory)
    runner.run()
  }
}
