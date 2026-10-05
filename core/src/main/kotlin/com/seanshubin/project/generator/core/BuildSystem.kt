package com.seanshubin.project.generator.core

/**
 * Which build system to emit for.
 *
 * This is the axis that genuinely forks generation: a Maven project gets a
 * `pom.xml` per module with group/artifact coordinates resolved against Maven
 * Central, a Cargo project gets a workspace `Cargo.toml` with flat crate names
 * resolved against crates.io.  [Project.language] is a narrower thing -- which
 * compiler plugin goes in the pom -- and is not a substitute for this.
 */
enum class BuildSystem {
    MAVEN,
    CARGO;

    companion object {
        fun fromString(value: String): BuildSystem =
            entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException(
                    "Unsupported buildSystem '$value', expected one of: " +
                            entries.joinToString(", ") { it.name.lowercase() }
                )
    }
}
