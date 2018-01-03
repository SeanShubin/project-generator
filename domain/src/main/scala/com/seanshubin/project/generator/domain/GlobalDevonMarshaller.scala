package com.seanshubin.project.generator.domain

import com.seanshubin.devon.domain.{DevonMarshaller, DevonMarshallerWiring}

object GlobalDevonMarshaller {
  val devonMarshaller: DevonMarshaller = DevonMarshallerWiring.Default
}
