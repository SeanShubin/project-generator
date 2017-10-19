package com.seanshubin.project.generator.domain

import com.seanshubin.project.generator.domain.Specification.Project

class CommandLineRunner(commandLineArguments: Array[String],
                        specificationLoader: SpecificationLoader,
                        createSpecificationRunner: Project => Runnable) extends Runnable {
  override def run(): Unit = {
    val configurationFileName = commandLineArguments(0)
    val projectSpecification = specificationLoader.load(configurationFileName)
    val runner = createSpecificationRunner(projectSpecification).run()
  }
}
