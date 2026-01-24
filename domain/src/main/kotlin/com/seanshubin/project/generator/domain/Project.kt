package com.seanshubin.project.generator.domain

data class Project(
    val prefix: List<String>, // words of the reverse domain name, host only, do not include the path
    val name: List<String>, // words distinguishing this project, does not include prefix.  The group id will be prefix + name
    val description: String, // description of the project, required in order to push to maven central
    val version: String, // the project version
    val language: String, // the project language
    val developer: Developer, // developer information, required in order to push to maven central
    val dependencies: Map<String, GroupArtifactScope>, // the dependencies, mapped by alias
    val versionOverrides: List<GroupArtifactVersion>,
    val global: List<String>, // global dependencies will be specified in the parent pom file, so they will be included in each child module without having to be specified
    val modules: Map<String, List<String>>, // the dependency structure, each dependency can be identified by either module name or alias
    val javaVersion: String, // which java version to use
    val entryPoints: Map<String, String> = emptyMap() // optional entry point class names for modules that should generate executable JARs
)
