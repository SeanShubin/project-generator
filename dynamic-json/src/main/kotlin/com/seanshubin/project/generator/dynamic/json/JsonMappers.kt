package com.seanshubin.project.generator.dynamic.json

import com.fasterxml.jackson.core.StreamReadFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue

object JsonMappers {
    private val kotlinModule = KotlinModule.Builder().build()
    val pretty: ObjectMapper = JsonMapper.builder()
        .enable(SerializationFeature.INDENT_OUTPUT)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
        .build()
        .registerModule(kotlinModule)
        .registerModule(JavaTimeModule())
    val compact: ObjectMapper = JsonMapper.builder()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
        .build()
        .registerModule(kotlinModule)
        .registerModule(JavaTimeModule())
    val parser: ObjectMapper = compact
    inline fun <reified T> parse(json: String): T = parser.readValue(json)

    fun Any?.toJson(): String = pretty.writeValueAsString(this)
    fun String.normalizeJson(): String {
        val asObject = parser.readValue<Any?>(this)
        val asNormalized = pretty.writeValueAsString(asObject)
        return asNormalized
    }
}
