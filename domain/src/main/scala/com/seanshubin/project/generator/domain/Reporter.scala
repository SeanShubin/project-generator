package com.seanshubin.project.generator.domain

trait Reporter {
  def reportResult(result: Result): Unit
}
