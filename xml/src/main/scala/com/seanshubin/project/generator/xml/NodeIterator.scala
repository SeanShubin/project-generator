package com.seanshubin.project.generator.xml

import org.w3c.dom.NodeList

class NodeIterator(nodeList: NodeList) extends Iterator[Node] {
  private var index = 0

  override def hasNext: Boolean = index < nodeList.getLength

  override def next(): Node = {
    val value = new Node(nodeList.item(index))
    index += 1
    value
  }
}
