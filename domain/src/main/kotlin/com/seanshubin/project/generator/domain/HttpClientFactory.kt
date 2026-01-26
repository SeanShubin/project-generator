package com.seanshubin.project.generator.domain

import java.net.http.HttpClient

interface HttpClientFactory {
    fun createHttpClient(): HttpClient
}
