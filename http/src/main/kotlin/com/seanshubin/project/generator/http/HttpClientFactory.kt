package com.seanshubin.project.generator.http

import java.net.http.HttpClient

interface HttpClientFactory {
    fun createHttpClient(): HttpClient
}
