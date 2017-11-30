package com.seanshubin.project.generator.xml

object NodeFormatter {
  def format(node: Node): String = {
    nodeToMultipleLineString(node).mkString("\n")
  }

  def nodeToMultipleLineString(node: Node): Seq[String] = {
    if (node.nodeType == NodeType.ElementNode) {
      val lines = Seq(openXml(node.name)) ++
        node.children.filter(!_.isEmptyText).flatMap(nodeToMultipleLineString).map(indent) ++
        Seq(closeXml(node.name))
      lines
    } else if (node.nodeType == NodeType.TextNode) {
      Seq(node.text)
    } else {
      throw new RuntimeException(s"Unable to format type ${node.nodeType.name}")
    }
  }

  def openXml(name: String): String = s"<$name>"

  def closeXml(name: String): String = s"</$name>"

  def indent(text: String): String = s"  $text"
}
