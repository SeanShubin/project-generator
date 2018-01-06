package com.seanshubin.project.generator.domain

import java.io.InputStream

trait HttpClient {
  def getInputStream(uri: String): InputStream
}
