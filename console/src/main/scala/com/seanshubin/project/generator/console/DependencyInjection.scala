package com.seanshubin.project.generator.console

import com.seanshubin.project.generator.domain.ApplicationRunner

trait DependencyInjection {
  val runner:Runnable = new ApplicationRunner()
}
