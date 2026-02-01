package com.seanshubin.project.generator.core

data class Developer(
    val name: String,          // name of the developer, for the "developers" section of the pom file
    val githubName: String,    // github name of the developer, for the "scm" section of the pom file
    val mavenUserName: String, // maven user name, required for the settings.xml file used to push to maven central
    val organization: String,  // developer or organization name, for the "developers" section of the pom file
    val url: String            // developer or organization url, for the "developers" section of the pom file
)
