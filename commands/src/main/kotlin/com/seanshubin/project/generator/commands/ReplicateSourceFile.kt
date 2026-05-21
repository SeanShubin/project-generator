package com.seanshubin.project.generator.commands

import com.seanshubin.project.generator.source.PackageTransformation
import java.nio.file.Path

data class ReplicateSourceFile(
    val sourcePath: Path,
    val targetPath: Path,
    val transformations: List<PackageTransformation>
) : Command {
    override fun execute(environment: Environment) {
        try {
            if (!environment.files.exists(sourcePath)) {
                environment.onSourceFileNotFound(sourcePath)
                return
            }

            val sourceLines = environment.files.readAllLines(sourcePath)
            val transformedLines = sourceLines.map { transformLine(it) }

            val parent = targetPath.parent
            if (parent != null) {
                environment.files.createDirectories(parent)
            }

            val existed = environment.files.exists(targetPath)
            if (existed) {
                val existingLines = environment.files.readAllLines(targetPath)
                if (existingLines == transformedLines) {
                    environment.onFileUnchanged(targetPath)
                    return
                }
                environment.files.write(targetPath, transformedLines)
                environment.onFileModified(targetPath)
            } else {
                environment.files.write(targetPath, transformedLines)
                environment.onFileCreated(targetPath)
            }
        } catch (e: Exception) {
            environment.onFileTransformationError(sourcePath, targetPath, e.message ?: "Unknown error")
            throw e
        }
    }

    private fun transformLine(line: String): String {
        val trimmed = line.trim()
        if (trimmed.startsWith("package ")) return transformPackageDeclaration(line)
        if (trimmed.startsWith("import ")) return transformImportStatement(line)
        return line
    }

    private fun transformPackageDeclaration(line: String): String {
        val match = Regex("""^(\s*package\s+)([a-zA-Z0-9_.]+)(.*)$""").matchEntire(line) ?: return line
        val (prefix, packageName, suffix) = match.destructured
        return "$prefix${applyTransformations(packageName)}$suffix"
    }

    private fun transformImportStatement(line: String): String {
        val staticMatch = Regex("""^(\s*import\s+static\s+)([a-zA-Z0-9_.]+)(.*)$""").matchEntire(line)
        if (staticMatch != null) {
            val (prefix, importPath, suffix) = staticMatch.destructured
            return "$prefix${applyTransformations(importPath)}$suffix"
        }
        val match = Regex("""^(\s*import\s+)([a-zA-Z0-9_.]+)(.*)$""").matchEntire(line) ?: return line
        val (prefix, importPath, suffix) = match.destructured
        return "$prefix${applyTransformations(importPath)}$suffix"
    }

    private fun applyTransformations(packageString: String): String {
        for (transformation in transformations) {
            val transformed = transformation.transformString(packageString)
            if (transformed != packageString) return transformed
        }
        return packageString
    }
}
