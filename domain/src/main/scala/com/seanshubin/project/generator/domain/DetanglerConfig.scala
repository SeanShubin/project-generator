package com.seanshubin.project.generator.domain

import java.nio.file.Path

object DetanglerConfig {

  case class Configuration(reportDir: Path,
                           searchPaths: Seq[Path],
                           level: Option[Int],
                           startsWith: StartsWithConfiguration,
                           ignoreFiles: Seq[Path],
                           canFailBuild: Option[Boolean],
                           allowedInCycle: Seq[Seq[String]])

  case class StartsWithConfiguration(include: Seq[Seq[String]],
                                     exclude: Seq[Seq[String]],
                                     drop: Seq[Seq[String]])

}
