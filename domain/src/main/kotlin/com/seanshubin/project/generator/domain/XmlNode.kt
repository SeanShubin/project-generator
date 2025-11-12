package com.seanshubin.project.generator.domain

sealed interface XmlNode {
    data class Element(
        val name: String,
        val attributes: List<Pair<String, String>>,
        val children: List<XmlNode>
    ) : XmlNode

    data class Text(val text: String) : XmlNode
}
