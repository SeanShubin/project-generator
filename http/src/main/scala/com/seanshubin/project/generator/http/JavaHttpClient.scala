package com.seanshubin.project.generator.http
import java.io.InputStream
import java.net.URI
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.{HttpClient, HttpRequest}

class JavaHttpClient extends HttpClient {
  private val httpClient = HttpClient.newHttpClient()
  override def getInputStream(uri: String): InputStream = {
    val request = HttpRequest.newBuilder().uri(URI.create(uri)).build()
    val response = httpClient.send(request, BodyHandlers.ofInputStream())
    val statusCode = response.statusCode()
    if(statusCode != 200){
      throw new RuntimeException(s"got status code $statusCode for request $uri")
    }
    response.body()
  }
}
