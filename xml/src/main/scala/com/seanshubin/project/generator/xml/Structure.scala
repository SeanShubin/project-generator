package com.seanshubin.project.generator.xml

import java.io.InputStream
import javax.xml.parsers.{DocumentBuilder, DocumentBuilderFactory}

import org.w3c.dom.{Document, NodeList, Node => DomNode}

object Structure {

  class Node(domNode: DomNode) {
    def stringSeqAt(path: String*): Seq[String] = {
      if (path.isEmpty) Seq(text)
      else if (path.length == 1) childrenNamed(path(0)).flatMap(_.stringSeqAt())
      else childElement(path.head).stringSeqAt(path.tail: _*)
    }

    def childrenNamed(name: String): Seq[Node] = {
      children.filter(_.nodeType == NodeType.ElementNode).filter(_.name == name)
    }

    def name: String = domNode.getNodeName

    def nodeType: NodeType = NodeType.fromCode(domNode.getNodeType)

    //
    //  def value: String = domNode.getNodeValue
    //
    def text: String = domNode.getTextContent

    def children: Seq[Node] = new NodeIterator(domNode.getChildNodes).toSeq

    //
    //  def innerText: String = {
    //    val child = exactlyOne(children, "child of node")
    //    if (child.nodeType != NodeType.TextNode) {
    //      throw new RuntimeException(s"expected child node to be of type ${NodeType.TextNode.name}")
    //    }
    //    child.text
    //  }
    //
    def childElement(name: String): Node = {
      val matching = childrenNamed(name)
      exactlyOne(matching, s"name is '$name'")
    }

    def exactlyOne[T](seq: Seq[T], criteria: String): T = {
      if (seq.size == 1) {
        seq.head
      } else {
        throw new RuntimeException(s"expected exactly 1 element such that $criteria, got ${seq.size}")
      }
    }

    def isEmptyText: Boolean = nodeType == NodeType.TextNode && text.trim == ""
  }

  object Node {
    private val documentBuilderFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance
    private val documentBuilder: DocumentBuilder = documentBuilderFactory.newDocumentBuilder

    def fromInputStream(inputStream: InputStream): Node = {
      val document: Document = documentBuilder.parse(inputStream)
      val root: Node = new Node(document.getDocumentElement)
      root
    }
  }

  class NodeIterator(nodeList: NodeList) extends Iterator[Node] {
    private var index = 0

    override def hasNext: Boolean = index < nodeList.getLength

    override def next(): Node = {
      val value = new Node(nodeList.item(index))
      index += 1
      value
    }
  }

}
