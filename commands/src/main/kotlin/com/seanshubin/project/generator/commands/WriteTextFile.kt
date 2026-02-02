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
                environment.fileOperationNotifications.fileUnchanged(path)
                return
            }
            environment.files.writeString(path, content)
            if (executable) {
                environment.files.setPosixFilePermissions(path, executablePermissions)
            }
            environment.fileOperationNotifications.fileModified(path)
        } else {
            environment.files.writeString(path, content)
            if (executable) {
                environment.files.setPosixFilePermissions(path, executablePermissions)
            }
            environment.fileOperationNotifications.fileCreated(path)
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
