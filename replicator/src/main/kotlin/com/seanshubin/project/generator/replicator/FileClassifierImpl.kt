package com.seanshubin.project.generator.replicator

class FileClassifierImpl : FileClassifier {
    override fun classify(relativePath: String): FileClass {
        val normalized = relativePath.replace("\\", "/")

        if (normalized.startsWith(".git/") || normalized == ".git") return FileClass.Skip
        if (normalized.startsWith("target/")) return FileClass.Skip
        if (normalized.startsWith("generated/")) return FileClass.Skip
        if (normalized == "pom.xml" || normalized.endsWith("/pom.xml")) return FileClass.Skip

        val extension = normalized.substringAfterLast(".", "")
        return when (extension) {
            "kt", "java", "kts", "groovy", "scala" -> FileClass.SourceTransform
            "xml", "json", "sh", "md", "txt", "html", "properties", "yaml", "yml", "svg" -> FileClass.TextTransform
            else -> FileClass.BinaryCopy
        }
    }
}
