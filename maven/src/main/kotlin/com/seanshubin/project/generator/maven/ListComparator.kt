package com.seanshubin.project.generator.maven

class ListComparator<T : Comparable<T>> : Comparator<List<T>> {
    override fun compare(listA: List<T>?, listB: List<T>?): Int {
        return if (listA == null) {
            if (listB == null) {
                0
            } else {
                -1
            }
        } else {
            if (listB == null) {
                1
            } else {
                compareNotNull(listA, listB)
            }
        }
    }

    private fun compareNotNull(listA: List<T>, listB: List<T>): Int {
        return if (listA.isEmpty()) {
            if (listB.isEmpty()) {
                0
            } else {
                -1
            }
        } else {
            if (listB.isEmpty()) {
                1
            } else {
                compareNotEmpty(listA, listB)
            }
        }
    }

    private fun compareNotEmpty(listA: List<T>, listB: List<T>): Int {
        val a = listA[0]
        val b = listB[0]
        val abCompare = a.compareTo(b)
        return if (abCompare == 0) {
            compareNotNull(listA.drop(1), listB.drop(1))
        } else {
            abCompare
        }
    }
}
