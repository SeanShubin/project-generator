package com.seanshubin.project.generator.replicator

sealed class FileClass {
    object Skip : FileClass()
    object BinaryCopy : FileClass()
    object TextTransform : FileClass()
    object SourceTransform : FileClass()
}
