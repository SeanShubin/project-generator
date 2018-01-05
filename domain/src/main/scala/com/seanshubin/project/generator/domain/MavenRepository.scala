package com.seanshubin.project.generator.domain

class MavenRepository(httpClient: HttpClient) extends Repository {
  override def latestVersionFor(group: String, artifact: String): String = {
    //val uri = s"http://repo1.maven.org/maven2/$groupPath/$artifactPath/maven-metadata.xml"
    //val body = httpClient.get(uri)
    ???
  }
}
