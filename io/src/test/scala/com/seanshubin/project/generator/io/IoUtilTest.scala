package com.seanshubin.project.generator.io

import java.io.ByteArrayOutputStream
import java.nio.charset.{Charset, StandardCharsets}

import com.seanshubin.project.generator.io.IoUtil._
import org.scalatest.FunSuite

class IoUtilTest extends FunSuite {
  val charset: Charset = StandardCharsets.UTF_8
  test("bytes") {
    val inputStream = stringToInputStream("Hello, world!", charset)
    val string = inputStreamToString(inputStream, charset)
    assert(string === "Hello, world!")
  }

  test("string to output stream") {
    val original = "Hello, world!"
    val outputStream = new ByteArrayOutputStream()
    IoUtil.stringToOutputStream(original, charset, outputStream)
    val string = bytesToString(outputStream.toByteArray, charset)
    assert(string === "Hello, world!")
  }

  test("bytes to output stream") {
    val original = "Hello, world!"
    val bytes = stringToBytes(original, charset)
    val outputStream = new ByteArrayOutputStream()
    bytesToOutputStream(bytes, outputStream)
    val string = bytesToString(outputStream.toByteArray, charset)
    assert(string === "Hello, world!")
  }

  test("chars") {
    val reader = stringToReader("Hello, world!")
    val string = readerToString(reader)
    assert(string === "Hello, world!")
  }

  test("channel") {
    val readableChannel = stringToReadableByteChannel("Hello, world!", charset)
    val byteArrayOutputStream = new ByteArrayOutputStream()
    val writableChannel = outputStreamToWritableByteChannel(byteArrayOutputStream)
    feedReadableChannelIntoWritableChannel(readableChannel, writableChannel)
    val resultString = bytesToString(byteArrayOutputStream.toByteArray, charset)
    assert(resultString === "Hello, world!")
  }
}
