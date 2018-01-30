package com.seanshubin.project.generator.console

import java.nio.file.Path

import com.seanshubin.project.generator.domain._
import com.seanshubin.project.generator.http.{GoogleHttpClient, HttpClient}

trait SpecificationDependencyInjection {
  def specification: Specification.Project

  def destinationDirectory: Path

  val commandGenerator: CommandGenerator = new CommandGeneratorImpl(specification, destinationDirectory)
  val files: FilesContract = FilesDelegate
  val classLoader: ClassLoaderContract = new ClassLoaderDelegate(this.getClass.getClassLoader)
  val newline: String = "\n"
  val httpClient: HttpClient = new GoogleHttpClient()
  val repository: Repository = new MavenRepository(httpClient)
  val pomGenerator: PomGenerator = new PomGeneratorImpl(newline, repository)
  val commandEnvironment: CommandEnvironment = new CommandEnvironment(destinationDirectory, pomGenerator, files, classLoader)
  val commandExecutor: CommandExecutor = new CommandExecutorImpl(commandEnvironment)
  val emitLine: String => Unit = println
  val reporter: Reporter = new ReporterImpl(emitLine)
  val runner: Runnable = new SpecificationRunner(commandGenerator, commandExecutor, reporter)
}
