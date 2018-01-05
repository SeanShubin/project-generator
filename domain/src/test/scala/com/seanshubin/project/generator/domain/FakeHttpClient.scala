package com.seanshubin.project.generator.domain

class FakeHttpClient(getRequestToResourceNameMap: Map[String, String]) extends HttpClient {
  def get(uri: String): String = getRequestToResourceNameMap(uri)
}
