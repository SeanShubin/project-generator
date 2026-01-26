package com.seanshubin.project.generator.domain

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
 * 3. Writes the transformed content to the target path
 *
 * Example transformation with ["com", "old", "project", "f"] → ["com", "new", "project", "j"]:
 * - "package com.old.project.f.util" → "package com.new.project.j.util"
 * - "import com.old.project.f.Helper" → "import com.new.project.j.Helper"
 * - "import com.external.Library" → "import com.external.Library" (unchanged)
 */
data class CopyAndTransformSourceFile(
    val sourcePath: Path,
    val targetPath: Path,
    val transformations: List<PackageTransformation>
) : Command {
    override fun execute(environment: Environment) {
        try {
            if (!environment.files.exists(sourcePath)) {
                System.err.println("Error: Source file not found: $sourcePath")
                return
            }

            val sourceLines = environment.files.readAllLines(sourcePath)
            val transformedLines = transformLines(sourceLines)

            val parent = targetPath.parent
            if (parent != null) {
                environment.files.createDirectories(parent)
            }
            environment.files.write(targetPath, transformedLines)
        } catch (e: Exception) {
            System.err.println("Error copying and transforming file from $sourcePath to $targetPath: ${e.message}")
            throw e
        }
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
