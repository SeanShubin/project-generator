package com.seanshubin.project.generator.cargo

interface CrateVersionLookup {
    /**
     * The latest non-yanked release version of [crateName].
     *
     * A crate is identified by a single flat name, unlike the group/artifact
     * pair Maven Central needs, which is why this is a separate interface from
     * [com.seanshubin.project.generator.maven.VersionLookup] rather than an
     * overload of it.
     */
    fun latestProductionVersion(crateName: String): String
}
