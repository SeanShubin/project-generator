package com.seanshubin.project.generator.cargo

/**
 * Where a Cargo workspace keeps its member crates.
 *
 * Shared between the manifest emitter, which writes these paths into
 * `[workspace] members` and the `path` of each internal dependency, and the
 * command emitter, which creates the directories -- the two have to agree.
 */
object CargoLayout {
    const val CRATES_DIRECTORY = "crates"
    const val SOURCE_DIRECTORY = "src"
    const val LIB_FILE = "lib.rs"
    const val MAIN_FILE = "main.rs"
    const val MANIFEST_FILE = "Cargo.toml"

    /** A member crate's directory, relative to the workspace root. */
    fun cratePath(module: String): String = "$CRATES_DIRECTORY/$module"
}
