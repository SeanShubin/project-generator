package com.seanshubin.project.generator.domain

import org.scalatest.FunSuite

class RepositoryTest extends FunSuite {
  test("latest version") {
    // given
    val uri = "http://repo1.maven.org/maven2/org/scala-lang/scala-library/maven-metadata.xml"
    val httpClient = new FakeHttpClient(Map(uri -> "scala-maven-metadata.xml"))
    val repository = new MavenRepository(httpClient)
    val group = "org.scala-lang"
    val artifact = "scala-library"
    val expectedVersion = "2.12.4"

    // when
    val actualVersion = repository.latestVersion(group, artifact)

    // then
    assert(actualVersion === expectedVersion)
  }
}
