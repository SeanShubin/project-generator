package com.seanshubin.project.generator.commands

import java.nio.file.Path

/**
 * Writes [content] to [path] only when nothing is there yet.
 *
 * This exists for files a build system requires in order to work at all but
 * whose contents belong to the developer. A Cargo crate does not compile
 * without a `src/lib.rs` or `src/main.rs`, so generation has to supply one;
 * overwriting it on the next run would destroy real code, which is what
 * [WriteFile] and [WriteTextFile] would do.
 */
data class CreateFileIfAbsent(val path: Path, val content: String) : Command {
    override fun execute(environment: Environment) {
        if (environment.files.exists(path)) {
            environment.onFileUnchanged(path)
            return
        }
        val parent = path.parent
        if (parent != null) {
            environment.files.createDirectories(parent)
        }
        environment.files.writeString(path, content)
        environment.onFileCreated(path)
    }
}
