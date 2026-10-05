package com.seanshubin.project.generator.core

/**
 * An optional layer of extra generation on top of a build system.
 *
 * A preset is not a language and not a build system; it is a bundle of
 * dependencies, build profiles and directories that a particular kind of
 * project always wants.  Bevy is the motivating case: it needs its own
 * dependency, an optimized dev profile for its transitive crates, a faster
 * linker and an `assets` directory, none of which is a new language.
 */
enum class Preset {
    NONE,
    BEVY;

    companion object {
        fun fromString(value: String): Preset =
            entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException(
                    "Unsupported preset '$value', expected one of: " +
                            entries.joinToString(", ") { it.name.lowercase() }
                )
    }
}
