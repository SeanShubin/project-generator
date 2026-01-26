package com.seanshubin.project.generator.domain

object VersionRules {
    private const val versionSeparator = "."

    fun isReleaseVersion(version: String): Boolean {
        val versionParts = version.split(versionSeparator)
        return versionParts.all(allDigits)
    }

    private val allDigitsRegex = Regex("""[0-9]+""")
    private val allDigits = { s: String ->
        s.matches(allDigitsRegex)
    }
    val versionNumberComparator = Comparator<String> { o1, o2 ->
        val o1List = o1.toListOfInts()
        val o2List = o2.toListOfInts()
        listOfIntComparator.compare(o1List, o2List)
    }

    private fun String.toListOfInts(): List<Int> = split(versionSeparator).map { it.toInt() }
    private val listOfIntComparator = ListComparator<Int>()
}
