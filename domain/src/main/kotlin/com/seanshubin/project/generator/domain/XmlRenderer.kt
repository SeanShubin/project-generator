package com.seanshubin.project.generator.domain

interface XmlRenderer {
    fun toLines(node: XmlNode):List<String>
}