package com.seanshubin.project.generator.cargo

interface TomlRenderer {
    fun toLines(node: TomlNode): List<String>
}
