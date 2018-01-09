package com.seanshubin.project.generator.domain

trait Repository {
  def latestVersion(group: String, artifact: String): String
}
