package com.seanshubin.project.generator.domain

import com.seanshubin.project.generator.configuration.KeyValueStore
import com.seanshubin.project.generator.contract.FilesContract
import java.nio.file.Path

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
        val description: String = keyValueStore.loadOrCreateDefault(listOf("description"), "project description") as String
        val version: String = keyValueStore.loadOrCreateDefault(listOf("version"), "project version") as String
        val language: String = keyValueStore.loadOrCreateDefault(listOf("language"), "kotlin") as String
        val developerName: String =
            keyValueStore.loadOrCreateDefault(listOf("developer", "name"), "developer name") as String
        val developerGithubName: String =
            keyValueStore.loadOrCreateDefault(listOf("developer", "githubName"), "developer github name") as String
        val developerMavenUserName: String =
            keyValueStore.loadOrCreateDefault(listOf("developer", "mavenUserName"), "developer maven user name") as String
        val developerOrganization: String =
            keyValueStore.loadOrCreateDefault(listOf("developer", "organization"), "developer organization") as String
        val developerUrl: String = keyValueStore.loadOrCreateDefault(listOf("developer", "url"), "developer url") as String
        val developer =
            Developer(developerName, developerGithubName, developerMavenUserName, developerOrganization, developerUrl)
        val dependencies: Map<String, GroupArtifactScope> = loadDependencies()
        val versionOverrides: List<GroupArtifactVersion> = loadVersionOverrides()
        val global: List<String> = loadStringArray(listOf("global"), emptyList<String>())
        val modules: Map<String, List<String>> = loadMapOfListOfString(listOf("modules"), emptyMap())
        val javaVersion: String = keyValueStore.loadOrCreateDefault(listOf("javaVersion"), "25") as String
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
            javaVersion
        )
        val runner = createRunner(project, baseDirectory)
        runner.run()
    }

    private fun loadMapOfListOfString(
        key: List<String>,
        default: Map<String, List<String>>
    ): Map<String, List<String>> {
        return if (keyValueStore.exists(key)) {
            val theObject = keyValueStore.load(key) as Map<*, *>
            theObject.mapKeys { entry -> (entry.key as String) }
                .mapValues { entry -> (entry.value as List<*>).map { it as String } }
        } else {
            default
        }
    }

    private fun loadDependencies(): Map<String, GroupArtifactScope> {
        return if (keyValueStore.exists(listOf("dependencies"))) {
            val theObject = keyValueStore.load(listOf("dependencies")) as Map<*, *>
            theObject.map { (name, dependency) ->
                name as String
                dependency as Map<*, *>
                val group = dependency["group"] as String
                val artifact = dependency["artifact"] as String
                val scope = dependency["scope"] as String?
                name to GroupArtifactScope(group, artifact, scope)
            }.toMap()
        } else {
            emptyMap()
        }
    }

    private fun loadVersionOverrides(): List<GroupArtifactVersion> {
        return if (keyValueStore.exists(listOf("versionOverrides"))) {
            val versionOverridesList = keyValueStore.load(listOf("versionOverrides")) as List<*>
            versionOverridesList.map { versionOverrideMap ->
                versionOverrideMap as Map<*, *>
                val group = versionOverrideMap["group"] as String
                val artifact = versionOverrideMap["artifact"] as String
                val version = versionOverrideMap["version"] as String
                GroupArtifactVersion(group, artifact, version)
            }
        } else {
            emptyList()
        }
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
}
