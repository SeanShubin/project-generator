package com.seanshubin.project.generator.domain

object VersionRules {
    fun isReleaseVersion(version:String):Boolean {
        val versionParts = version.split(".")
        return versionParts.all(allDigits)
    }
    private val allDigitsRegex = Regex("""[0-9]+""")
    private val allDigits = { s:String ->
        s.matches(allDigitsRegex)
    }
    val versionNumberComparator = Comparator<String> { o1, o2 ->
        val o1List = o1.toListOfInts()
        val o2List = o2.toListOfInts()
        listOfIntComparator.compare(o1List, o2List)
    }
    private fun String.toListOfInts():List<Int> = split(".").map{it.toInt()}
    private val listOfIntComparator = ListComparator<Int>()
}
