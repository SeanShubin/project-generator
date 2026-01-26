package com.seanshubin.project.generator.domain

data class PackageTransformation(
    val sourcePackage: List<String>,
    val targetPackage: List<String>
) {
    fun matches(packageName: List<String>): Boolean {
        if (packageName.size < sourcePackage.size) return false
        return packageName.take(sourcePackage.size) == sourcePackage
    }

    fun transform(packageName: List<String>): List<String> {
        if (!matches(packageName)) return packageName
        val suffix = packageName.drop(sourcePackage.size)
        return targetPackage + suffix
    }

    fun transformString(packageString: String): String {
        val parts = packageString.split(".")
        val transformed = transform(parts)
        return transformed.joinToString(".")
    }
}
