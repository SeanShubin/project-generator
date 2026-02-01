package com.seanshubin.project.generator.domain

import java.nio.file.Path

data class WriteTextFile(val path: Path, val content: String, val executable: Boolean = false) : Command {
    override fun execute(environment: Environment) {
        val parent = path.parent
        if (parent != null) {
            environment.files.createDirectories(parent)
        }
        environment.files.writeString(path, content)
        if (executable) {
            environment.files.setPosixFilePermissions(path, setOf(
                java.nio.file.attribute.PosixFilePermission.OWNER_READ,
                java.nio.file.attribute.PosixFilePermission.OWNER_WRITE,
                java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE,
                java.nio.file.attribute.PosixFilePermission.GROUP_READ,
                java.nio.file.attribute.PosixFilePermission.GROUP_EXECUTE,
                java.nio.file.attribute.PosixFilePermission.OTHERS_READ,
                java.nio.file.attribute.PosixFilePermission.OTHERS_EXECUTE
            ))
        }
    }
}
