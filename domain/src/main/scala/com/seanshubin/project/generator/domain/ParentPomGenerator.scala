package com.seanshubin.project.generator.domain

case class ParentPomGenerator(prefix: Seq[String],
                              name: Seq[String],
                              description: String,
                              versionString: String,
                              dependencyMap: Map[String, Dependency],
                              modules: Seq[String],
                              developerName: String,
                              developerOrganization: String,
                              developerUrl: String) {
  def generate(): Seq[String] = {
    val depth = 0
    val lines = project(depth)
    lines
  }

  def project(projectDepth: Int): Seq[String] = {
    val depth = projectDepth + 1
    val projectContents =
      model(depth) ++
        group(depth) ++
        artifact(depth) ++
        version(depth) ++
        packaging(depth) ++
        dependencies(depth) ++
        dependencyManagement(depth) ++
        modules(depth) ++
        properties(depth) ++
        build(depth) ++
        name(depth) ++
        description(depth)
    wrap(depth, "project", projectContents)
  }

  def model(depth: Int): Seq[String] = {
    wrap(depth, "modelVersion", "4.0.0")
  }

  def group(depth: Int): Seq[String] = {
    val groupContents = (prefix ++ name).mkString(".")
    group(depth, groupContents)
  }

  def group(depth: Int, contents: String): Seq[String] = {
    wrap(depth, "groupId", contents)
  }

  def artifact(depth: Int): Seq[String] = {
    val artifactContents = (name ++ Seq("parent")).mkString("-")
    artifact(depth, artifactContents)
  }

  def artifact(depth: Int, contents: String): Seq[String] = {
    wrap(depth, "artifactId", contents)
  }

  def version(depth: Int): Seq[String] = {
    version(depth, versionString)
  }

  def version(depth: Int, contents: String): Seq[String] = {
    wrap(depth, "version", contents)
  }

  def packaging(depth: Int): Seq[String] = {
    wrap(depth, "packaging", "pom")
  }

  def dependencies(depth: Int): Seq[String] = {
    val scalaLang = Dependency("org.scala-lang", "scala-library", "2.12.4")
    val scalaTest = Dependency("org.scalatest", "scalatest_2.12", "3.0.4", Some("test"))
    val contents = Seq(scalaLang, scalaTest).flatMap(dependencyValue(depth + 1, _: Dependency))
    wrap(depth, "dependencies", contents)
  }

  def dependencyManagement(depth: Int): Seq[String] = {
    val contents = dependencyMap.keys.toSeq.sorted.flatMap(dependency(depth + 1, _: String))
    wrap(depth, "dependencyManagement", contents)
  }

  def dependency(depth: Int, name: String): Seq[String] = {
    val dependencyValue = dependencyMap(name)

    def dependencyContents =
      group(depth, dependencyValue.group) ++
        artifact(depth, dependencyValue.artifact) ++
        version(depth, dependencyValue.version)

    wrap(depth, "dependency", dependencyContents)
  }

  def dependencyValue(depth: Int, dependencyValue: Dependency): Seq[String] = {
    def dependencyContents =
      group(depth, dependencyValue.group) ++
        artifact(depth, dependencyValue.artifact) ++
        version(depth, dependencyValue.version)

    wrap(depth, "dependency", dependencyContents)
  }

  def modules(depth: Int): Seq[String] = {
    val moduleContents = modules.flatMap(module(depth + 1, _: String))
    wrap(depth, "modules", moduleContents)
  }

  def module(depth: Int, name: String): Seq[String] = {
    wrap(depth, "module", name)
  }

  def properties(depth: Int): Seq[String] = {
    wrap(depth, "properties", propertyUtf8(depth + 1))
  }

  def propertyUtf8(depth: Int): Seq[String] = {
    wrap(depth, "project.build.sourceEncoding", "UTF-8")
  }

  def build(depth: Int): Seq[String] = {
    val buildContents =
      plugins(depth + 1) ++
        pluginManagement(depth + 1)
    wrap(depth, "build", buildContents)
  }

  def plugins(outerDepth: Int): Seq[String] = {
    val depth = outerDepth + 1
    val pluginContents =
      scalaMavenPlugin(depth) ++
        mavenSourcePlugin(depth) ++
        disableSurefirePlugin(depth)
    wrap(depth, "plugins", pluginContents)
  }

  def pluginManagement(depth: Int): Seq[String] = {
    val pluginsContents = wrap(depth + 2, "plugin", scalaTestMavenPlugin(depth + 3))
    val pluginManagementContents = wrap(depth + 1, "plugins", pluginsContents)
    wrap(depth, "pluginManagement", pluginManagementContents)
  }

  def scalaTestMavenPlugin(depth: Int): Seq[String] = {
    val configurationContent =
      wrap(depth + 1, "reportsDirectory", "${project.build.directory}/surefire-reports") ++
        wrap(depth + 1, "junitxml", ".") ++
        wrap(depth + 1, "filereports", "WDF TestSuite.txt")
    val goalsContent =
      wrap(depth + 3, "goal", "test")
    val executionContent =
      wrap(depth + 2, "id", "test") ++
        wrap(depth + 2, "goals", goalsContent)
    val executionsContent =
      wrap(depth + 1, "execution", executionContent)
    wrap(depth, "groupId", "org.scalatest") ++
      wrap(depth, "artifactId", "scalatest-maven-plugin") ++
      wrap(depth, "version", "1.0") ++
      wrap(depth, "configuration", configurationContent) ++
      wrap(depth, "executions", executionsContent)
  }

  def scalaMavenPlugin(depth: Int): Seq[String] = {
    wrap(depth, "plugin", scalaMavenPluginInner(depth + 1))
  }

  def scalaMavenPluginInner(depth: Int): Seq[String] = {
    wrap(depth, "groupId", "net.alchim31.maven") ++
      wrap(depth, "artifactId", "scala-maven-plugin") ++
      wrap(depth, "version", "3.2.2") ++
      wrap(depth, "executions", scalaMavenPluginExecutions(depth + 1)) ++
      wrap(depth, "configuration", scalaMavenPluginConfiguration(depth + 1))
  }

  def scalaMavenPluginExecutions(depth: Int): Seq[String] = {
    val goalsContent =
      wrap(depth + 2, "goal", "compile") ++
        wrap(depth + 2, "goal", "testCompile")
    val executionContent = wrap(depth + 1, "goals", goalsContent)
    wrap(depth, "execution", executionContent)
  }

  def scalaMavenPluginConfiguration(depth: Int): Seq[String] = {
    val jvmArgsContent =
      wrap(depth + 2, "jvmArg", "-Xms64m") ++
        wrap(depth + 2, "jvmArg", "-Xmx1024m")
    val argsContent =
      wrap(depth + 2, "arg", "-unchecked") ++
        wrap(depth + 2, "arg", "-deprecation") ++
        wrap(depth + 2, "arg", "-feature")
    val configurationContent =
      wrap(depth + 1, "sourceDir", "src/main/java") ++
        wrap(depth + 1, "jvmArgs", jvmArgsContent) ++
        wrap(depth + 1, "args", argsContent)
    wrap(depth, "configuration", configurationContent)
  }

  def mavenSourcePlugin(depth: Int): Seq[String] = {
    wrap(depth, "plugin", mavenSourcePluginInner(depth + 1))
  }

  def mavenSourcePluginInner(depth: Int): Seq[String] = {
    wrap(depth, "groupId", "org.apache.maven.plugins") ++
      wrap(depth, "artifactId", "maven-source-plugin") ++
      wrap(depth, "version", "3.0.1") ++
      wrap(depth, "executions", mavenSourcePluginExecution(depth + 1))
  }

  def mavenSourcePluginExecution(depth: Int): Seq[String] = {
    val goalsContent =
      wrap(depth + 2, "goal", "jar-no-fork") ++
        wrap(depth + 2, "goal", "test-jar-no-fork")
    val executionContent =
      wrap(depth + 1, "id", "attach-sources") ++
        wrap(depth + 1, "phase", "verify") ++
        wrap(depth + 1, "goals", goalsContent)
    wrap(depth, "execution", executionContent)
  }

  def disableSurefirePlugin(depth: Int): Seq[String] = {
    wrap(depth, "plugin", disableSurefirePluginInner(depth + 1))
  }

  def disableSurefirePluginInner(depth: Int): Seq[String] = {
    val configurationContent =
      Seq("<!--disable surefire-->") ++
        wrap(depth + 1, "skipTests", "true")
    wrap(depth, "groupId", "org.apache.maven.plugins") ++
      wrap(depth, "artifactId", "maven-surefire-plugin") ++
      wrap(depth, "version", "2.20") ++
      wrap(depth, "configuration", configurationContent)
  }

  def name(depth: Int): Seq[String] = {
    wrap(depth, "name", "${project.groupId}:${project.artifactId}")
  }

  def description(depth: Int): Seq[String] = {
    wrap(depth, "description", description)
  }

  def wrap(depth: Int, elementName: String, contents: String): Seq[String] = {
    Seq(s"<$elementName>$contents</$elementName>")
  }

  def wrap(depth: Int, elementName: String, contents: Seq[String]): Seq[String] = {
    Seq(s"<$elementName>") ++
      contents.map(indent(depth + 1, _: String)) ++
      Seq(s"</$elementName>")
  }

  def indent(depth: Int, s: String): String = {
    " " * 2 + s
  }
}

object ParentPomGenerator extends App {
  val scalaLang = Dependency("org.scala-lang", "scala-library", "2.12.14")
  val scalaTest = Dependency("org.scalatest", "scalatest_2.12", "3.0.4", Some("test"))

  val prefix: Seq[String] = Seq("com", "seanshubin")
  val name: Seq[String] = Seq("devon")
  val description: String = "A simple, language neutral notation for representing structured values"
  val versionString: String = "1.1.1"
  val dependencyMap: Map[String, Dependency] = Map(
    "scala-lang" -> Dependency("org.scala-lang", "scala-library", "2.12.14"),
    "scala-reflect" -> Dependency("org.scala-lang", "scala-reflect", "2.12.3"),
    "scala-test" -> Dependency("org.scalatest", "scalatest_2.12", "3.0.4", Some("test")),
  )
  val modules: Seq[String] = Seq(
    "domain",
    "tokenizer",
    "rules",
    "parser",
    "reflection",
    "string")
  val versionControlPrefix: String = "https://github.com/SeanShubin/"

  val developerName = "Sean Shubin"
  val developerOrganization = "Sean Shubin"
  val developerUrl = "http://seanshubin.com/"

  val generator = new ParentPomGenerator(
    prefix,
    name,
    description,
    versionString,
    dependencyMap,
    modules,
    developerName,
    developerOrganization,
    developerUrl,
  )
  val lines = generator.generate()
  lines.foreach(println)
}

/*
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0                       http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <url>https://github.com/SeanShubin/developers-value-notation</url>
    <licenses>
        <license>
            <name>Unlicense</name>
            <url>http://unlicense.org/</url>
        </license>
    </licenses>
    <developers>
        <developer>
            <name>Sean Shubin</name>
            <organization>Sean Shubin</organization>
            <organizationUrl>http://seanshubin.com/</organizationUrl>
        </developer>
    </developers>
    <scm>
                 <connection>scm:git:git@github.com:SeanShubin/developers-value-notation.git</connection>
        <developerConnection>scm:git:git@github.com:SeanShubin/developers-value-notation.git</developerConnection>
        <url>https://github.com/SeanShubin/developers-value-notation</url>
    </scm>
    <distributionManagement>
        <repository>
            <id>maven-staging</id>
            <url>https://oss.sonatype.org/service/local/staging/deploy/maven2</url>
        </repository>
    </distributionManagement>
    <profiles>
        <profile>
            <id>stage</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-gpg-plugin</artifactId>
                        <version>1.6</version>
                        <executions>
                            <execution>
                                <id>sign-artifacts</id>
                                <phase>verify</phase>
                                <goals>
                                    <goal>sign</goal>
                                </goals>
                            </execution>
                        </executions>
                    </plugin>
                    <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-javadoc-plugin</artifactId>
                        <version>2.10.4</version>
                        <executions>
                            <execution>
                                <id>generate-dummy-javadoc-per-maven-central-requirements</id>
                                <phase>package</phase>
                                <goals>
                                    <goal>jar</goal>
                                </goals>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>
</project>
 */
