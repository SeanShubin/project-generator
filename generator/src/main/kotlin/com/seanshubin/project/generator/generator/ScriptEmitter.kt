package com.seanshubin.project.generator.generator

import com.seanshubin.project.generator.commands.Command
import com.seanshubin.project.generator.commands.WriteTextFile
import java.nio.file.Path

/**
 * Copies fixed files out of this jar's resources into a generated project.
 *
 * Both build systems ship a `scripts` directory and a `.gitignore`; only the
 * contents differ, so the mechanism is shared and the resource directory is a
 * parameter.  Placeholder substitution is supported for the few scripts that
 * need to know something about the project.
 */
class ScriptEmitter(
    private val baseDirectory: Path,
    private val resourceDirectory: String
) {
    fun scriptsDirectory(): Command = com.seanshubin.project.generator.commands.CreateDirectory(scriptsPath())

    fun script(scriptName: String, replacements: Map<String, String> = emptyMap()): Command {
        val content = substitute(loadResource(scriptName), replacements)
        return WriteTextFile(scriptsPath().resolve(scriptName), content, executable = true)
    }

    fun fileAtRoot(resourceName: String, targetName: String): Command {
        val content = loadResource(resourceName)
        return WriteTextFile(baseDirectory.resolve(targetName), content)
    }

    private fun scriptsPath(): Path = baseDirectory.resolve("scripts")

    private fun substitute(content: String, replacements: Map<String, String>): String =
        replacements.entries.fold(content) { text, (placeholder, value) ->
            text.replace(placeholder, value)
        }

    private fun loadResource(resourceName: String): String {
        val resourcePath = "$resourceDirectory/$resourceName"
        val classLoader = this.javaClass.classLoader
        val inputStream = classLoader.getResourceAsStream(resourcePath)
            ?: throw RuntimeException("Resource not found: $resourcePath")
        return inputStream.bufferedReader().use { it.readText() }
    }
}
