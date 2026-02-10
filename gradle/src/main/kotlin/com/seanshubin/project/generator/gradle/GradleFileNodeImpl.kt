package com.seanshubin.project.generator.gradle

import com.seanshubin.project.generator.core.GradlePluginSpec
import com.seanshubin.project.generator.core.Project
import com.seanshubin.project.generator.maven.VersionLookup

class GradleFileNodeImpl(private val versionLookup: VersionLookup) : GradleFileNode {
    override fun generateBuildGradle(project: Project, spec: GradlePluginSpec): GradleNode {
        val kotlinVersion = versionLookup.latestProductionVersion("org.jetbrains.kotlin", "kotlin-stdlib")
        val pluginPublishVersion = "1.3.0" // Hardcoded until we find the correct Maven coordinates

        val content = mutableListOf<GradleNode>()

        // Plugins block
        content.add(GradleNode.Block("plugins", listOf(
            GradleNode.KotlinPluginDsl("jvm", kotlinVersion),
            GradleNode.BacktickPlugin("java-gradle-plugin"),
            GradleNode.BacktickPlugin("maven-publish"),
            GradleNode.PluginDsl("com.gradle.plugin-publish", pluginPublishVersion)
        )))

        content.add(GradleNode.EmptyLine)

        // Group and version
        content.add(GradleNode.Statement("group = \"${(project.prefix + project.name).joinToString(".")}\""))
        content.add(GradleNode.Statement("version = \"${project.version}\""))

        content.add(GradleNode.EmptyLine)

        // Repositories
        content.add(GradleNode.Block("repositories", listOf(
            GradleNode.Statement("mavenCentral()"),
            GradleNode.Statement("mavenLocal()")
        )))

        content.add(GradleNode.EmptyLine)

        // Dependencies
        val dependencyNodes = mutableListOf<GradleNode>()

        // Add dependencies on Maven modules
        for (dependsOnModule in spec.dependsOn) {
            val artifactId = (project.name + listOf(dependsOnModule)).joinToString("-")
            val groupId = (project.prefix + project.name).joinToString(".")
            dependencyNodes.add(GradleNode.DependencyDsl("implementation", "\"$groupId:$artifactId:${project.version}\""))
        }

        content.add(GradleNode.Block("dependencies", dependencyNodes))

        content.add(GradleNode.EmptyLine)

        // Gradle plugin configuration
        val gradlePluginContent = mutableListOf<GradleNode>()

        // Website and VCS URL come first
        val githubUrl = "https://github.com/${project.developer.githubName}/${project.name.joinToString("-")}"
        val website = spec.website ?: githubUrl
        val vcsUrl = spec.vcsUrl ?: "$githubUrl.git"

        gradlePluginContent.add(GradleNode.MethodCall("website", "set", listOf("\"$website\"")))
        gradlePluginContent.add(GradleNode.MethodCall("vcsUrl", "set", listOf("\"$vcsUrl\"")))
        gradlePluginContent.add(GradleNode.EmptyLine)

        // Plugin declaration with create()
        // Convert gradle-plugin to gradlePlugin (camelCase)
        val pluginName = spec.module.split("-").mapIndexed { index, part ->
            if (index == 0) part else part.replaceFirstChar { it.uppercase() }
        }.joinToString("")
        val pluginDeclarations = mutableListOf<GradleNode>()
        pluginDeclarations.add(GradleNode.Statement("id = \"${spec.pluginId}\""))
        pluginDeclarations.add(GradleNode.Statement("implementationClass = \"${spec.implementationClass}\""))
        pluginDeclarations.add(GradleNode.Statement("displayName = \"${spec.displayName}\""))

        // Handle multiline description
        if (spec.description.length > 80) {
            pluginDeclarations.add(GradleNode.Statement("description ="))
            pluginDeclarations.add(GradleNode.Statement("    \"${spec.description}\""))
        } else {
            pluginDeclarations.add(GradleNode.Statement("description = \"${spec.description}\""))
        }

        // Tags
        if (spec.tags.isNotEmpty()) {
            val tagsList = spec.tags.joinToString(", ") { "\"$it\"" }
            pluginDeclarations.add(GradleNode.MethodCall("tags", "set", listOf("listOf($tagsList)")))
        }

        gradlePluginContent.add(GradleNode.Block("plugins", listOf(
            GradleNode.Block("create(\"$pluginName\")", pluginDeclarations)
        )))

        content.add(GradleNode.Block("gradlePlugin", gradlePluginContent))

        content.add(GradleNode.EmptyLine)

        // Java compatibility with jar generation
        content.add(GradleNode.Block("java", listOf(
            GradleNode.Statement("sourceCompatibility = JavaVersion.VERSION_${project.javaVersion}"),
            GradleNode.Statement("targetCompatibility = JavaVersion.VERSION_${project.javaVersion}"),
            GradleNode.Statement("withJavadocJar()"),
            GradleNode.Statement("withSourcesJar()")
        )))

        content.add(GradleNode.EmptyLine)

        // Kotlin toolchain
        content.add(GradleNode.Block("kotlin", listOf(
            GradleNode.Statement("jvmToolchain(${project.javaVersion})")
        )))

        content.add(GradleNode.EmptyLine)

        // Publishing configuration
        val artifactId = (project.name + listOf(spec.module)).joinToString("-")
        val projectName = (project.prefix + project.name).joinToString(" ") { it.replaceFirstChar { ch -> ch.uppercase() } } + " " +
                          spec.module.split("-").joinToString(" ") { it.replaceFirstChar { ch -> ch.uppercase() } }
        val developerName = project.developer.name
        val developerId = project.developer.githubName

        val pomContent = listOf(
            GradleNode.MethodCall("name", "set", listOf("\"$projectName\"")),
            GradleNode.MethodCall("description", "set", listOf("\"${spec.description}\"")),
            GradleNode.MethodCall("url", "set", listOf("\"$githubUrl\"")),
            GradleNode.EmptyLine,
            GradleNode.Block("licenses", listOf(
                GradleNode.Block("license", listOf(
                    GradleNode.MethodCall("name", "set", listOf("\"The Unlicense\"")),
                    GradleNode.MethodCall("url", "set", listOf("\"https://unlicense.org/\""))
                ))
            )),
            GradleNode.EmptyLine,
            GradleNode.Block("developers", listOf(
                GradleNode.Block("developer", listOf(
                    GradleNode.MethodCall("id", "set", listOf("\"$developerId\"")),
                    GradleNode.MethodCall("name", "set", listOf("\"$developerName\""))
                ))
            )),
            GradleNode.EmptyLine,
            GradleNode.Block("scm", listOf(
                GradleNode.MethodCall("connection", "set", listOf("\"scm:git:$githubUrl.git\"")),
                GradleNode.MethodCall("developerConnection", "set", listOf("\"scm:git:$githubUrl.git\"")),
                GradleNode.MethodCall("url", "set", listOf("\"$githubUrl\""))
            ))
        )

        val publicationContent = listOf(
            GradleNode.Statement("artifactId = \"$artifactId\""),
            GradleNode.EmptyLine,
            GradleNode.Block("pom", pomContent)
        )

        val publicationsContent = listOf(
            GradleNode.Block("create<MavenPublication>(\"pluginMaven\")", publicationContent)
        )

        content.add(GradleNode.Block("publishing", listOf(
            GradleNode.Block("publications", publicationsContent)
        )))

        // Return a wrapper that the renderer will handle specially
        return GradleNode.Block("__ROOT__", content)
    }

    override fun generateSettingsGradle(project: Project, spec: GradlePluginSpec): GradleNode {
        val projectName = (project.prefix + project.name + listOf(spec.module)).joinToString("-")
        return GradleNode.Block("__ROOT__", listOf(
            GradleNode.Statement("rootProject.name = \"$projectName\"")
        ))
    }
}
