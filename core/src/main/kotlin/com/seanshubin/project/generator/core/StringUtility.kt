package com.seanshubin.project.generator.core

/**
 * Text formatting shared by every renderer.
 *
 * This lives here rather than beside one renderer because indentation is not
 * specific to a markup language: the XML, Gradle and TOML renderers all take
 * the same function.
 */
object StringUtility {
    val indent: (String) -> String = { "    $it" }
}
