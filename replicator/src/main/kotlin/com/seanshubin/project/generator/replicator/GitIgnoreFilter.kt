package com.seanshubin.project.generator.replicator

import java.nio.file.FileSystems
import java.nio.file.PathMatcher
import java.nio.file.Paths

class GitIgnoreFilter(patterns: List<String>) {
    private val matchers: List<(String) -> Boolean> = patterns
        .map { it.trim() }
        .filter { it.isNotEmpty() && !it.startsWith("#") && !it.startsWith("!") }
        .mapNotNull { buildMatcher(it) }

    fun isIgnored(relativePath: String): Boolean {
        val normalized = relativePath.replace("\\", "/")
        return matchers.any { it(normalized) }
    }

    private fun buildMatcher(pattern: String): ((String) -> Boolean)? {
        val p = pattern.trimEnd('/')
        return when {
            p.startsWith("**/") -> {
                val tail = p.removePrefix("**/")
                if (tail.contains('*') || tail.contains('?')) {
                    val m = makeGlob("**/$tail") ?: return null
                    fun(path: String): Boolean = m.matches(Paths.get(path))
                } else {
                    fun(path: String): Boolean =
                        path == tail || path.startsWith("$tail/") ||
                        path.contains("/$tail/") || path.endsWith("/$tail")
                }
            }
            p.startsWith("/") -> {
                val anchored = p.removePrefix("/")
                if (anchored.contains('*') || anchored.contains('?')) {
                    val m = makeGlob(anchored) ?: return null
                    fun(path: String): Boolean = m.matches(Paths.get(path))
                } else {
                    fun(path: String): Boolean =
                        path == anchored || path.startsWith("$anchored/")
                }
            }
            p.contains('/') -> {
                val m = makeGlob(p) ?: return null
                fun(path: String): Boolean = m.matches(Paths.get(path))
            }
            p.contains('*') || p.contains('?') -> {
                val m = makeGlob(p) ?: return null
                fun(path: String): Boolean =
                    path.split("/").any { seg -> m.matches(Paths.get(seg)) }
            }
            else -> {
                fun(path: String): Boolean = path.split("/").any { seg -> seg == p }
            }
        }
    }

    private fun makeGlob(glob: String): PathMatcher? {
        return try {
            FileSystems.getDefault().getPathMatcher("glob:$glob")
        } catch (e: Exception) {
            null
        }
    }
}
