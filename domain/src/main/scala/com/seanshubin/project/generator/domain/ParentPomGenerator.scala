package com.seanshubin.project.generator.domain

case class ParentPomGenerator(prefix: Seq[String],
                              name: Seq[String],
                              versionString: String,
                              dependencyMap: Map[String, Dependency],
                              modules: Seq[String]) {
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
        properties(depth)
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

  def modules(depth:Int):Seq[String] = {
    val moduleContents = modules.flatMap(module(depth+1, _:String))
    wrap(depth, "modules", moduleContents)
  }

  def module(depth:Int, name:String):Seq[String] = {
    wrap(depth, "module", name)
  }

  def properties(depth:Int):Seq[String] = {
    wrap(depth, "properties", propertyUtf8(depth+1))
  }

  def propertyUtf8(depth:Int):Seq[String] = {
    wrap(depth, "project.build.sourceEncoding", "UTF-8")
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

  val generator = new ParentPomGenerator(prefix, name, versionString, dependencyMap, modules)
  val lines = generator.generate()
  lines.foreach(println)
}

/*
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0                       http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- The Basics -->
    <groupId>com.seanshubin.devon</groupId>
    <artifactId>devon-parent</artifactId>
    <version>1.1.1</version>
    <packaging>pom</packaging>
    <dependencies>
        <dependency>
            <groupId>org.scala-lang</groupId>
            <artifactId>scala-library</artifactId>
            <version>2.12.4</version>
        </dependency>
        <dependency>
            <groupId>org.scalatest</groupId>
            <artifactId>scalatest_2.12</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <!--<parent>...</parent>-->
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.scala-lang</groupId>
                <artifactId>scala-library</artifactId>
                <version>2.12.4</version>
            </dependency>
            <dependency>
                <groupId>org.scala-lang</groupId>
                <artifactId>scala-reflect</artifactId>
                <version>2.12.3</version>
            </dependency>
            <dependency>
                <groupId>org.scalatest</groupId>
                <artifactId>scalatest_2.12</artifactId>
                <version>3.0.4</version>
                <scope>test</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    <modules>
        <module>domain</module>
        <module>tokenizer</module>
        <module>rules</module>
        <module>parser</module>
        <module>reflection</module>
        <module>string</module>
    </modules>
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <!-- Build Settings -->
    <build>
        <plugins>
            <plugin>
                <groupId>net.alchim31.maven</groupId>
                <artifactId>scala-maven-plugin</artifactId>
                <version>3.2.2</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>testCompile</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <sourceDir>src/main/java</sourceDir>
                    <jvmArgs>
                        <jvmArg>-Xms64m</jvmArg>
                        <jvmArg>-Xmx1024m</jvmArg>
                    </jvmArgs>
                    <args>
                        <arg>-unchecked</arg>
                        <arg>-deprecation</arg>
                        <arg>-feature</arg>
                    </args>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-source-plugin</artifactId>
                <version>3.0.1</version>
                <executions>
                    <execution>
                        <id>attach-sources</id>
                        <phase>verify</phase>
                        <goals>
                            <goal>jar-no-fork</goal>
                            <goal>test-jar-no-fork</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <!-- disable surefire -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>2.20</version>
                <configuration>
                    <skipTests>true</skipTests>
                </configuration>
            </plugin>
        </plugins>
        <pluginManagement>
            <plugins>
                <!-- enable scalatest -->
                <plugin>
                    <groupId>org.scalatest</groupId>
                    <artifactId>scalatest-maven-plugin</artifactId>
                    <version>1.0</version>
                    <configuration>
                        <reportsDirectory>${project.build.directory}/surefire-reports</reportsDirectory>
                        <junitxml>.</junitxml>
                        <filereports>WDF TestSuite.txt</filereports>
                    </configuration>
                    <executions>
                        <execution>
                            <id>test</id>
                            <goals>
                                <goal>test</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
    <!--<reporting>...</reporting>-->

    <!-- More Project Information -->
    <name>${project.groupId}:${project.artifactId}</name>
    <description>A simple, language neutral notation for representing structured values</description>
    <url>https://github.com/SeanShubin/developers-value-notation</url>
    <!--<inceptionYear>...</inceptionYear>-->
    <licenses>
        <license>
            <name>Unlicense</name>
            <url>http://unlicense.org/</url>
        </license>
    </licenses>
    <!--<organization>...</organization>-->
    <developers>
        <developer>
            <name>Sean Shubin</name>
            <organization>Sean Shubin</organization>
            <organizationUrl>http://seanshubin.com/</organizationUrl>
        </developer>
    </developers>
    <!--<contributors>...</contributors>-->

    <!-- Environment Settings -->
    <!--<issueManagement>...</issueManagement>-->
    <!--<ciManagement>...</ciManagement>-->
    <!--<mailingLists>...</mailingLists>-->
    <scm>
        <connection>scm:git:git@github.com:SeanShubin/developers-value-notation.git</connection>
        <developerConnection>scm:git:git@github.com:SeanShubin/developers-value-notation.git</developerConnection>
        <url>https://github.com/SeanShubin/developers-value-notation</url>
    </scm>
    <!--<prerequisites>...</prerequisites>-->
    <!--<repositories>...</repositories>-->
    <!--<pluginRepositories>...</pluginRepositories>-->
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
