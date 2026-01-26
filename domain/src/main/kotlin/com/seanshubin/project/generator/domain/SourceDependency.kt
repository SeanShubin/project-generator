package com.seanshubin.project.generator.domain

import java.nio.file.Path

/**
 * Configuration for copying source code from an external project.
 *
 * This allows projects to depend on source code from other local projects rather than
 * just maven dependencies. The source code is copied and transformed to fit the target
 * project's package structure.
 *
 * Example configuration in project-specification.json:
 * ```json
 * {
 *   "sourceDependencies": {
 *     "sourceProjectPath": "/path/to/external/project",
 *     "moduleMapping": {
 *       "source-module-f": "target-module-j",
 *       "source-module-g": "target-module-k"
 *     }
 *   }
 * }
 * ```
 *
 * @property sourceProjectPath Absolute path to the external project's root directory
 * @property moduleMapping Map of source module names to target module names
 */
data class SourceDependency(
    val sourceProjectPath: Path,
    val moduleMapping: Map<String, String>
)
