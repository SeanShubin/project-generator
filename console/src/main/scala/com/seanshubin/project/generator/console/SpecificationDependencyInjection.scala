package com.seanshubin.project.generator.console

import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.Path

import com.seanshubin.project.generator.domain._

trait SpecificationDependencyInjection {
  def specification: Specification.Project

  def destinationDirectory: Path

  val commandGenerator: CommandGenerator = new CommandGeneratorImpl(specification, destinationDirectory)
  val files: FilesContract = FilesDelegate
  val classLoader: ClassLoaderContract = new ClassLoaderDelegate(this.getClass.getClassLoader)
  val charset: Charset = StandardCharsets.UTF_8
  val commandEnvironment: CommandEnvironment = CommandEnvironment(files, classLoader, charset)
  val commandExecutor: CommandExecutor = new CommandExecutorImpl(commandEnvironment)
  val emitLine: String => Unit = println
  val reporter: Reporter = new ReporterImpl(emitLine)
  val runner: Runnable = new SpecificationRunner(commandGenerator, commandExecutor, reporter)
}
