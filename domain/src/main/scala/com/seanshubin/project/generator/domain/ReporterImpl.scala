package com.seanshubin.project.generator.domain

class ReporterImpl(emitLine: String => Unit) extends Reporter {
  override def reportResult(result: Result): Unit = {
    result.toLines.foreach(emitLine)
  }
}
