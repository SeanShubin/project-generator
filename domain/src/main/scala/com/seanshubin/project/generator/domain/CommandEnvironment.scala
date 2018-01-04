package com.seanshubin.project.generator.domain

import java.nio.file.Path

class CommandEnvironment(val baseDirectory: Path,
                         val pomGenerator: PomGenerator,
                         val files: FilesContract,
                         val classLoader: ClassLoaderContract)
