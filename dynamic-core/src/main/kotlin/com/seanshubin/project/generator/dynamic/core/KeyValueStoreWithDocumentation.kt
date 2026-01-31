package com.seanshubin.project.generator.dynamic.core

interface KeyValueStoreWithDocumentation {
    fun load(key:List<String>, default:Any?, documentation:List<String>):Any?
}
