package com.seanshubin.project.generator.domain

import com.seanshubin.project.generator.http.HttpClient
import com.seanshubin.project.generator.xml.Structure.Node

class MavenRepository(httpClient: HttpClient) extends Repository {
  private val latestVersionOrdering = Ordering.fromLessThan(LexicographicalCompare.lessThan).reverse

  override def latestVersion(group: String, artifact: String): String = {
    if(group == null || artifact == null){
      throw new RuntimeException(s"invalid group $group and artifact $artifact")
    }
    val groupPath = group.replace(".", "/")
    val uri = s"https://repo1.maven.org/maven2/$groupPath/$artifact/maven-metadata.xml"
    val xmlInputStream = httpClient.getInputStream(uri)
    val node = Node.fromInputStream(xmlInputStream)
    val versions = node.stringSeqAt("versioning", "versions", "version")
    val productionVersions = versions.filter(VersionUtil.isProductionVersion)
    val sortedVersions = productionVersions.sorted(latestVersionOrdering)
    sortedVersions.head
  }
}
