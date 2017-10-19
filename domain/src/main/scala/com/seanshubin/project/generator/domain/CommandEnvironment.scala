package com.seanshubin.project.generator.domain

import java.nio.charset.Charset

case class CommandEnvironment(files: FilesContract, classLoader: ClassLoaderContract, charset: Charset)
