package com.seanshubin.project.generator.xml

import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory

class SaxParserFactoryImpl : XmlParserFactory {
    override fun createParser(): SAXParser {
        val saxParserFactory = SAXParserFactory.newInstance()
        return saxParserFactory.newSAXParser()
    }
}
