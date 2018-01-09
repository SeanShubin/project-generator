package com.seanshubin.project.generator.domain

import java.io.InputStream

import com.google.api.client.http._
import com.google.api.client.http.javanet.NetHttpTransport

class GoogleHttpClient extends HttpClient {
  val httpTransport: HttpTransport = new NetHttpTransport()
  val factory: HttpRequestFactory = httpTransport.createRequestFactory()

  override def getInputStream(uri: String): InputStream = {
    val url = new GenericUrl(uri)
    val httpRequest: HttpRequest = factory.buildGetRequest(url)
    httpRequest.setThrowExceptionOnExecuteError(false)
    val httpResponse: HttpResponse = httpRequest.execute()
    val statusCode = httpResponse.getStatusCode
    if (statusCode != 200) {
      throw new RuntimeException(s"got status code $statusCode for request $uri")
    }
    val inputStream = httpResponse.getContent
    inputStream
  }
}
