package com.seanshubin.project.generator.gradle

import com.seanshubin.project.generator.core.GradlePluginSpec
import com.seanshubin.project.generator.core.Project

interface GradleFileNode {
    fun generateBuildGradle(project: Project, spec: GradlePluginSpec): GradleNode
    fun generateSettingsGradle(project: Project, spec: GradlePluginSpec): GradleNode
}
