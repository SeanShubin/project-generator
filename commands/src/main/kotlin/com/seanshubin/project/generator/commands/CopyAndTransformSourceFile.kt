package com.seanshubin.project.generator.commands

import com.seanshubin.project.generator.source.PackageTransformation
import java.nio.file.Path

/**
 * Command that copies a source file and transforms package declarations and import statements.
 *
 * This command:
 * 1. Reads the source file
 * 2. Transforms each line:
 *    - Package declarations: Replaces package prefix (e.g., "package com.old.f" → "package com.new.j")
 *    - Import statements: Updates imports from copied modules (e.g., "import com.old.g.Utils" → "import com.new.k.Utils")
 *    - Static imports: Handles "import static" statements
 *    - Wildcard imports: Preserves wildcards (e.g., "import com.old.f.*" → "import com.new.j.*")
 *    - External imports: Left unchanged (imports from packages not in the transformation list)
 *    - All other code: Copied as-is
 * 3. Inserts a comment after imports indicating the source of the file
 * 4. Writes the transformed content to the target path
 *
 * Example transformation with ["com", "old", "project", "f"] → ["com", "new", "project", "j"]:
 * - "package com.old.project.f.util" → "package com.new.project.j.util"
 * - "import com.old.project.f.Helper" → "import com.new.project.j.Helper"
 * - "import com.external.Library" → "import com.external.Library" (unchanged)
 */
data class CopyAndTransformSourceFile(
    val sourcePath: Path,
    val targetPath: Path,
    val transformations: List<PackageTransformation>,
    val sourceProjectPath: Path,
    val sourceModule: String
) : Command {
    override fun execute(environment: Environment) {
        try {
            if (!environment.files.exists(sourcePath)) {
                environment.onSourceFileNotFound(sourcePath)
                return
            }

            val sourceLines = environment.files.readAllLines(sourcePath)
            val transformedLines = transformLines(sourceLines)

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
            environment.onFileTransformationError(
                sourcePath,
                targetPath,
                e.message ?: "Unknown error"
            )
            throw e
        }
    }

    private fun transformLines(lines: List<String>): List<String> {
        val transformedLines = lines.map { line -> transformLine(line) }
        return insertSourceComment(transformedLines)
    }

    private fun insertSourceComment(lines: List<String>): List<String> {
        // Find the index after the last import statement
        var lastImportIndex = -1
        for (i in lines.indices) {
            val trimmed = lines[i].trim()
            if (trimmed.startsWith("import ")) {
                lastImportIndex = i
            }
        }

        // If no imports found, try to find package declaration
        if (lastImportIndex == -1) {
            for (i in lines.indices) {
                val trimmed = lines[i].trim()
                if (trimmed.startsWith("package ")) {
                    lastImportIndex = i
                    break
                }
            }
        }

        // If still not found, insert at the beginning
        val insertionIndex = if (lastImportIndex >= 0) lastImportIndex + 1 else 0

        val comment = buildList {
            add("")
            add("//")
            add("// This file was imported from: $sourceProjectPath")
            add("// Module: $sourceModule")
            add("//")
            add("// Before editing this file, consider whether updating the source project")
            add("// and re-importing would be a better approach.")
            add("//")
        }

        return buildList {
            addAll(lines.subList(0, insertionIndex))
            addAll(comment)
            if (insertionIndex < lines.size) {
                addAll(lines.subList(insertionIndex, lines.size))
            }
        }
    }

    private fun transformLine(line: String): String {
        val trimmed = line.trim()

        // Transform package declaration
        if (trimmed.startsWith("package ")) {
            return transformPackageDeclaration(line)
        }

        // Transform import statements (including static imports)
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
        // Handle static imports: import static com.foo.Bar.method
        val staticImportPattern = Regex("""^(\s*import\s+static\s+)([a-zA-Z0-9_.]+)(.*)$""")
        val staticMatch = staticImportPattern.matchEntire(line)
        if (staticMatch != null) {
            val (prefix, importPath, suffix) = staticMatch.destructured
            val transformedImport = applyTransformations(importPath)
            return "$prefix$transformedImport$suffix"
        }

        // Handle regular imports (including wildcards): import com.foo.Bar or import com.foo.*
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
