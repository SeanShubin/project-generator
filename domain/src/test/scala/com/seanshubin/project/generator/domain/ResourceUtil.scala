package com.seanshubin.project.generator.domain

import java.io.InputStream

object ResourceUtil {
  def getResourceAsStream(name: String): InputStream = {
    val inputStream = getClass.getClassLoader.getResourceAsStream(name)
    if (inputStream == null) {
      throw new RuntimeException(s"Unable to load resource named '$name'")
    }
    inputStream
  }
}
