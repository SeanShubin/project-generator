package com.seanshubin.project.generator.domain

trait CommandGenerator {
  def generate(): Iterable[Command]
}
