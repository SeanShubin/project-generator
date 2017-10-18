package com.seanshubin.project.generator.console

object EntryPoint extends App {
  new DependencyInjection{}.runner.run()
}
