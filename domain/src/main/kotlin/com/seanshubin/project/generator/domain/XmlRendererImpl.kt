package com.seanshubin.project.generator.domain

class XmlRendererImpl(private val indent:(String)->String):XmlRenderer {
    override fun toLines(node: XmlNode): List<String> {
        return listOf(xmlHeader) + genericToLines(node)
    }

    private fun genericToLines(node:XmlNode):List<String>{
        return when(node){
            is XmlNode.Element -> elementToLines(node)
            is XmlNode.Text -> listOf(node.text)
        }
    }

    private fun elementToLines(element:XmlNode.Element):List<String>{
        return if(element.children.isEmpty()){
            listOf(singleTag(element))
        } else if(element.children.size == 1){
            val child = element.children[0]
            if(child is XmlNode.Text) {
                listOf("${openTag(element)}${child.text}${closeTag(element)}")
            } else {
                genericElementToLines(element)
            }
        } else {
            genericElementToLines(element)
        }
    }

    private fun genericElementToLines(element:XmlNode.Element):List<String>{
        val childLines = element.children.flatMap(::genericToLines).map(indent)
        val lines = listOf(openTag(element)) + childLines + listOf(closeTag(element))
        return lines
    }

    private fun openTag(element:XmlNode.Element):String {
        return "<${element.name}${attributesString(element)}>"
    }
    private fun closeTag(element:XmlNode.Element):String = "</${element.name}>"
    private fun singleTag(element:XmlNode.Element):String {
        return "<${element.name}${attributesString(element)}/>"
    }
    private fun attributesString(element:XmlNode.Element):String = if(element.attributes.isEmpty()) "" else element.attributes.joinToString(" ", " ") { (name, value) ->
        "$name=\"$value\""
    }
    private val xmlHeader = """<?xml version="1.0" encoding="UTF-8"?>"""
}
