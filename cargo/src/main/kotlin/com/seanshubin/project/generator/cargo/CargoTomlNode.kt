package com.seanshubin.project.generator.cargo

import com.seanshubin.project.generator.core.Project

interface CargoTomlNode {
    /** The workspace manifest at the project root. */
    fun generateWorkspaceToml(project: Project): TomlNode

    /** The manifest for one member crate. */
    fun generateCrateToml(project: Project, module: String, dependencies: List<String>): TomlNode
}
