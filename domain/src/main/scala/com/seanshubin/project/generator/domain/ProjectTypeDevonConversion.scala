package com.seanshubin.project.generator.domain

import com.seanshubin.devon.reflection.SimpleTypeConversion

object ProjectTypeDevonConversion extends SimpleTypeConversion {
  override def className: String = classOf[ProjectType].getName

  override def toDynamic(x: Any): String = x.asInstanceOf[ProjectType].name

  override def toStatic(x: String): Any = ProjectType.fromName(x)
}
