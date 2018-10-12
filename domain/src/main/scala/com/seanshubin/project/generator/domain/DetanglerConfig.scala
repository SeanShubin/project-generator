package com.seanshubin.project.generator.domain

import java.nio.file.Path

object DetanglerConfig {
  case class Configuration(reportDir: Path,
                           searchPaths: Seq[Path],
                           level: Int,
                           startsWith: StartsWithConfiguration,
                           ignoreFiles: Seq[Path],
                           canFailBuild: Boolean,
                           ignoreJavadoc: Boolean,
                           logTiming: Boolean,
                           logEffectiveConfiguration: Boolean,
                           allowedInCycle: Path)

  case class StartsWithConfiguration(include: Seq[Seq[String]],
                                     exclude: Seq[Seq[String]],
                                     drop: Seq[Seq[String]])
}
