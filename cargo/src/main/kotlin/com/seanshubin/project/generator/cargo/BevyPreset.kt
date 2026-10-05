package com.seanshubin.project.generator.cargo

import com.seanshubin.project.generator.core.DependencySpec

/**
 * What the Bevy preset contributes to a Cargo workspace manifest.
 *
 * Bevy is not a language or a build system; it is a bundle of things a Bevy
 * project always wants. Two of them belong in the manifest:
 *
 * - the `bevy` dependency itself, supplied only when the project has not
 *   declared its own, so that a spec wanting particular features stays in
 *   control;
 * - a dev profile that optimizes dependencies, without which a debug build of
 *   a Bevy game is too slow to play.
 *
 * The profile has to be here rather than in a separate file because Cargo only
 * honors `[profile]` in the workspace root manifest.
 */
object BevyPreset {
    const val CRATE_KEY = "bevy"

    /**
     * Dependencies the preset supplies. These are merged *under* the project's
     * own dependencies, so a project declaring `bevy` with its own feature set
     * overrides this entry rather than conflicting with it.
     */
    fun dependencies(): Map<String, DependencySpec> = mapOf(
        CRATE_KEY to DependencySpec.Crate(
            crate = CRATE_KEY,
            version = null,
            features = emptyList(),
            defaultFeatures = null,
            scope = null
        )
    )

    /**
     * Build profiles, as recommended by Bevy for development builds: the
     * project's own crates stay cheap to recompile, while its dependencies --
     * which change rarely and dominate the frame time -- are fully optimized.
     */
    fun profileTables(): List<TomlNode> = listOf(
        TomlNode.BlankLine,
        TomlNode.Comment(
            "Bevy's recommended development profile. Our own crates stay at a low\n" +
                    "optimization level so they are quick to rebuild, while dependencies are\n" +
                    "fully optimized so a debug build runs at a playable frame rate.\n" +
                    "Turn this off when profiling or benchmarking."
        ),
        TomlNode.Table(
            "profile.dev",
            listOf(TomlNode.KeyValue("opt-level", TomlValue.of(1)))
        ),
        TomlNode.BlankLine,
        TomlNode.Table(
            "profile.dev.package.\"*\"",
            listOf(TomlNode.KeyValue("opt-level", TomlValue.of(3)))
        )
    )
}
