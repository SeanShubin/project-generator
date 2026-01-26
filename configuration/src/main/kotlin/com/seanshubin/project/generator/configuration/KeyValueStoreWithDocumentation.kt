package com.seanshubin.project.generator.configuration

interface KeyValueStoreWithDocumentation {
    fun load(key:List<String>, default:Any?, documentation:List<String>):Any?
}
