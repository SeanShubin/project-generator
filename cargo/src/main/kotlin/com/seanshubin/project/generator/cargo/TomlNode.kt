package com.seanshubin.project.generator.cargo

/**
 * A structural model of a TOML document, rendered by [TomlRenderer].
 *
 * Only the subset Cargo manifests need is modeled: tables, arrays of tables,
 * key/value pairs, comments and blank lines.  Nesting is expressed in dotted
 * table names rather than by nesting nodes, which is how TOML itself works.
 */
sealed interface TomlNode {
    /** The whole document: a sequence of nodes rendered in order. */
    data class Document(val children: List<TomlNode>) : TomlNode

    /** A `[name]` header followed by its entries. */
    data class Table(val name: String, val children: List<TomlNode>) : TomlNode

    /**
     * A `[[name]]` header followed by its entries, TOML's array-of-tables.
     * Cargo uses it for repeatable sections such as `[[bin]]`.
     */
    data class ArrayTable(val name: String, val children: List<TomlNode>) : TomlNode

    /** A `key = value` entry. */
    data class KeyValue(val key: String, val value: TomlValue) : TomlNode

    /** A `# text` line. */
    data class Comment(val text: String) : TomlNode

    /** A separator between tables. */
    data object BlankLine : TomlNode
}

/** The value half of a [TomlNode.KeyValue]. */
sealed interface TomlValue {
    /** A string, rendered with quotes and escaping. */
    data class Text(val value: String) : TomlValue

    /** Rendered verbatim, for numbers and booleans. */
    data class Literal(val value: String) : TomlValue

    /**
     * An array.  When [multiline] it renders one element per line with a
     * trailing comma, which is how workspace `members` lists are conventionally
     * written and keeps diffs to a single line when a member is added.
     */
    data class Array(val elements: List<TomlValue>, val multiline: Boolean = false) : TomlValue

    /** A `{ key = value, ... }` inline table, as used by dependency entries. */
    data class InlineTable(val entries: List<Pair<String, TomlValue>>) : TomlValue

    companion object {
        val TRUE: TomlValue = Literal("true")
        fun of(value: String): TomlValue = Text(value)
        fun of(value: Int): TomlValue = Literal(value.toString())
        fun of(value: Boolean): TomlValue = Literal(value.toString())
    }
}
