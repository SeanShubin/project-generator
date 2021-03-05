package com.seanshubin.project.generator.domain

import com.seanshubin.project.generator.domain.Specification.Developer

class PomGeneratorImpl(newline: String, repository: Repository) extends PomGenerator {
  override def generateParent(project: Specification.Project): Seq[String] = {
    val pomGenerator = new ParentPomGenerator(
      project.prefix,
      project.name,
      project.description,
      project.version,
      project.language,
      project.dependencies,
      project.global,
      project.modules,
      project.developer,
      project.javaVersion,
      project.deployableToMavenCentral.getOrElse(false)
    )
    pomGenerator.generate()
  }

  override def generateModule(project: Specification.Project, moduleName: String): Seq[String] = {
    val pomGenerator = ModulePomGenerator(
      project.prefix,
      project.name,
      project.description,
      project.version,
      project.language,
      project.dependencies,
      project.modules,
      project.developer,
      moduleName,
      project.detangler,
      project.consoleEntryPoint,
      project.mavenPlugin,
      project.javaVersion,
      project.deployableToMavenCentral.getOrElse(false)
    )
    pomGenerator.generate()
  }

  abstract class PomGenerator(prefix: Seq[String],
                              name: Seq[String],
                              description: String,
                              versionString: String,
                              language: String,
                              dependencyMap: Map[String, Specification.Dependency],
                              moduleMap: Map[String, Seq[String]],
                              developer: Developer,
                              maybeJavaVersion: Option[String],
                              deployableToMavenCentral: Boolean) {
    def generate(): Seq[String] = {
      Seq(
        """<?xml version="1.0" encoding="UTF-8" standalone="no"?>""",
        """<project xmlns="http://maven.apache.org/POM/4.0.0"
          |         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"""".stripMargin,
        """         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">""") ++
        projectLines() ++
        Seq("</project>")
    }

    def projectLines(): Seq[String] = {
      val linesForDeployableToMavenCentral = if (deployableToMavenCentral) {
        url() ++
          inceptionYear() ++
          licenses() ++
          organization() ++
          developers() ++
          contributors() ++
          issueManagement() ++
          ciManagement() ++
          mailingLists() ++
          scm() ++
          prerequisites() ++
          repositories() ++
          pluginRepositories() ++
          distributionManagement() ++
          profiles()
      } else Seq()
      model() ++
        generateGroup() ++
        generateArtifact() ++
        generateVersion() ++
        packaging() ++
        dependencies() ++
        parent() ++
        dependencyManagement() ++
        modules() ++
        properties() ++
        build() ++
        reporting() ++
        generateName() ++
        generateDescription() ++
        linesForDeployableToMavenCentral
    }

    def model(): Seq[String] = {
      wrap("modelVersion", "4.0.0")
    }

    def generateGroup(): Seq[String] = comment("groupId")

    def fullGroup(): Seq[String] = {
      val groupContents = (prefix ++ name).mkString(".")
      groupElement(groupContents)
    }

    def groupElement(contents: String): Seq[String] = {
      wrap("groupId", contents)
    }

    def parentArtifact(): Seq[String] = {
      val artifactContents = (name ++ Seq("parent")).mkString("-")
      artifactElement(artifactContents)
    }

    def moduleArtifact(moduleName: String): Seq[String] = {
      val artifactContents = (name ++ Seq(moduleName)).mkString("-")
      artifactElement(artifactContents)
    }

    def generateArtifact(): Seq[String]

    def artifactElement(contents: String): Seq[String] = {
      wrap("artifactId", contents)
    }

    def generateVersion(): Seq[String] = comment("version")

    def fullVersion(): Seq[String] = {
      versionElement(versionString)
    }

    def versionElement(contents: String): Seq[String] = {
      wrap("version", contents)
    }

    def scope(maybeContents: Option[String]): Seq[String] = {
      maybeContents match {
        case Some(contents) => wrap("scope", contents)
        case None => Seq()
      }
    }

    def packaging(): Seq[String] = comment("packaging")

    def pomPackaging(): Seq[String] = {
      wrap("packaging", "pom")
    }

    def dependencies(): Seq[String]

    def dependenciesUsingFunction(dependencies: Map[String, Specification.Dependency], dependencyFunction: (String, Specification.Dependency) => Seq[String]): Seq[String] = {
      val contents = dependencies.flatMap(dependencyFunction.tupled).toSeq
      wrap("dependencies", contents)
    }

    def parentDependencies(global: Seq[String]): Seq[String] = {
      val dependenciesInParent = dependencyMap.filterKeys(global.contains)
      dependenciesUsingFunction(dependenciesInParent, partialDependencyValue)
    }

    def childDependencies(moduleName: String): Seq[String] = {
      val moduleDependenciesContent = buildModuleDependenciesContent(moduleName)
      val thirdPartyDependenciesContent = buildThirdPartyDependenciesContent(moduleName)
      val allContent = moduleDependenciesContent ++ thirdPartyDependenciesContent
      if (allContent.isEmpty) comment("dependencies")
      else wrap("dependencies", allContent)
    }

    def buildModuleDependenciesContent(moduleName: String): Seq[String] = {
      val moduleNames = moduleMap(moduleName).filter(name => !dependencyMap.contains(name)).toSeq
      moduleNames.flatMap(buildModuleDependency)
    }

    def buildModuleDependency(name: String): Seq[String] = {
      val content = fullGroup() ++
        moduleArtifact(name) ++
        versionElement("${project.version}")
      wrap("dependency", content)
    }

    def buildThirdPartyDependency(name: String): Seq[String] = {
      val content = groupElement(dependencyMap(name).group) ++
        artifactElement(dependencyMap(name).artifact)
      wrap("dependency", content)
    }

    def buildThirdPartyDependenciesContent(moduleName: String): Seq[String] = {
      val thirdPartyDependencyNames = moduleMap(moduleName).filter(name => dependencyMap.contains(name))
      thirdPartyDependencyNames.flatMap(buildThirdPartyDependency).toSeq
    }

    def parent(): Seq[String] = comment("parent")

    def dependencyManagement(): Seq[String] = comment("dependencyManagement")

    def fullDependencyManagement(): Seq[String] = {
      val contents = dependenciesUsingFunction(dependencyMap, fullDependencyValue)
      wrap("dependencyManagement", contents)
    }

    def fullDependencyValue(name: String, dependency: Specification.Dependency): Seq[String] = {
      val version = chooseVersion(dependency)

      def dependencyContents =
        groupElement(dependency.group) ++
          artifactElement(dependency.artifact) ++
          versionElement(version) ++
          scope(dependency.scope)

      wrap("dependency", dependencyContents)
    }

    def chooseVersion(dependency: Specification.Dependency): String = {
      dependency.lockedAtVersion match {
        case Some(version) => version
        case None => repository.latestVersion(dependency.group, dependency.artifact)
      }
    }

    def partialDependencyValue(name: String, dependencyValue: Specification.Dependency): Seq[String] = {
      def dependencyContents =
        groupElement(dependencyValue.group) ++
          artifactElement(dependencyValue.artifact)

      wrap("dependency", dependencyContents)
    }

    def modules(): Seq[String] = comment("modules")

    def fullModules(): Seq[String] = {
      val moduleContents = moduleMap.flatMap((module _).tupled).toSeq
      wrap("modules", moduleContents)
    }

    def module(name: String, dependencyNames: Seq[String]): Seq[String] = {
      wrap("module", name)
    }

    def properties(): Seq[String] = comment("properties")

    def fullProperties(): Seq[String] = {
      wrap("properties", propertyUtf8())
    }

    def propertyUtf8(): Seq[String] = {
      wrap("project.build.sourceEncoding", "UTF-8")
    }

    def build(): Seq[String]

    def parentBuild(): Seq[String] = {
      val buildContents =
        sourceDir() ++
          testDir() ++
          plugins() ++
          pluginManagement()
      wrap("build", buildContents)
    }

    def childBuild(): Seq[String] = {
      val buildContents =
        plugins() ++
          pluginManagement()
      wrapUnlessEmpty("build", buildContents)
    }

    def sourceDir(): Seq[String] = {
      wrap("sourceDirectory", "${project.basedir}/src/main/" + language)
    }

    def testDir(): Seq[String] = {
      wrap("testSourceDirectory", "${project.basedir}/src/test/" + language)
    }

    def plugins(): Seq[String]

    def childPlugins(): Seq[String] = {
      val mavenTestPlugin =
        if (language == "scala") scalaTestMavenPlugin()
        else if (language == "kotlin") Seq()
        else if (language == "java") Seq()
        else throw new RuntimeException(s"Unsupported language $language")

      val pluginContents = mavenTestPlugin ++ detanglerPlugin() ++ executableJarPlugin() ++ maybeMavenPluginPlugin()
      wrapUnlessEmpty("plugins", pluginContents)
    }

    def parentPlugins(): Seq[String] = {
      val languagePlugin =
        if (language == "scala") scalaMavenPlugin()
        else if (language == "kotlin") kotlinMavenPlugin()
        else if(language == "java") Seq()
        else throw new RuntimeException(s"Unsupported language $language")
      val surefirePlugin =
        if (language == "scala") disableSurefirePlugin()
        else if (language == "kotlin") Seq()
        else if (language == "java") Seq()
        else throw new RuntimeException(s"Unsupported language $language")
      val pluginContents =
        maybeJavaVersionPlugin() ++
          languagePlugin ++
          mavenSourcePlugin() ++
          surefirePlugin
      wrap("plugins", pluginContents)
    }

    def maybeJavaVersionPlugin(): Seq[String] = {
      maybeJavaVersion match {
        case Some(javaVersion) => javaVersionPlugin(javaVersion)
        case None => Seq()
      }
    }

    def javaVersionPlugin(javaVersion: String): Seq[String] = {
      val group = "org.apache.maven.plugins"
      val artifact = "maven-compiler-plugin"
      val version = repository.latestVersion(group, artifact)
      val configurationContents =
        wrap("source", javaVersion) ++
          wrap("target", javaVersion)
      val pluginContents =
        wrap("groupId", group) ++
          wrap("artifactId", artifact) ++
          wrap("version", version) ++
          wrap("configuration", configurationContents)
      wrap("plugin", pluginContents)
    }

    def pluginManagement(): Seq[String] = Seq()

    def fullPluginManagement(): Seq[String] = {
      val testPlugin =
        if (language == "scala") Seq(indent("<!-- enable scalatest -->")) ++ scalaTestMavenPlugin()
        else if (language == "kotlin") Seq()
        else if (language == "java") Seq()
        else throw new RuntimeException(s"Unsupported language $language")
      val pluginManagementContents = wrapUnlessEmpty("plugins", testPlugin)
      wrapUnlessEmpty("pluginManagement", pluginManagementContents)
    }

    def scalaTestMavenPluginInner(): Seq[String]

    def parentScalaTestMavenPluginInner(): Seq[String] = {
      val configurationContent =
        wrap("reportsDirectory", "${project.build.directory}/surefire-reports") ++
          wrap("junitxml", ".") ++
          wrap("filereports", "WDF TestSuite.txt")
      val goalsContent =
        wrap("goal", "test")
      val executionContent =
        wrap("id", "test") ++
          wrap("goals", goalsContent)
      val executionsContent =
        wrap("execution", executionContent)
      val group = "org.scalatest"
      val artifact = "scalatest-maven-plugin"
      val version = repository.latestVersion(group, artifact)
      wrap("groupId", group) ++
        wrap("artifactId", artifact) ++
        wrap("version", version) ++
        wrap("configuration", configurationContent) ++
        wrap("executions", executionsContent)
    }

    def childScalaTestMavenPluginInner(): Seq[String] = {
      wrap("groupId", "org.scalatest") ++
        wrap("artifactId", "scalatest-maven-plugin")
    }

    def scalaTestMavenPlugin(): Seq[String] = {
      wrap("plugin", scalaTestMavenPluginInner())
    }

    def scalaMavenPlugin(): Seq[String] = {
      wrap("plugin", scalaMavenPluginInner())
    }

    def scalaMavenPluginInner(): Seq[String] = {
      val group = "net.alchim31.maven"
      val artifact = "scala-maven-plugin"
      val version = repository.latestVersion(group, artifact)
      wrap("groupId", group) ++
        wrap("artifactId", artifact) ++
        wrap("version", version) ++
        wrap("executions", scalaMavenPluginExecutions()) ++
        wrap("configuration", scalaMavenPluginConfiguration())
    }

    def kotlinMavenPlugin(): Seq[String] = {
      wrap("plugin", kotlinMavenPluginInner())
    }

    def kotlinMavenPluginInner(): Seq[String] = {
      val group = "org.jetbrains.kotlin"
      val artifact = "kotlin-maven-plugin"
      val version = repository.latestVersion(group, artifact)
      val javaVersion = maybeJavaVersion.getOrElse("1.8")
      val kotlinMavenConfiguration = wrap("jvmTarget", javaVersion)
      wrap("groupId", group) ++
        wrap("artifactId", artifact) ++
        wrap("version", version) ++
        wrap("executions", kotlinMavenPluginExecutions()) ++
        wrap("configuration", kotlinMavenConfiguration)
    }

    def scalaMavenPluginExecutions(): Seq[String] = {
      val goalsContent =
        wrap("goal", "compile") ++
          wrap("goal", "testCompile")
      val executionContent = wrap("goals", goalsContent)
      wrap("execution", executionContent)
    }

    def kotlinMavenPluginExecutions(): Seq[String] = {
      kotlinPluginCompileExecution() ++ kotlinPluginTestExecution()
    }

    def kotlinPluginCompileExecution(): Seq[String] = {
      val goalsContent =
        wrap("goal", "compile")
      val executionContent =
        wrap("id", "compile") ++
          wrap("goals", goalsContent)
      wrap("execution", executionContent)
    }

    def kotlinPluginTestExecution(): Seq[String] = {
      val goalsContent =
        wrap("goal", "test-compile")
      val executionContent =
        wrap("id", "test-compile") ++
          wrap("goals", goalsContent)
      wrap("execution", executionContent)
    }

    def scalaMavenPluginConfiguration(): Seq[String] = {
      val jvmArgsContent =
        wrap("jvmArg", "-Xms64m") ++
          wrap("jvmArg", "-Xmx1024m")
      val argsContent =
        wrap("arg", "-unchecked") ++
          wrap("arg", "-deprecation") ++
          wrap("arg", "-feature")
      val configurationContent =
        wrap("sourceDir", "src/main/java") ++
          wrap("jvmArgs", jvmArgsContent) ++
          wrap("args", argsContent)
      configurationContent
    }

    def mavenSourcePlugin(): Seq[String] = {
      wrap("plugin", mavenSourcePluginInner())
    }

    def mavenSourcePluginInner(): Seq[String] = {
      val group = "org.apache.maven.plugins"
      val artifact = "maven-source-plugin"
      val version = repository.latestVersion(group, artifact)
      wrap("artifactId", artifact) ++
        wrap("version", version) ++
        wrap("executions", mavenSourcePluginExecution())
    }

    def mavenSourcePluginExecution(): Seq[String] = {
      val goalsContent =
        wrap("goal", "jar-no-fork") ++
          wrap("goal", "test-jar-no-fork")
      val executionContent =
        wrap("id", "attach-sources") ++
          wrap("phase", "verify") ++
          wrap("goals", goalsContent)
      wrap("execution", executionContent)
    }

    def disableSurefirePlugin(): Seq[String] = {
      Seq(indent("<!-- disable surefire -->")) ++
        wrap("plugin", disableSurefirePluginInner())
    }

    def disableSurefirePluginInner(): Seq[String] = {
      val configurationContent = wrap("skipTests", "true")
      val group = "org.apache.maven.plugins"
      val artifact = "maven-surefire-plugin"
      val version = repository.latestVersion(group, artifact)
      wrap("artifactId", artifact) ++
        wrap("version", version) ++
        wrap("configuration", configurationContent)
    }

    def reporting(): Seq[String] = comment("reporting")

    def generateName(): Seq[String] = {
      wrap("name", "${project.groupId}:${project.artifactId}")
    }

    def generateDescription(): Seq[String] = comment("description")

    def url(): Seq[String] = comment("url")

    def inceptionYear(): Seq[String] = comment("inceptionYear")

    def licenses(): Seq[String] = comment("licenses")

    def organization(): Seq[String] = comment("organization")

    def developers(): Seq[String] = comment("developers")

    def contributors(): Seq[String] = comment("contributors")

    def issueManagement(): Seq[String] = comment("issueManagement")

    def ciManagement(): Seq[String] = comment("ciManagement")

    def mailingLists(): Seq[String] = comment("mailingLists")

    def scm(): Seq[String] = comment("scm")

    def prerequisites(): Seq[String] = comment("prerequisites")

    def repositories(): Seq[String] = comment("repositories")

    def pluginRepositories(): Seq[String] = comment("pluginRepositories")

    def distributionManagement(): Seq[String] = comment("distributionManagement")

    def profiles(): Seq[String] = comment("profiles")

    def stagingProfile(): Seq[String] = {
      val pluginsContents =
        wrap("plugin", mavenGpgPlugin()) ++
          wrap("plugin", mavenJavadocPlugin())
      val buildContents =
        wrap("plugins", pluginsContents)
      val profileContents =
        wrap("id", "stage") ++
          wrap("build", buildContents)
      wrap("profile", profileContents)
    }

    def mavenGpgPlugin(): Seq[String] = {
      val goalsContent =
        wrap("goal", "sign")
      val executionContent =
        wrap("id", "sign-artifacts") ++
          wrap("phase", "verify") ++
          wrap("goals", goalsContent)
      val executionsContent =
        wrap("execution", executionContent)
      val group = "org.apache.maven.plugins"
      val artifact = "maven-gpg-plugin"
      val version = repository.latestVersion(group, artifact)
      wrap("artifactId", artifact) ++
        wrap("version", version) ++
        wrap("executions", executionsContent)
    }

    def mavenJavadocPlugin(): Seq[String] = {
      val goalsContent =
        wrap("goal", "jar")
      val executionContent =
        wrap("id", "generate-dummy-javadoc-per-maven-central-requirements") ++
          wrap("phase", "package") ++
          wrap("goals", goalsContent)
      val executionsContent =
        wrap("execution", executionContent)
      val group = "org.apache.maven.plugins"
      val artifact = "maven-javadoc-plugin"
      val version = repository.latestVersion(group, artifact)
      wrap("artifactId", artifact) ++
        wrap("version", version) ++
        wrap("executions", executionsContent)
    }

    def detanglerPluginContents(): Seq[String] = {
      val configFile = wrap("detanglerConfig", s"$${basedir}/detangler.txt")
      val goalsContents = wrap("goal", "report")
      val executionContents = wrap("phase", "verify") ++ wrap("goals", goalsContents)
      val executionsContents = wrap("execution", executionContents)
      val executions = wrap("executions", executionsContents)
      val configuration = wrap("configuration", configFile)
      val group = "com.seanshubin.detangler"
      val artifact = "detangler-maven-plugin"
      val version = repository.latestVersion(group, artifact)
      groupElement(group) ++
        artifactElement(artifact) ++
        versionElement(version) ++
        executions ++
        configuration
    }

    def shouldHaveDetanglerPlugin(): Boolean = false

    def detanglerPlugin(): Seq[String] = {
      if (shouldHaveDetanglerPlugin()) {
        wrap("plugin", detanglerPluginContents())
      } else {
        Seq()
      }
    }

    def executableJarPlugin(finalName: String, mainClass: String): Seq[String] = {
      val manifest = wrap("mainClass", mainClass)
      val descriptorRefs = wrap("descriptorRef", "jar-with-dependencies")
      val archive = wrap("manifest", manifest)
      val configuration =
        wrap("finalName", finalName) ++
          wrap("appendAssemblyId", "false") ++
          wrap("descriptorRefs", descriptorRefs) ++
          wrap("archive", archive)
      val goals = wrap("goal", "single")
      val execution =
        wrap("id", "make-assembly") ++
          wrap("phase", "package") ++
          wrap("goals", goals)
      val executions = wrap("execution", execution)
      val group = "org.apache.maven.plugins"
      val artifact = "maven-assembly-plugin"
      val version = repository.latestVersion(group, artifact)
      val contents =
        wrap("artifactId", artifact) ++
          wrap("version", version) ++
          wrap("configuration", configuration) ++
          wrap("executions", executions)

      wrap("plugin", contents)
    }

    def shouldHaveMavenPluginPlugin(): Boolean = false

    def mavenPluginPlugin(): Seq[String] = {
      val defaultDescriptorContents =
        wrap("id", "default-descriptor") ++
          wrap(Seq("goals", "goal"), "descriptor") ++
          wrap("phase", "process-classes")
      val helpDescriptorContents =
        wrap("id", "help-descriptor") ++
          wrap(Seq("goals", "goal"), "helpmojo") ++
          wrap("phase", "process-classes")
      val defaultDescriptor = wrap("execution", defaultDescriptorContents)
      val helpDescriptor = wrap("execution", helpDescriptorContents)
      val executionsContents = defaultDescriptor ++ helpDescriptor
      val group = "org.apache.maven.plugins"
      val artifact = "maven-plugin-plugin"
      val version = repository.latestVersion(group, artifact)
      val configurationContents = wrap("goalPrefix", "plugin")
      val contents =
        wrap("artifactId", artifact) ++
          wrap("version", version) ++
          wrap("configuration", configurationContents) ++
          wrap("executions", executionsContents)
      wrap("plugin", contents)
    }

    def maybeMavenPluginPlugin(): Seq[String] = if (shouldHaveMavenPluginPlugin()) {
      mavenPluginPlugin()
    } else {
      Seq()
    }

    def executableJarPlugin(): Seq[String] = Seq()

    def comment(elementName: String): Seq[String] = {
      //      Seq(s"<!--<$elementName>...</$elementName>-->").map(indent)
      Seq()
    }

    def wrap(elementName: String, contents: String): Seq[String] = {
      Seq(indent(s"<$elementName>$contents</$elementName>"))
    }

    def wrap(elementName: String, contents: Seq[String]): Seq[String] = {
      Seq(indent(s"<$elementName>")) ++
        contents.map(indent(_: String)) ++
        Seq(indent(s"</$elementName>"))
    }

    def wrapUnlessEmpty(elementName: String, contents: Seq[String]): Seq[String] = {
      if (contents.isEmpty)
        Seq()
      else
        Seq(indent(s"<$elementName>")) ++
          contents.map(indent(_: String)) ++
          Seq(indent(s"</$elementName>"))
    }

    def wrap(elementNames: Seq[String], contents: Seq[String]): Seq[String] = {
      (elementNames, contents) match {
        case (Seq(), _) => contents
        case (Seq(elementName), Seq(content)) => wrap(elementName, content)
        case (Seq(elementName), _) => wrap(elementName, contents)
        case (head :: tail, Seq(content)) => wrap(head, wrap(tail, contents))
      }
    }

    def wrap(elementNames: Seq[String], contents: String): Seq[String] = {
      wrap(elementNames, Seq(contents))
    }

    def indent(s: String): String = {
      "    " + s
    }
  }

  class ParentPomGenerator(prefix: Seq[String],
                           name: Seq[String],
                           description: String,
                           versionString: String,
                           languageDirName: String,
                           dependencyMap: Map[String, Specification.Dependency],
                           global: Seq[String],
                           moduleMap: Map[String, Seq[String]],
                           developer: Developer,
                           maybeJavaVersion: Option[String],
                           deployableToMavenCentral: Boolean) extends PomGenerator(
    prefix,
    name,
    description,
    versionString,
    languageDirName,
    dependencyMap,
    moduleMap,
    developer,
    maybeJavaVersion,
    deployableToMavenCentral) {
    override def generateArtifact(): Seq[String] = parentArtifact()

    override def dependencies(): Seq[String] = parentDependencies(global)

    override def dependencyManagement(): Seq[String] = fullDependencyManagement()

    override def modules(): Seq[String] = fullModules()

    override def generateGroup(): Seq[String] = fullGroup()

    override def generateVersion(): Seq[String] = fullVersion()

    override def packaging(): Seq[String] = pomPackaging()

    override def properties(): Seq[String] = fullProperties()

    override def build(): Seq[String] = parentBuild()

    override def plugins(): Seq[String] = parentPlugins()

    override def scalaTestMavenPluginInner(): Seq[String] = parentScalaTestMavenPluginInner()

    override def pluginManagement(): Seq[String] = fullPluginManagement()

    override def generateDescription(): Seq[String] = {
      wrap("description", description)
    }

    override def url(): Seq[String] = {
      wrap("url", s"https://github.com/${developer.githubName}/${name.mkString("-")}")
    }

    override def licenses(): Seq[String] = {
      val licenseContent =
        wrap("name", "Unlicense") ++
          wrap("url", "http://unlicense.org/")
      val licensesContent = wrap("license", licenseContent)
      wrap("licenses", licensesContent)
    }

    override def developers(): Seq[String] = {
      val developerContent =
        wrap("name", developer.name) ++
          wrap("organization", developer.organization) ++
          wrap("organizationUrl", developer.url)
      val developersContent = wrap("developer", developerContent)
      wrap("developers", developersContent)
    }

    override def scm(): Seq[String] = {
      val scmContent =
        wrap("connection", s"git@github.com:${developer.githubName}/${name.mkString("-")}.git") ++
          wrap("developerConnection", s"git@github.com:${developer.githubName}/${name.mkString("-")}.git") ++
          wrap("url", s"https://github.com/${developer.githubName}/${name.mkString("-")}")
      wrap("scm", scmContent)
    }

    override def distributionManagement(): Seq[String] = {
      val repositoryContent =
        wrap("id", "maven-staging") ++
          wrap("url", "https://oss.sonatype.org/service/local/staging/deploy/maven2")
      val distributionManagementContent = wrap("repository", repositoryContent)
      wrap("distributionManagement", distributionManagementContent)
    }

    override def profiles(): Seq[String] = {
      val profilesContent = stagingProfile()
      wrap("profiles", profilesContent)
    }
  }

  case class ModulePomGenerator(prefix: Seq[String],
                                name: Seq[String],
                                description: String,
                                versionString: String,
                                languageDirName: String,
                                dependencyMap: Map[String, Specification.Dependency],
                                moduleMap: Map[String, Seq[String]],
                                developer: Developer,
                                moduleName: String,
                                detangler: Seq[String],
                                entryPoint: Map[String, String],
                                mavenPlugin: Seq[String],
                                maybeJavaVersion: Option[String],
                                deployableToMavenCentral: Boolean) extends PomGenerator(
    prefix,
    name,
    description,
    versionString,
    languageDirName,
    dependencyMap,
    moduleMap,
    developer,
    maybeJavaVersion,
    deployableToMavenCentral) {
    override def generateArtifact(): Seq[String] = moduleArtifact(moduleName)

    override def parent(): Seq[String] = {
      val contents = fullGroup() ++ parentArtifact() ++ fullVersion()
      wrap("parent", contents)
    }

    override def dependencies(): Seq[String] = childDependencies(moduleName)

    override def build(): Seq[String] = childBuild()

    override def plugins(): Seq[String] = childPlugins()

    override def scalaTestMavenPluginInner(): Seq[String] = childScalaTestMavenPluginInner()

    override def shouldHaveDetanglerPlugin(): Boolean = detangler.contains(moduleName)

    override def executableJarPlugin(): Seq[String] = {
      entryPoint.get(moduleName) match {
        case Some(className) =>
          val finalName = (name :+ moduleName).mkString("-")
          executableJarPlugin(finalName, className)
        case None => Seq()
      }
    }

    override def shouldHaveMavenPluginPlugin(): Boolean = {
      mavenPlugin.contains(moduleName)
    }

    override def packaging(): Seq[String] = {
      if (shouldHaveMavenPluginPlugin()) {
        wrap("packaging", "maven-plugin")
      } else {
        super.packaging()
      }
    }
  }

}
