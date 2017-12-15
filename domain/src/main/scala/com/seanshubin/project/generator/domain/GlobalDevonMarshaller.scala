package com.seanshubin.project.generator.domain

import com.seanshubin.devon.domain.{DevonMarshaller, DevonMarshallerWiring, NoOperationStringProcessor}
import com.seanshubin.devon.parser.StringProcessor
import com.seanshubin.devon.reflection.SimpleTypeConversion

object GlobalDevonMarshaller {
  val devonMarshaller: DevonMarshaller = new DevonMarshallerWiring {
    override lazy val stringProcessor: StringProcessor = NoOperationStringProcessor
    override lazy val typeConversions: Seq[SimpleTypeConversion] =
      ProjectTypeDevonConversion :: SimpleTypeConversion.defaultConversions
  }.devonMarshaller
}
