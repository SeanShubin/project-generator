package com.seanshubin.project.generator.domain

class SpecificationRunner(commandGenerator: CommandGenerator,
                          commandExecutor: CommandExecutor,
                          reporter: Reporter) extends Runnable {
  override def run(): Unit = {
    val commands = commandGenerator.generate()
    val results = commands.map(commandExecutor.execute)
    results.foreach(reporter.reportResult)
  }
}
