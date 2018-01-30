package com.seanshubin.project.generator.http

import java.io.InputStream

trait HttpClient {
  def getInputStream(uri: String): InputStream
}
