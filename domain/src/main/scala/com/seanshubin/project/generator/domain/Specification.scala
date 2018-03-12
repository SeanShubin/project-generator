package com.seanshubin.project.generator.domain

import scala.collection.immutable.ListMap

object Specification {

  case class Project(prefix: Seq[String], // words of the reverse domain name, host only, do not include the path
                     name: Seq[String], // words distinguishing this project, does not include prefix.  The group id will be prefix + name
                     description: String, // description of the project, required in order to push to maven central
                     version: String, // the project version
                     developer: Developer, // developer information, required in order to push to maven central
                     dependencies: ListMap[String, Dependency], // the dependencies
                     global: Seq[String], // these dependencies will be specified in the parent pom file, so they will be included in each child module without having to be specified
                     modules: ListMap[String, Seq[String]], // the dependency structure
                     detangler: Seq[String], // which modules have the detangler enabled, detangler fails the build upon detecting a dependency cycle
                     consoleEntryPoint: Map[String, String], // module name -> qualified class name, for each entry point.  Used to generate the maven-assembly-plugin section to create an executable jar
                     mavenPlugin: Seq[String], // used to specify which modules have "packaging" element set to "maven-plugin"
                     primary: Option[String], // used for generating detangler configuration.  not needed for modules that are a console entry point.  used to indicate that this module is responsible for generating the detangler report for itself and all other modules
                     javaVersion: Option[String])               // which java version to use
  {
    def baseDirectoryName: String = name.mkString("-")

    def nullSafe: Project = copy(
      global = Option(global).getOrElse(Seq()),
      detangler = Option(detangler).getOrElse(Seq()),
      consoleEntryPoint = Option(consoleEntryPoint).getOrElse(Map()),
      mavenPlugin = Option(mavenPlugin).getOrElse(Seq()),
    )
  }

  case class Dependency(group: String,                   // maven group id
                        artifact: String,                // maven artifact id
                        lockedAtVersion: Option[String], // defaults to latest version, only specify this if you want to hold the version back
                        scope: Option[String])           // defaults to omitted, which in maven defaults to compiled

  case class Developer(name: String,          // name of the developer, for the "developers" section of the pom file
                       githubName: String,    // github name of the developer, for the "scm" section of the pom file
                       mavenUserName: String, // maven user name, required for the settings.xml file used to push to maven central
                       organization: String,  // developer or organization name, for the "developers" section of the pom file
                       url: String)           // developer or organization url, for the "developers" section of the pom file

}
