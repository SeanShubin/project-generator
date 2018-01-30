package com.seanshubin.project.generator.datomic

import java.nio.channels.Channels
import java.nio.file.{Files, Paths, StandardOpenOption}

import com.seanshubin.project.generator.duration.format.DurationFormat
import com.seanshubin.project.generator.http.{GoogleHttpClient, HttpClient}
import com.seanshubin.project.generator.io.IoUtil
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.scalatest.FunSuite

class DatomicDownloaderTest extends FunSuite {
  test("list datomic versions") {
    trait DatomicRepository {
      def versions(): Seq[String]
    }
    def elementsToSeq(elements: Elements): Seq[Element] = {
      for {
        i <- 0 until elements.size()
        element = elements.get(i)
      } yield element
    }

    def rowToVersion(element: Element): String = {
      val cells = element.select("td")
      val versionCell = cells.get(2)
      versionCell.ownText()
    }

    class DatomicOfficialWebsite extends DatomicRepository {
      override def versions(): Seq[String] = {
        val url = "https://my.datomic.com/downloads/free"
        val jsoup = Jsoup.connect(url).get()
        val rows = elementsToSeq(jsoup.select("#downloads-table tbody tr"))
        val versions = rows.map(rowToVersion)
        versions
      }
    }
    val httpClient: HttpClient = new GoogleHttpClient

    def downloadVersion(version: String): Unit = {
      val uri = s"https://my.datomic.com/downloads/free/$version"
      val inputStream = httpClient.getInputStream(uri)
      if (inputStream == null) {
        throw new RuntimeException(s"No data found at $uri")
      }
      val destination = Paths.get("target", "datomic.jar")
      Files.createDirectories(destination.getParent)
      println(destination.toAbsolutePath)
      //      val outputStream = Files.newOutputStream(destination)
      val inputChannel = Channels.newChannel(inputStream)
      val outputChannel = Files.newByteChannel(destination, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)
      //      IoUtil.feedInputStreamToOutputStream(inputStream, outputStream)
      IoUtil.feedReadableChannelIntoWritableChannel(inputChannel, outputChannel)
      //      outputStream.close()
      outputChannel.close()
    }

    val datomicRepository: DatomicRepository = new DatomicOfficialWebsite
    val versions = datomicRepository.versions()
    versions.foreach(println)
    val startTime = System.currentTimeMillis()
    downloadVersion(versions.head)
    val endTime = System.currentTimeMillis()
    val duration = endTime - startTime
    println(DurationFormat.MillisecondsFormat.format(duration))
  }
}
