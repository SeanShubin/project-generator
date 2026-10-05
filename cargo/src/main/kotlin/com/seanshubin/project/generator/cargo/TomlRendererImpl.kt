package com.seanshubin.project.generator.cargo

/**
 * Renders a [TomlNode] tree as TOML text.
 *
 * Unlike XML, TOML table headers are flat rather than nested: a table's entries
 * sit at the left margin under their `[header]`, and nesting is expressed in the
 * dotted header name.  So, unlike [com.seanshubin.project.generator.xml.XmlRendererImpl],
 * this renderer takes no indent function; the only indentation it emits is
 * inside a multiline array.
 */
class TomlRendererImpl(private val indent: (String) -> String) : TomlRenderer {
    override fun toLines(node: TomlNode): List<String> = when (node) {
        is TomlNode.Document -> node.children.flatMap(::toLines)
        is TomlNode.Table -> listOf("[${node.name}]") + node.children.flatMap(::toLines)
        is TomlNode.ArrayTable -> listOf("[[${node.name}]]") + node.children.flatMap(::toLines)
        is TomlNode.KeyValue -> keyValueToLines(node)
        is TomlNode.Comment -> node.text.lines().map { "# $it".trimEnd() }
        TomlNode.BlankLine -> listOf("")
    }

    private fun keyValueToLines(keyValue: TomlNode.KeyValue): List<String> {
        val value = keyValue.value
        return if (value is TomlValue.Array && value.multiline && value.elements.isNotEmpty()) {
            listOf("${keyValue.key} = [") +
                    value.elements.map { indent("${valueToString(it)},") } +
                    listOf("]")
        } else {
            listOf("${keyValue.key} = ${valueToString(value)}")
        }
    }

    private fun valueToString(value: TomlValue): String = when (value) {
        is TomlValue.Text -> quote(value.value)
        is TomlValue.Literal -> value.value
        is TomlValue.Array -> value.elements.joinToString(", ", "[", "]", transform = ::valueToString)
        is TomlValue.InlineTable ->
            if (value.entries.isEmpty()) "{}"
            else value.entries.joinToString(", ", "{ ", " }") { (key, entryValue) ->
                "$key = ${valueToString(entryValue)}"
            }
    }

    private fun quote(text: String): String {
        val escaped = text.map(::escapeCharacter).joinToString("")
        return "\"$escaped\""
    }

    private fun escapeCharacter(character: Char): String = when (character) {
        '\\' -> "\\\\"
        '"' -> "\\\""
        '\n' -> "\\n"
        '\r' -> "\\r"
        '\t' -> "\\t"
        else -> character.toString()
    }
}
