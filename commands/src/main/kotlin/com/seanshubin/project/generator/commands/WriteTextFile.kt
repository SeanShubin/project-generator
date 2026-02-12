package com.seanshubin.project.generator.commands

import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission.*

data class WriteTextFile(val path: Path, val content: String, val executable: Boolean = false) : Command {
    override fun execute(environment: Environment) {
        val parent = path.parent
        if (parent != null) {
            environment.files.createDirectories(parent)
        }

        val existed = environment.files.exists(path)
        if (existed) {
            val existingContent = environment.files.readString(path)
            if (existingContent == content) {
                environment.onFileUnchanged(path)
                return
            }
        }

        writeFileWithPermissions(environment)

        if (existed) {
            environment.onFileModified(path)
        } else {
            environment.onFileCreated(path)
        }
    }

    private fun writeFileWithPermissions(environment: Environment) {
        environment.files.writeString(path, content)
        if (executable) {
            setExecutablePermissions(environment, path)
        }
    }

    private fun setExecutablePermissions(environment: Environment, path: Path) {
        try {
            environment.files.setPosixFilePermissions(path, executablePermissions)
        } catch (e: UnsupportedOperationException) {
            // POSIX permissions not supported on this platform (e.g., Windows)
            // On Windows, files are writable by default, no action needed
        }
    }

    companion object {
        private val executablePermissions = setOf(
            OWNER_READ, OWNER_WRITE, OWNER_EXECUTE,
            GROUP_READ, GROUP_EXECUTE,
            OTHERS_READ, OTHERS_EXECUTE
        )
    }
}
