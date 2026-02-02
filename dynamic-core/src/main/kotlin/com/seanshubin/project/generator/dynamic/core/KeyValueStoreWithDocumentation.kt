package com.seanshubin.project.generator.dynamic.core

//
// This file was imported from: ../kotlin-reusable
// Module: dynamic-core
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

interface KeyValueStoreWithDocumentation {
    fun load(key: List<String>, default: Any?, documentation: List<String>): Any?
}
