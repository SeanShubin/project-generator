package com.seanshubin.project.generator.domain

sealed trait Result {
  def toLines: Seq[String]
}

object Result {

  case class Success(content: String) extends Result {
    override def toLines: Seq[String] = Seq(content)
  }

}