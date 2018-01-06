package com.seanshubin.project.generator.xml

import scala.collection.mutable.ArrayBuffer

sealed abstract case class NodeType(name: String, code: Int) {
  NodeType.valuesBuffer += this

  override def toString: String = name
}

object NodeType {
  private val valuesBuffer = new ArrayBuffer[NodeType]
  lazy val values: Seq[NodeType] = valuesBuffer
  val ElementNode: NodeType = new NodeType("ElementNode", 1) {}
  val AttributeNode: NodeType = new NodeType("AttributeNode", 2) {}
  val TextNode: NodeType = new NodeType("TextNode", 3) {}
  val CdataSectionNode: NodeType = new NodeType("CdataSectionNode", 4) {}
  val EntityReferenceNode: NodeType = new NodeType("EntityReferenceNode", 5) {}
  val EntityNode: NodeType = new NodeType("EntityNode", 6) {}
  val ProcessingInstructionNode: NodeType = new NodeType("ProcessingInstructionNode", 7) {}
  val CommentNode: NodeType = new NodeType("CommentNode", 8) {}
  val DocumentNode: NodeType = new NodeType("DocumentNode", 9) {}
  val DocumentTypeNode: NodeType = new NodeType("DocumentTypeNode", 10) {}
  val DocumentFragmentNode: NodeType = new NodeType("DocumentFragmentNode", 11) {}
  val NotationNode: NodeType = new NodeType("NotationNode", 12) {}

  def fromCode(code: Int): NodeType = {
    values.find(_.code == code).get
  }
}
