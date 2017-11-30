package com.seanshubin.project.generator.domain

import java.nio.charset.Charset

class CommandEnvironment(val pomGenerator: PomGenerator,
                         val files: FilesContract,
                         val classLoader: ClassLoaderContract,
                         val charset: Charset)
