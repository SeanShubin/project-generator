package com.seanshubin.project.generator.domain

trait CommandExecutor {
  def execute(command: Command): Result
}
