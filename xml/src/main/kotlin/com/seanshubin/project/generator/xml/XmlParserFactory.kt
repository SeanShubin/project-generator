package com.seanshubin.project.generator.xml

import javax.xml.parsers.SAXParser

interface XmlParserFactory {
    fun createParser(): SAXParser
}
