package com.seanshubin.project.generator.http

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class HttpImpl(private val httpClient: HttpClient) : Http {
    override fun getAssertSuccess(uriString: String): String {
        val uri = URI.create(uriString)
        val request = HttpRequest.newBuilder().uri(uri).build()
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        val statusCode = response.statusCode()
        if (!isSuccess(statusCode)) {
            throw RuntimeException("Unexpected status code $statusCode")
        }
        val body = response.body()
        return body
    }

    private fun isSuccess(statusCode: Int): Boolean = statusCode in 200..299
}