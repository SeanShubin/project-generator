package com.seanshubin.project.generator.domain

class PomGeneratorImpl(newline: String) extends PomGenerator {
  override def generateParent(project: Specification.Project): Seq[String] = {
    val pomGenerator = new ParentPomGenerator(
      project.prefix,
      project.name,
      project.description,
      project.version,
      project.dependencies,
      project.modules,
      project.developer.githubName,
      project.developer.name,
      project.developer.organization,
      project.developer.url
    )
    pomGenerator.generate()
  }

  override def generateModule(project: Specification.Project, moduleName: String): Seq[String] = {
    val pomGenerator = ModulePomGenerator(
      project.prefix,
      project.name,
      project.description,
      project.version,
      project.dependencies,
      project.modules,
      project.developer.githubName,
      project.developer.name,
      project.developer.organization,
      project.developer.url,
      moduleName,
      project.detangler,
      project.consoleEntryPoint,
      project.mavenPlugin
    )
    pomGenerator.generate()
  }

  abstract class PomGenerator(prefix: Seq[String],
                              name: Seq[String],
                              description: String,
                              versionString: String,
                              dependencyMap: Map[String, Specification.Dependency],
                              moduleMap: Map[String, Seq[String]],
                              githubName: String,
                              developerName: String,
                              developerOrganization: String,
                              developerUrl: String) {
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
      model() ++
        group() ++
        artifact() ++
        version() ++
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
    }

    def model(): Seq[String] = {
      wrap("modelVersion", "4.0.0")
    }

    def group(): Seq[String] = comment("groupId")

    def fullGroup(): Seq[String] = {
      val groupContents = (prefix ++ name).mkString(".")
      group(groupContents)
    }

    def group(contents: String): Seq[String] = {
      wrap("groupId", contents)
    }

    def parentArtifact(): Seq[String] = {
      val artifactContents = (name ++ Seq("parent")).mkString("-")
      artifact(artifactContents)
    }

    def moduleArtifact(moduleName: String): Seq[String] = {
      val artifactContents = (name ++ Seq(moduleName)).mkString("-")
      artifact(artifactContents)
    }

    def artifact(): Seq[String]

    def artifact(contents: String): Seq[String] = {
      wrap("artifactId", contents)
    }

    def version(): Seq[String] = comment("version")

    def fullVersion(): Seq[String] = {
      version(versionString)
    }

    def version(contents: String): Seq[String] = {
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

    def parentDependencies(): Seq[String] = {
      val dependencyNamesToInclude = Seq("scala-library", "scala-test")
      val dependenciesInParent = dependencyMap.filterKeys(dependencyNamesToInclude.contains)
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
        version("${project.version}")
      wrap("dependency", content)
    }

    def buildThirdPartyDependency(name: String): Seq[String] = {
      val content = group(dependencyMap(name).group) ++
        artifact(dependencyMap(name).artifact)
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

    def fullDependencyValue(name: String, dependencyValue: Specification.Dependency): Seq[String] = {
      def dependencyContents =
        group(dependencyValue.group) ++
          artifact(dependencyValue.artifact) ++
          version(dependencyValue.version) ++
          scope(dependencyValue.scope)

      wrap("dependency", dependencyContents)
    }

    def partialDependencyValue(name: String, dependencyValue: Specification.Dependency): Seq[String] = {
      def dependencyContents =
        group(dependencyValue.group) ++
          artifact(dependencyValue.artifact)

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
        plugins() ++
          pluginManagement()
      wrap("build", buildContents)
    }

    def childBuild(): Seq[String] = {
      val buildContents =
        sourceDir() ++
          testDir() ++
          plugins() ++
          pluginManagement()
      wrap("build", buildContents)
    }

    def sourceDir(): Seq[String] = {
      wrap("sourceDirectory", "src/main/scala")
    }

    def testDir(): Seq[String] = {
      wrap("testSourceDirectory", "src/test/scala")
    }

    def plugins(): Seq[String]

    def childPlugins(): Seq[String] = {
      val pluginContents = scalaTestMavenPlugin() ++ detanglerPlugin() ++ executableJarPlugin() ++ maybeMavenPluginPlugin()
      wrap("plugins", pluginContents)
    }

    def parentPlugins(): Seq[String] = {
      val pluginContents =
        scalaMavenPlugin() ++
          mavenSourcePlugin() ++
          disableSurefirePlugin()
      wrap("plugins", pluginContents)
    }

    def pluginManagement(): Seq[String] = Seq()

    def fullPluginManagement(): Seq[String] = {
      val pluginManagementContents = wrap("plugins",
        Seq(indent("<!-- enable scalatest -->")) ++ scalaTestMavenPlugin())
      wrap("pluginManagement", pluginManagementContents)
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
      wrap("groupId", "org.scalatest") ++
        wrap("artifactId", "scalatest-maven-plugin") ++
        wrap("version", "1.0") ++
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
      wrap("groupId", "net.alchim31.maven") ++
        wrap("artifactId", "scala-maven-plugin") ++
        wrap("version", "3.3.1") ++
        wrap("executions", scalaMavenPluginExecutions()) ++
        wrap("configuration", scalaMavenPluginConfiguration())
    }

    def scalaMavenPluginExecutions(): Seq[String] = {
      val goalsContent =
        wrap("goal", "compile") ++
          wrap("goal", "testCompile")
      val executionContent = wrap("goals", goalsContent)
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
      wrap("groupId", "org.apache.maven.plugins") ++
        wrap("artifactId", "maven-source-plugin") ++
        wrap("version", "3.0.1") ++
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
      wrap("groupId", "org.apache.maven.plugins") ++
        wrap("artifactId", "maven-surefire-plugin") ++
        wrap("version", "2.20.1") ++
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
      wrap("groupId", "org.apache.maven.plugins") ++
        wrap("artifactId", "maven-gpg-plugin") ++
        wrap("version", "1.6") ++
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
      wrap("groupId", "org.apache.maven.plugins") ++
        wrap("artifactId", "maven-javadoc-plugin") ++
        wrap("version", "2.10.4") ++
        wrap("executions", executionsContent)
    }

    def detanglerPluginContents(): Seq[String] = {
      val configFile = wrap("detanglerConfig", "detangler.txt")
      val goalsContents = wrap("goal", "report")
      val executionContents = wrap("phase", "verify") ++ wrap("goals", goalsContents)
      val executionsContents = wrap("execution", executionContents)
      val executions = wrap("executions", executionsContents)
      val configuration = wrap("configuration", configFile)
      group("com.seanshubin.detangler") ++
        artifact("detangler-maven-plugin") ++
        version("0.9.1") ++
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
      val contents =
        wrap("artifactId", "maven-assembly-plugin") ++
          wrap("version", "3.1.0") ++
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
      val contents =
        wrap("groupId", "org.apache.maven.plugins") ++
          wrap("artifactId", "maven-plugin-plugin") ++
          wrap("version", "3.5") ++
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
      Seq(s"<!--<$elementName>...</$elementName>-->").map(indent)
    }

    def wrap(elementName: String, contents: String): Seq[String] = {
      Seq(indent(s"<$elementName>$contents</$elementName>"))
    }

    def wrap(elementName: String, contents: Seq[String]): Seq[String] = {
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
                           dependencyMap: Map[String, Specification.Dependency],
                           moduleMap: Map[String, Seq[String]],
                           githubName: String,
                           developerName: String,
                           developerOrganization: String,
                           developerUrl: String) extends PomGenerator(
    prefix,
    name,
    description,
    versionString,
    dependencyMap,
    moduleMap,
    githubName,
    developerName,
    developerOrganization,
    developerUrl) {
    override def artifact(): Seq[String] = parentArtifact()

    override def dependencies(): Seq[String] = parentDependencies()

    override def dependencyManagement(): Seq[String] = fullDependencyManagement()

    override def modules(): Seq[String] = fullModules()

    override def group(): Seq[String] = fullGroup()

    override def version(): Seq[String] = fullVersion()

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
      wrap("url", s"https://github.com/$githubName/${name.mkString("-")}")
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
        wrap("name", developerName) ++
          wrap("organization", developerOrganization) ++
          wrap("organizationUrl", developerUrl)
      val developersContent = wrap("developer", developerContent)
      wrap("developers", developersContent)
    }

    override def scm(): Seq[String] = {
      val scmContent =
        wrap("connection", s"git@github.com:$githubName/${name.mkString("-")}.git") ++
          wrap("developerConnection", s"git@github.com:$githubName/${name.mkString("-")}.git") ++
          wrap("url", s"https://github.com/$githubName/${name.mkString("-")}")
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
                                dependencyMap: Map[String, Specification.Dependency],
                                moduleMap: Map[String, Seq[String]],
                                githubName: String,
                                developerName: String,
                                developerOrganization: String,
                                developerUrl: String,
                                moduleName: String,
                                detangler: Seq[String],
                                entryPoint: Map[String, String],
                                mavenPlugin: Seq[String]) extends PomGenerator(
    prefix,
    name,
    description,
    versionString,
    dependencyMap,
    moduleMap,
    githubName,
    developerName,
    developerOrganization,
    developerUrl) {
    override def artifact(): Seq[String] = moduleArtifact(moduleName)

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
        case Some(className) => executableJarPlugin(name.mkString("-"), className)
        case None => Seq()
      }
    }

    override def shouldHaveMavenPluginPlugin(): Boolean = {
      mavenPlugin.contains(moduleName)
    }
  }

}
