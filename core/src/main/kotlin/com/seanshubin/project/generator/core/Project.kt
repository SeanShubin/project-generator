package com.seanshubin.project.generator.core

data class Project(
    val prefix: List<String>, // words of the reverse domain name, host only, do not include the path
    val name: List<String>, // words distinguishing this project, does not include prefix.  The group id will be prefix + name
    val description: String, // description of the project, required in order to push to maven central
    val version: String, // the project version
    val language: String, // the project language
    val developer: Developer, // developer information, required in order to push to maven central
    val dependencies: Map<String, DependencySpec>, // the dependencies, can be either external (maven) or internal (module) references
    val versionOverrides: List<GroupArtifactVersion>,
    val global: List<String>, // global dependencies will be specified in the parent pom file, so they will be included in each child module without having to be specified
    val modules: Map<String, List<String>>, // the dependency structure, each dependency can be identified by either module name or alias
    val javaVersion: String, // which java version to use
    val entryPoints: Map<String, String> = emptyMap(), // optional entry point class names for modules that should generate executable JARs
    val sourceDependencies: List<SourceDependency> = emptyList(), // optional source dependencies for copying and transforming code from external projects
    val mavenPlugin: List<String> = emptyList(), // modules that are Maven plugins
    val gradlePlugin: List<GradlePluginSpec> = emptyList(), // modules that are Gradle plugins
    val exports: List<String> = emptyList() // modules designed to be imported by other projects via source dependencies
)

data class GradlePluginSpec(
    val module: String, // directory name for the Gradle plugin module
    val pluginId: String, // plugin ID for the Gradle plugin portal
    val implementationClass: String, // fully qualified class name of the plugin implementation
    val displayName: String, // display name for the plugin
    val description: String, // description for the plugin
    val tags: List<String> = emptyList(), // tags for the Gradle plugin portal
    val dependsOn: List<String> = emptyList(), // Maven module dependencies that this Gradle plugin depends on
    val website: String? = null, // website URL, defaults to GitHub URL if not specified
    val vcsUrl: String? = null // VCS URL, defaults to GitHub URL if not specified
)
