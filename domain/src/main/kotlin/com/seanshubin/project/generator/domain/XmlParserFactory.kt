package com.seanshubin.project.generator.domain

import javax.xml.parsers.SAXParser

interface XmlParserFactory {
    fun createParser(): SAXParser
}
