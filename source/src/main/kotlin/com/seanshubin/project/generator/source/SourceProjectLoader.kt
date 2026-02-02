package com.seanshubin.project.generator.source

import com.seanshubin.project.generator.core.Developer
import com.seanshubin.project.generator.core.GroupArtifactScope
import com.seanshubin.project.generator.core.GroupArtifactVersion
import com.seanshubin.project.generator.core.Project
import com.seanshubin.project.generator.di.contract.FilesContract
import com.seanshubin.project.generator.dynamic.core.KeyValueStore
import com.seanshubin.project.generator.dynamic.json.JsonFileKeyValueStore
import com.seanshubin.project.generator.dynamic.json.loadListOrEmpty
import com.seanshubin.project.generator.dynamic.json.loadMapOrEmpty
import com.seanshubin.project.generator.dynamic.json.loadStringOrDefault
import java.nio.file.Path

/**
 * Loads project metadata from an external source project.
 *
 * Reads the project-specification.json file from an external project to extract
 * its package structure, module configuration, and other metadata needed for
 * source code transformation.
 */
interface SourceProjectLoader {
    /**
     * Loads a Project from the specified project directory.
     *
     * @param projectPath The absolute path to the external project's root directory
     * @return The Project metadata loaded from the external project
     * @throws IllegalArgumentException if the project path doesn't exist, is not a directory,
     *         or doesn't contain a project-specification.json file
     */
    fun loadProject(projectPath: Path): Project
}

class SourceProjectLoaderImpl(
    private val files: FilesContract
) : SourceProjectLoader {
    override fun loadProject(projectPath: Path): Project {
        if (!files.exists(projectPath)) {
            throw IllegalArgumentException("Source project path does not exist: $projectPath")
        }
        if (!files.isDirectory(projectPath)) {
            throw IllegalArgumentException("Source project path is not a directory: $projectPath")
        }

        val specPath = projectPath.resolve("project-specification.json")
        if (!files.exists(specPath)) {
            throw IllegalArgumentException("Source project specification file not found: $specPath")
        }

        val keyStore = JsonFileKeyValueStore(files, specPath)

        // Load all project fields using same logic as KeyValueStoreRunner
        val prefix = loadStringArray(keyStore, listOf("prefix"), listOf("prefix", "parts"))
        val name = loadStringArray(keyStore, listOf("name"), listOf("name", "parts"))
        val description = keyStore.loadStringOrDefault(listOf("description"), "project description")
        val version = keyStore.loadStringOrDefault(listOf("version"), "project version")
        val language = keyStore.loadStringOrDefault(listOf("language"), "kotlin")

        val developerName = keyStore.loadStringOrDefault(listOf("developer", "name"), "developer name")
        val developerGithubName =
            keyStore.loadStringOrDefault(listOf("developer", "githubName"), "developer github name")
        val developerMavenUserName =
            keyStore.loadStringOrDefault(listOf("developer", "mavenUserName"), "developer maven user name")
        val developerOrganization =
            keyStore.loadStringOrDefault(listOf("developer", "organization"), "developer organization")
        val developerUrl = keyStore.loadStringOrDefault(listOf("developer", "url"), "developer url")
        val developer =
            Developer(developerName, developerGithubName, developerMavenUserName, developerOrganization, developerUrl)

        val dependencies = loadDependencies(keyStore)
        val versionOverrides = loadVersionOverrides(keyStore)
        val global = loadStringArray(keyStore, listOf("global"), emptyList())
        val modules = loadMapOfListOfString(keyStore, listOf("modules"), emptyMap())
        val javaVersion = keyStore.loadStringOrDefault(listOf("javaVersion"), "25")
        val entryPoints = loadMapOfString(keyStore, listOf("entryPoints"), emptyMap())
        val mavenPlugin = loadStringArray(keyStore, listOf("mavenPlugin"), emptyList())

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
            null,  // Source project itself doesn't have source dependencies
            mavenPlugin
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
