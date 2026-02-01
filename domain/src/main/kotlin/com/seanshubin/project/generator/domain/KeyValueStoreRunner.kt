package com.seanshubin.project.generator.domain

import com.seanshubin.project.generator.dynamic.core.KeyValueStore
import com.seanshubin.project.generator.di.contract.FilesContract
import java.nio.file.Path
import java.nio.file.Paths

class KeyValueStoreRunner(
    private val keyValueStore: KeyValueStore,
    private val baseDirectory: Path,
    private val files: FilesContract,
    private val createRunner: (Project, Path) -> Runnable
) : Runnable {
    override fun run() {
        files.createDirectories(baseDirectory)
        val prefix: List<String> = loadStringArray(listOf("prefix"), listOf("prefix", "parts"))
        val name: List<String> = loadStringArray(listOf("name"), listOf("name", "parts"))
        val description: String = keyValueStore.loadStringOrDefault(listOf("description"), "project description")
        val version: String = keyValueStore.loadStringOrDefault(listOf("version"), "project version")
        val language: String = keyValueStore.loadStringOrDefault(listOf("language"), "kotlin")
        val developerName: String = keyValueStore.loadStringOrDefault(listOf("developer", "name"), "developer name")
        val developerGithubName: String = keyValueStore.loadStringOrDefault(listOf("developer", "githubName"), "developer github name")
        val developerMavenUserName: String = keyValueStore.loadStringOrDefault(listOf("developer", "mavenUserName"), "developer maven user name")
        val developerOrganization: String = keyValueStore.loadStringOrDefault(listOf("developer", "organization"), "developer organization")
        val developerUrl: String = keyValueStore.loadStringOrDefault(listOf("developer", "url"), "developer url")
        val developer =
            Developer(developerName, developerGithubName, developerMavenUserName, developerOrganization, developerUrl)
        val dependencies: Map<String, GroupArtifactScope> = loadDependencies()
        val versionOverrides: List<GroupArtifactVersion> = loadVersionOverrides()
        val global: List<String> = loadStringArray(listOf("global"), emptyList<String>())
        val modules: Map<String, List<String>> = loadMapOfListOfString(listOf("modules"), emptyMap())
        val javaVersion: String = keyValueStore.loadStringOrDefault(listOf("javaVersion"), "25")
        val entryPoints: Map<String, String> = loadEntryPoints()
        val sourceDependency: SourceDependency? = loadSourceDependency()
        val mavenPlugin: List<String> = loadStringArray(listOf("mavenPlugin"), emptyList())
        val project = Project(
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
            sourceDependency,
            mavenPlugin
        )
        val runner = createRunner(project, baseDirectory)
        runner.run()
    }

    private fun loadMapOfListOfString(
        key: List<String>,
        default: Map<String, List<String>>
    ): Map<String, List<String>> {
        val theObject = keyValueStore.loadMapOrEmpty(key)
        if (theObject.isEmpty()) return default
        return theObject.mapKeys { entry -> (entry.key as String) }
            .mapValues { entry -> (entry.value as List<*>).map { it as String } }
    }

    private fun loadDependencies(): Map<String, GroupArtifactScope> {
        val theObject = keyValueStore.loadMapOrEmpty(listOf("dependencies"))
        return theObject.map { (name, dependency) ->
            val dependencyName = name as String
            val dependencyMap = dependency as Map<*, *>
            dependencyName to extractGroupArtifactScope(dependencyMap)
        }.toMap()
    }

    private fun loadVersionOverrides(): List<GroupArtifactVersion> {
        val versionOverridesList = keyValueStore.loadListOrEmpty(listOf("versionOverrides"))
        return versionOverridesList.map { versionOverrideMap ->
            val overrideMap = versionOverrideMap as Map<*, *>
            extractGroupArtifactVersion(overrideMap)
        }
    }

    private fun extractGroupArtifactScope(map: Map<*, *>): GroupArtifactScope {
        val group = map["group"] as String
        val artifact = map["artifact"] as String
        val scope = map["scope"] as String?
        return GroupArtifactScope(group, artifact, scope)
    }

    private fun extractGroupArtifactVersion(map: Map<*, *>): GroupArtifactVersion {
        val group = map["group"] as String
        val artifact = map["artifact"] as String
        val version = map["version"] as String
        return GroupArtifactVersion(group, artifact, version)
    }

    private fun loadEntryPoints(): Map<String, String> {
        val theObject = keyValueStore.loadMapOrEmpty(listOf("entryPoints"))
        return theObject.mapKeys { entry -> entry.key as String }
            .mapValues { entry -> entry.value as String }
    }

    private fun loadStringArray(key: List<String>, default: List<String>): List<String> {
        if (!keyValueStore.exists(key)) return default
        val arraySize = keyValueStore.arraySize(key)
        return (0 until arraySize).map { index ->
            val subKey = key + index
            val value = keyValueStore.load(subKey)
            value as String
        }
    }

    private fun loadSourceDependency(): SourceDependency? {
        if (!keyValueStore.exists(listOf("sourceDependencies"))) return null

        val sourceProjectPathString = keyValueStore.loadStringOrDefault(
            listOf("sourceDependencies", "sourceProjectPath"),
            ""
        )
        if (sourceProjectPathString.isEmpty()) return null

        val moduleMapping = loadMapOfString(
            listOf("sourceDependencies", "moduleMapping"),
            emptyMap()
        )

        val sourceProjectPath = Paths.get(sourceProjectPathString)
        return SourceDependency(sourceProjectPath, moduleMapping)
    }

    private fun loadMapOfString(
        key: List<String>,
        default: Map<String, String>
    ): Map<String, String> {
        val theObject = keyValueStore.loadMapOrEmpty(key)
        if (theObject.isEmpty()) return default
        return theObject.mapKeys { entry -> entry.key as String }
            .mapValues { entry -> entry.value as String }
    }
}
