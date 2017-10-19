package com.seanshubin.project.generator.domain

import com.seanshubin.devon.domain.DevonMarshallerWiring

class LineEmittingNotifications(emit: String => Unit) extends Notifications {
  override def effectiveSpecification(specification: Specification.Project): Unit = {
    val lines = DevonMarshallerWiring.Default.valueToPretty(specification)
    lines.foreach(emit)
  }

}
