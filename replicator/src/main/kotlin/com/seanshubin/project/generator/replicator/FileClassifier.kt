package com.seanshubin.project.generator.replicator

interface FileClassifier {
    fun classify(relativePath: String): FileClass
}
