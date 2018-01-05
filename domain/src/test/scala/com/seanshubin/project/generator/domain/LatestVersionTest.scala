package com.seanshubin.project.generator.domain

import org.scalatest.FunSuite

class LatestVersionTest extends FunSuite {
  test("typical example") {
    val http = new FakeHttp(Map(
      "http://repo1.maven.org/maven2/org/scala-lang/scala-library/maven-metadata.xml" -> "scala-maven-metadata.xml"))
  }
}
