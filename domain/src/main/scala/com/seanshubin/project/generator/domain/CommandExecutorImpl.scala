package com.seanshubin.project.generator.domain

class CommandExecutorImpl(commandEnvironment: CommandEnvironment) extends CommandExecutor {
  override def execute(command: Command): Result = {
    command.execute(commandEnvironment)
  }
}
