package com.seanshubin.project.generator.domain

import java.io.InputStream

object ClassLoaderUtil {
  def getResourceAsStream(classLoader: ClassLoaderContract, resourceName: String): InputStream = {
    val resourceStream = classLoader.getResourceAsStream(resourceName)
    if (resourceStream == null) {
      throw new RuntimeException(s"Unable to load resource named '$resourceName'")
    }
    resourceStream
  }
}
