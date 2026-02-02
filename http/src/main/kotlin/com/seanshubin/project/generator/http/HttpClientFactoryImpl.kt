package com.seanshubin.project.generator.http

import java.net.http.HttpClient

class HttpClientFactoryImpl : HttpClientFactory {
    override fun createHttpClient(): HttpClient {
        return HttpClient.newHttpClient()
    }
}
