package com.seanshubin.project.generator.domain

case class Dependency(group: String, artifact: String, version: String, scope: Option[String] = None) {

}
