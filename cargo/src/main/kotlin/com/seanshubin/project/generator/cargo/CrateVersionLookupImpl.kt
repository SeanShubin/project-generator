package com.seanshubin.project.generator.cargo

import com.seanshubin.project.generator.core.VersionRules
import com.seanshubin.project.generator.dynamic.json.JsonMappers
import com.seanshubin.project.generator.http.Http

/**
 * Looks crate versions up in the crates.io sparse index.
 *
 * The sparse index is used rather than the `crates.io/api/v1` endpoint because
 * the API rejects requests without a `User-Agent` header with a 403, and the
 * index does not -- so this works through [Http] as it stands, with no header
 * support.  The index is also the interface crates.io asks tools to use.
 *
 * The response is newline-delimited JSON, one object per published version in
 * publication order, each carrying at least `vers` and `yanked`.
 */
class CrateVersionLookupImpl(
    private val http: Http,
    private val lookupVersionEvent: (String, String, String) -> Unit
) : CrateVersionLookup {
    override fun latestProductionVersion(crateName: String): String {
        val uri = "$indexBaseUri/${indexPath(crateName)}"
        val body = http.getAssertSuccess(uri)
        val version = latestProductionVersionFrom(crateName, body)
        lookupVersionEvent(uri, crateName, version)
        return version
    }

    private fun latestProductionVersionFrom(crateName: String, body: String): String {
        val versions = body.lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { JsonMappers.parse<Map<String, Any?>>(it) }
            .filterNot { it["yanked"] == true }
            .mapNotNull { it["vers"] as? String }
            .filter(VersionRules::isReleaseVersion)
            .toList()
        if (versions.isEmpty()) {
            throw RuntimeException("No non-yanked release version found for crate '$crateName'")
        }
        return versions.sortedWith(VersionRules.versionNumberComparator).last()
    }

    /**
     * The index is sharded by name length so that no directory grows without
     * bound: one- and two-character names live under `1/` and `2/`, a
     * three-character name under `3/<first letter>/`, and everything longer
     * under `<first two>/<next two>/`.
     */
    private fun indexPath(crateName: String): String {
        val name = crateName.lowercase()
        return when (name.length) {
            0 -> throw RuntimeException("Crate name must not be empty")
            1 -> "1/$name"
            2 -> "2/$name"
            3 -> "3/${name.substring(0, 1)}/$name"
            else -> "${name.substring(0, 2)}/${name.substring(2, 4)}/$name"
        }
    }

    companion object {
        private const val indexBaseUri = "https://index.crates.io"
    }
}
