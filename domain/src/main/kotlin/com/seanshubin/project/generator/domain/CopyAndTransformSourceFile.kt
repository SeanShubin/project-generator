package com.seanshubin.project.generator.domain

import java.nio.file.Path

data class CopyAndTransformSourceFile(
    val sourcePath: Path,
    val targetPath: Path,
    val transformations: List<PackageTransformation>
) : Command {
    override fun execute(environment: Environment) {
        val sourceLines = environment.files.readAllLines(sourcePath)
        val transformedLines = transformLines(sourceLines)

        val parent = targetPath.parent
        if (parent != null) {
            environment.files.createDirectories(parent)
        }
        environment.files.write(targetPath, transformedLines)
    }

    private fun transformLines(lines: List<String>): List<String> {
        return lines.map { line -> transformLine(line) }
    }

    private fun transformLine(line: String): String {
        val trimmed = line.trim()

        // Transform package declaration
        if (trimmed.startsWith("package ")) {
            return transformPackageDeclaration(line)
        }

        // Transform import statements
        if (trimmed.startsWith("import ")) {
            return transformImportStatement(line)
        }

        return line
    }

    private fun transformPackageDeclaration(line: String): String {
        val packagePattern = Regex("""^(\s*package\s+)([a-zA-Z0-9_.]+)(.*)$""")
        val match = packagePattern.matchEntire(line) ?: return line

        val (prefix, packageName, suffix) = match.destructured
        val transformedPackage = applyTransformations(packageName)

        return "$prefix$transformedPackage$suffix"
    }

    private fun transformImportStatement(line: String): String {
        val importPattern = Regex("""^(\s*import\s+)([a-zA-Z0-9_.]+)(.*)$""")
        val match = importPattern.matchEntire(line) ?: return line

        val (prefix, importPath, suffix) = match.destructured
        val transformedImport = applyTransformations(importPath)

        return "$prefix$transformedImport$suffix"
    }

    private fun applyTransformations(packageString: String): String {
        for (transformation in transformations) {
            val transformed = transformation.transformString(packageString)
            if (transformed != packageString) {
                return transformed
            }
        }
        return packageString
    }
}
