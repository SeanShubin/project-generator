package com.seanshubin.project.generator.xml

interface XmlRenderer {
    fun toLines(node: XmlNode): List<String>
}