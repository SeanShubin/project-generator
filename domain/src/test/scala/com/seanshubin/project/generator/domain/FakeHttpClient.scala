package com.seanshubin.project.generator.domain

import java.io.InputStream

import com.seanshubin.project.generator.http.HttpClient

class FakeHttpClient(getRequestToResourceNameMap: Map[String, String]) extends HttpClient {
  def getInputStream(uri: String): InputStream = {
    val resourceName = getRequestToResourceNameMap(uri)
    val inputStream = ResourceUtil.getResourceAsStream(resourceName)
    inputStream
  }
}
