package com.seanshubin.project.generator.domain

object VersionUtil {
  private val digits = """\d+"""
  private val dot = """\."""
  private val majorMinorPatch = capture(digits) + dot + capture(digits) + dot + capture(digits)
  private val MajorMinorPatch = majorMinorPatch.r

  private def capture(x: String): String = {
    s"($x)"
  }

  def isProductionVersion(version: String): Boolean = {
    version match {
      case MajorMinorPatch(_, _, _) => true
      case _ => false
    }
  }

  val lessThan: (String, String) => Boolean = (left, right) => {
    ???
  }
}
