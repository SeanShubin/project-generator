package com.seanshubin.project.generator.domain

trait Repository {
  def latestVersionFor(group: String, artifact: String): String
}
