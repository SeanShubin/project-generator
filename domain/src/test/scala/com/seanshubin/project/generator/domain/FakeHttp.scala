package com.seanshubin.project.generator.domain

class FakeHttp(getRequestToResourceNameMap: Map[String, String]) {
  def get(uri: String): String = getRequestToResourceNameMap(uri)
}
