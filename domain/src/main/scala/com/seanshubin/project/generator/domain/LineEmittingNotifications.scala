package com.seanshubin.project.generator.domain

import com.seanshubin.project.generator.domain.GlobalConstants.devonMarshaller

class LineEmittingNotifications(emit: String => Unit) extends Notifications {
  override def effectiveSpecification(specification: Specification.Project): Unit = {
    val lines = devonMarshaller.valueToPretty(specification)
    lines.foreach(emit)
  }
}
