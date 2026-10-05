package com.seanshubin.project.generator.cargo

import com.seanshubin.project.generator.core.StringUtility
import kotlin.test.Test
import kotlin.test.assertEquals

class TomlRendererImplTest {
    private val renderer = TomlRendererImpl(StringUtility.indent)

    @Test
    fun tableEntriesAreNotIndented() {
        val node = TomlNode.Table(
            "package",
            listOf(
                TomlNode.KeyValue("name", TomlValue.of("engine")),
                TomlNode.KeyValue("version.workspace", TomlValue.TRUE)
            )
        )
        assertEquals(
            listOf(
                "[package]",
                "name = \"engine\"",
                "version.workspace = true"
            ),
            renderer.toLines(node)
        )
    }

    @Test
    fun arrayOfTablesUsesDoubleBrackets() {
        val node = TomlNode.ArrayTable("bin", listOf(TomlNode.KeyValue("name", TomlValue.of("game"))))
        assertEquals(listOf("[[bin]]", "name = \"game\""), renderer.toLines(node))
    }

    @Test
    fun multilineArrayPutsOneElementPerLineWithTrailingComma() {
        val node = TomlNode.KeyValue(
            "members",
            TomlValue.Array(listOf(TomlValue.of("crates/a"), TomlValue.of("crates/b")), multiline = true)
        )
        assertEquals(
            listOf(
                "members = [",
                "    \"crates/a\",",
                "    \"crates/b\",",
                "]"
            ),
            renderer.toLines(node)
        )
    }

    @Test
    fun emptyMultilineArrayStaysOnOneLine() {
        val node = TomlNode.KeyValue("members", TomlValue.Array(emptyList(), multiline = true))
        assertEquals(listOf("members = []"), renderer.toLines(node))
    }

    @Test
    fun inlineArrayStaysOnOneLine() {
        val node = TomlNode.KeyValue(
            "features",
            TomlValue.Array(listOf(TomlValue.of("derive"), TomlValue.of("rc")))
        )
        assertEquals(listOf("features = [\"derive\", \"rc\"]"), renderer.toLines(node))
    }

    @Test
    fun inlineTableIsSpacedTheWayCargoWritesIt() {
        val node = TomlNode.KeyValue(
            "serde",
            TomlValue.InlineTable(
                listOf(
                    "version" to TomlValue.of("1.0.0"),
                    "default-features" to TomlValue.of(false)
                )
            )
        )
        assertEquals(listOf("serde = { version = \"1.0.0\", default-features = false }"), renderer.toLines(node))
    }

    @Test
    fun emptyInlineTableRendersAsBraces() {
        val node = TomlNode.KeyValue("empty", TomlValue.InlineTable(emptyList()))
        assertEquals(listOf("empty = {}"), renderer.toLines(node))
    }

    @Test
    fun stringsAreEscaped() {
        val node = TomlNode.KeyValue("path", TomlValue.of("c:\\a\"b\tc"))
        assertEquals(listOf("path = \"c:\\\\a\\\"b\\tc\""), renderer.toLines(node))
    }

    @Test
    fun commentsRenderOneLinePerLineOfText() {
        val node = TomlNode.Comment("first\nsecond")
        assertEquals(listOf("# first", "# second"), renderer.toLines(node))
    }

    @Test
    fun documentRendersChildrenInOrderWithBlankLines() {
        val node = TomlNode.Document(
            listOf(
                TomlNode.Table("a", listOf(TomlNode.KeyValue("x", TomlValue.of(1)))),
                TomlNode.BlankLine,
                TomlNode.Table("b", listOf(TomlNode.KeyValue("y", TomlValue.of(2))))
            )
        )
        assertEquals(listOf("[a]", "x = 1", "", "[b]", "y = 2"), renderer.toLines(node))
    }
}
