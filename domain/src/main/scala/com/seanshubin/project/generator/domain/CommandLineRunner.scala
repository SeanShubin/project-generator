package com.seanshubin.project.generator.domain

import java.nio.file.{Path, Paths}

import com.seanshubin.project.generator.domain.Specification.Project

class CommandLineRunner(commandLineArguments: Array[String],
                        specificationLoader: SpecificationLoader,
                        createSpecificationRunner: (Project, Path) => Runnable) extends Runnable {
  override def run(): Unit = {
    val configurationFilePath = Paths.get(commandLineArguments(0))
    val destinationDirectory = getParent(configurationFilePath)
    val projectSpecification = specificationLoader.load(configurationFilePath)
    val runner = createSpecificationRunner(projectSpecification, destinationDirectory)
    runner.run()
  }

  private def getParent(path: Path): Path = {
    val parent = path.getParent
    if (parent == null) {
      val absoluteParent = path.toAbsolutePath.getParent
      absoluteParent
    } else {
      parent
    }
  }
}
