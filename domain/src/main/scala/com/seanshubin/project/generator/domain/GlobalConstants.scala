package com.seanshubin.project.generator.domain

import java.nio.charset.{Charset, StandardCharsets}

import com.seanshubin.devon.domain.{DevonMarshaller, DevonMarshallerWiring}

object GlobalConstants {
  val devonMarshaller: DevonMarshaller = DevonMarshallerWiring.Default
  val charset: Charset = StandardCharsets.UTF_8
}
