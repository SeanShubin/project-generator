package com.seanshubin.project.generator.gradle

sealed class GradleNode {
    data class Block(val name: String, val content: List<GradleNode>) : GradleNode()
    data class Statement(val text: String) : GradleNode()
    data class Property(val name: String, val value: String) : GradleNode()
    data class PluginDsl(val id: String, val version: String? = null) : GradleNode()
    data class KotlinPluginDsl(val pluginName: String, val version: String) : GradleNode()
    data class BacktickPlugin(val pluginName: String) : GradleNode()
    data class DependencyDsl(val configuration: String, val notation: String) : GradleNode()
    data class MethodCall(val receiver: String, val method: String, val args: List<String> = emptyList()) : GradleNode()
    object EmptyLine : GradleNode()
}
