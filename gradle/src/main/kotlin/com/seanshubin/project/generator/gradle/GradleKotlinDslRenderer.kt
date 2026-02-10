package com.seanshubin.project.generator.gradle

class GradleKotlinDslRenderer {
    fun toLines(node: GradleNode, indent: Int = 0): List<String> {
        return when (node) {
            is GradleNode.Block -> {
                if (node.name == "__ROOT__") {
                    // Special case: root block doesn't add braces, just renders content
                    node.content.flatMap { toLines(it, indent) }
                } else {
                    val indentString = "    ".repeat(indent)
                    val header = "$indentString${node.name} {"
                    val content = node.content.flatMap { toLines(it, indent + 1) }
                    val footer = "$indentString}"
                    listOf(header) + content + listOf(footer)
                }
            }
            is GradleNode.Statement -> {
                val indentString = "    ".repeat(indent)
                listOf("$indentString${node.text}")
            }
            is GradleNode.Property -> {
                val indentString = "    ".repeat(indent)
                listOf("$indentString${node.name} = ${node.value}")
            }
            is GradleNode.PluginDsl -> {
                val indentString = "    ".repeat(indent)
                if (node.version != null) {
                    listOf("${indentString}id(\"${node.id}\") version \"${node.version}\"")
                } else {
                    listOf("${indentString}id(\"${node.id}\")")
                }
            }
            is GradleNode.KotlinPluginDsl -> {
                val indentString = "    ".repeat(indent)
                listOf("${indentString}kotlin(\"${node.pluginName}\") version \"${node.version}\"")
            }
            is GradleNode.BacktickPlugin -> {
                val indentString = "    ".repeat(indent)
                listOf("$indentString`${node.pluginName}`")
            }
            is GradleNode.DependencyDsl -> {
                val indentString = "    ".repeat(indent)
                listOf("$indentString${node.configuration}(${node.notation})")
            }
            is GradleNode.MethodCall -> {
                val indentString = "    ".repeat(indent)
                if (node.args.isEmpty()) {
                    listOf("$indentString${node.receiver}.${node.method}()")
                } else {
                    val argsString = node.args.joinToString(", ")
                    listOf("$indentString${node.receiver}.${node.method}($argsString)")
                }
            }
            GradleNode.EmptyLine -> listOf("")
        }
    }
}
