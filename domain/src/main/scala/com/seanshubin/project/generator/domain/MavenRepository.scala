package com.seanshubin.project.generator.domain

import com.seanshubin.project.generator.xml.Node

class MavenRepository(httpClient: HttpClient) extends Repository {
  private val latestVersionOrdering = Ordering.fromLessThan(LexicographicalCompare.lessThan).reverse
  override def latestVersionFor(group: String, artifact: String): String = {
    val groupPath = group.replace(".", "/")
    val artifactPath = artifact.replace(".", "/")
    val uri = s"http://repo1.maven.org/maven2/$groupPath/$artifactPath/maven-metadata.xml"
    val xmlInputStream = httpClient.getInputStream(uri)
    val node = Node.fromInputStream(xmlInputStream)
    val versions = node.stringSeqAt("versioning", "versions", "version")
    val productionVersions = versions.filter(VersionUtil.isProductionVersion)
    val sortedVersions = productionVersions.sorted(latestVersionOrdering)
    sortedVersions.head
  }
}
