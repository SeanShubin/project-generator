package com.seanshubin.project.generator.console

object EntryPoint extends App {
  new CommandLineArgumentsDependencyInjection {
    override def commandLineArguments: Array[String] = args
  }.runner.run()
}
