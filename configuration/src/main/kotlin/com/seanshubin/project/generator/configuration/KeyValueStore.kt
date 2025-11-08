package com.seanshubin.project.generator.configuration

interface KeyValueStore {
    fun load(key:List<Any>):Any?
    fun store(key:List<Any>, value:Any?)
    fun exists(key:List<Any>):Boolean
    fun arraySize(key: List<Any>): Int
    fun loadWithDefault(key:List<Any>, default:Any?):Any? {
        return if(exists(key)) {
            load(key)
        } else {
            store(key, default)
            default
        }
    }
}
