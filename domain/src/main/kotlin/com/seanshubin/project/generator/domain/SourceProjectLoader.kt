package com.seanshubin.project.generator.domain

import com.seanshubin.project.generator.configuration.JsonFileKeyValueStore
import com.seanshubin.project.generator.configuration.KeyValueStore
import com.seanshubin.project.generator.configuration.loadListOrEmpty
import com.seanshubin.project.generator.configuration.loadMapOrEmpty
import com.seanshubin.project.generator.configuration.loadStringOrDefault
import com.seanshubin.project.generator.contract.FilesContract
import java.nio.file.Path

interface SourceProjectLoader {
    fun loadProject(projectPath: Path): Project
}

class SourceProjectLoaderImpl(
    private val files: FilesContract
) : SourceProjectLoader {
    override fun loadProject(projectPath: Path): Project {
        val specPath = projectPath.resolve("project-specification.json")
        val keyStore = JsonFileKeyValueStore(specPath, files)

        // Load all project fields using same logic as KeyValueStoreRunner
        val prefix = loadStringArray(keyStore, listOf("prefix"), listOf("prefix", "parts"))
        val name = loadStringArray(keyStore, listOf("name"), listOf("name", "parts"))
        val description = keyStore.loadStringOrDefault(listOf("description"), "project description")
        val version = keyStore.loadStringOrDefault(listOf("version"), "project version")
        val language = keyStore.loadStringOrDefault(listOf("language"), "kotlin")

        val developerName = keyStore.loadStringOrDefault(listOf("developer", "name"), "developer name")
        val developerGithubName = keyStore.loadStringOrDefault(listOf("developer", "githubName"), "developer github name")
        val developerMavenUserName = keyStore.loadStringOrDefault(listOf("developer", "mavenUserName"), "developer maven user name")
        val developerOrganization = keyStore.loadStringOrDefault(listOf("developer", "organization"), "developer organization")
        val developerUrl = keyStore.loadStringOrDefault(listOf("developer", "url"), "developer url")
        val developer = Developer(developerName, developerGithubName, developerMavenUserName, developerOrganization, developerUrl)

        val dependencies = loadDependencies(keyStore)
        val versionOverrides = loadVersionOverrides(keyStore)
        val global = loadStringArray(keyStore, listOf("global"), emptyList())
        val modules = loadMapOfListOfString(keyStore, listOf("modules"), emptyMap())
        val javaVersion = keyStore.loadStringOrDefault(listOf("javaVersion"), "25")
        val entryPoints = loadMapOfString(keyStore, listOf("entryPoints"), emptyMap())

        return Project(
            prefix,
            name,
            description,
            version,
            language,
            developer,
            dependencies,
            versionOverrides,
            global,
            modules,
            javaVersion,
            entryPoints,
            null  // Source project itself doesn't have source dependencies
        )
    }

    private fun loadStringArray(keyStore: KeyValueStore, key: List<String>, default: List<String>): List<String> {
        if (!keyStore.exists(key)) return default
        val arraySize = keyStore.arraySize(key)
        return (0 until arraySize).map { index ->
            val subKey = key + index
            keyStore.load(subKey) as String
        }
    }

    private fun loadDependencies(keyStore: KeyValueStore): Map<String, GroupArtifactScope> {
        val theObject = keyStore.loadMapOrEmpty(listOf("dependencies"))
        return theObject.map { (name, dependency) ->
            val dependencyName = name as String
            val dependencyMap = dependency as Map<*, *>
            val group = dependencyMap["group"] as String
            val artifact = dependencyMap["artifact"] as String
            val scope = dependencyMap["scope"] as String?
            dependencyName to GroupArtifactScope(group, artifact, scope)
        }.toMap()
    }

    private fun loadVersionOverrides(keyStore: KeyValueStore): List<GroupArtifactVersion> {
        val versionOverridesList = keyStore.loadListOrEmpty(listOf("versionOverrides"))
        return versionOverridesList.map { versionOverrideMap ->
            val overrideMap = versionOverrideMap as Map<*, *>
            val group = overrideMap["group"] as String
            val artifact = overrideMap["artifact"] as String
            val version = overrideMap["version"] as String
            GroupArtifactVersion(group, artifact, version)
        }
    }

    private fun loadMapOfListOfString(
        keyStore: KeyValueStore,
        key: List<String>,
        default: Map<String, List<String>>
    ): Map<String, List<String>> {
        val theObject = keyStore.loadMapOrEmpty(key)
        if (theObject.isEmpty()) return default
        return theObject.mapKeys { entry -> entry.key as String }
            .mapValues { entry -> (entry.value as List<*>).map { it as String } }
    }

    private fun loadMapOfString(
        keyStore: KeyValueStore,
        key: List<String>,
        default: Map<String, String>
    ): Map<String, String> {
        val theObject = keyStore.loadMapOrEmpty(key)
        if (theObject.isEmpty()) return default
        return theObject.mapKeys { entry -> entry.key as String }
            .mapValues { entry -> entry.value as String }
    }
}
