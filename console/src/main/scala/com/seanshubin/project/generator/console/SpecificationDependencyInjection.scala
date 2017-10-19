package com.seanshubin.project.generator.console

import com.seanshubin.project.generator.domain._

trait SpecificationDependencyInjection {
  def projectSpecification: Specification.Project

  val commandGenerator: CommandGenerator = new CommandGeneratorImpl(projectSpecification)
  val commandExecutor: CommandExecutor = new CommandExecutorImpl()
  val reporter: Reporter = new ReporterImpl()
  val runner: Runnable = new SpecificationRunner(commandGenerator, commandExecutor, reporter)
}
