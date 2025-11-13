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
        val prefix: List<String> =
            keyValueStore.loadWithDefault(listOf("prefix"), default = listOf("prefix", "parts")) as List<String>
        val name: List<String> = keyValueStore.loadWithDefault(listOf("name"), listOf("name", "parts")) as List<String>
        val description: String = keyValueStore.loadWithDefault(listOf("description"), "project description") as String
        val version: String = keyValueStore.loadWithDefault(listOf("version"), "project version") as String
        val language: String = keyValueStore.loadWithDefault(listOf("language"), "kotlin") as String
        val developerName: String =
            keyValueStore.loadWithDefault(listOf("developer", "name"), "developer name") as String
        val developerGithubName: String =
            keyValueStore.loadWithDefault(listOf("developer", "githubName"), "developer github name") as String
        val developerMavenUserName: String =
            keyValueStore.loadWithDefault(listOf("developer", "mavenUserName"), "developer maven user name") as String
        val developerOrganization: String =
            keyValueStore.loadWithDefault(listOf("developer", "organization"), "developer organization") as String
        val developerUrl: String = keyValueStore.loadWithDefault(listOf("developer", "url"), "developer url") as String
        val developer: Developer =
            Developer(developerName, developerGithubName, developerMavenUserName, developerOrganization, developerUrl)
        val dependencies: Map<String, GroupArtifactScope> = loadDependencies()
        val versionOverrides: List<GroupArtifactVersion> = loadVersionOverrides()
        val global: List<String> = keyValueStore.loadWithDefault(listOf("global"), emptyList<String>()) as List<String>
        val modules: Map<String, List<String>> = keyValueStore.loadWithDefault(
            listOf("modules"),
            emptyMap<String, List<String>>()
        ) as Map<String, List<String>>
        val javaVersion: String = keyValueStore.loadWithDefault(listOf("javaVersion"), "25") as String
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

    private fun loadDependencies(): Map<String, GroupArtifactScope> {
        return if (keyValueStore.exists(listOf("dependencies"))) {
            convertDependencies(keyValueStore.load(listOf("dependencies")) as Map<String, Map<String, String>>)
        } else {
            emptyMap()
        }
    }

    private fun loadVersionOverrides(): List<GroupArtifactVersion> {
        return if (keyValueStore.exists(listOf("versionOverrides"))) {
            convertVersionOverrides(keyValueStore.load(listOf("versionOverrides")) as List<Map<String, String>>)
        } else {
            emptyList()
        }
    }

    private fun convertDependencies(map: Map<String, Map<String, String>>): Map<String, GroupArtifactScope> {
        return map.mapValues { (_, value) ->
            GroupArtifactScope(
                value["group"]!!,
                value["artifact"]!!,
                value["scope"]
            )
        }
    }

    private fun convertVersionOverrides(list: List<Map<String, String>>): List<GroupArtifactVersion> {
        return list.map { value ->
            GroupArtifactVersion(
                value["group"]!!,
                value["artifact"]!!,
                value["version"]!!
            )
        }
    }
}
