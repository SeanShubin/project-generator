package com.seanshubin.project.generator.domain

trait HttpClient {
  def get(uri: String): String
}
