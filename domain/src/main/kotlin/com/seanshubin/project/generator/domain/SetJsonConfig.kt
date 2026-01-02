package com.seanshubin.project.generator.domain

import java.nio.file.Path

data class SetJsonConfig(val path: Path, val value:Any, val keys: List<String>) : Command {
    override fun execute(environment: Environment) {
        val keyStore = environment.createKeyStore(path)
        keyStore.store(keys, value)
    }
}
