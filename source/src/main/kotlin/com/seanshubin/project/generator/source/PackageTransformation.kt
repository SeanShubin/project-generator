package com.seanshubin.project.generator.source

/**
 * Represents a package name transformation rule for source code copying.
 *
 * This class transforms package names from a source project to a target project by replacing
 * the source package prefix with the target package prefix while preserving any remaining
 * package path segments.
 *
 * Example: Transforming from ["com", "old", "project", "f"] to ["com", "new", "project", "j"]
 * - If given ["com", "old", "project", "f", "util", "Helper"], transforms to ["com", "new", "project", "j", "util", "Helper"]
 * - If given ["com", "other", "package"], returns unchanged (doesn't match source prefix)
 *
 * This transformation is applied to both package declarations and import statements.
 */
data class PackageTransformation(
    val sourcePackage: List<String>,
    val targetPackage: List<String>
) {
    /**
     * Checks if the given package name starts with the source package prefix.
     *
     * @param packageName The package name parts to check
     * @return true if packageName starts with sourcePackage, false otherwise
     */
    fun matches(packageName: List<String>): Boolean {
        if (packageName.size < sourcePackage.size) return false
        return packageName.take(sourcePackage.size) == sourcePackage
    }

    /**
     * Transforms a package name by replacing the source prefix with the target prefix.
     *
     * If the package name doesn't match the source prefix, returns it unchanged.
     * Otherwise, replaces the source prefix with the target prefix and preserves the remaining path.
     *
     * @param packageName The package name parts to transform
     * @return The transformed package name, or the original if it doesn't match
     */
    fun transform(packageName: List<String>): List<String> {
        if (!matches(packageName)) return packageName
        val suffix = packageName.drop(sourcePackage.size)
        return targetPackage + suffix
    }

    /**
     * Transforms a dot-separated package string.
     *
     * Convenience method that splits the string, applies the transformation, and rejoins.
     *
     * @param packageString The dot-separated package string (e.g., "com.example.util")
     * @return The transformed package string
     */
    fun transformString(packageString: String): String {
        val parts = packageString.split(".")
        val transformed = transform(parts)
        return transformed.joinToString(".")
    }
}
