package com.seanshubin.project.generator.domain

case class PomBuilder(groupId: String, artifactId: String, name: String, description: String) {
  def applyTemplate(template: String): String = {
    template.
      replace("---template-group-id---", groupId).
      replace("---template-artifact-id---", groupId).
      replace("---template-description---", groupId).
      replace("---template-name---", groupId)
  }
}
