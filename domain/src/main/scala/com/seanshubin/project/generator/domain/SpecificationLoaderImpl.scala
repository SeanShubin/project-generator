package com.seanshubin.project.generator.domain

import java.nio.charset.Charset
import java.nio.file.Paths

import com.seanshubin.devon.domain.DevonMarshallerWiring
import com.seanshubin.project.generator.domain.Specification.Project

class SpecificationLoaderImpl(files: FilesContract,
                              charset: Charset,
                              notifyEffectiveProjectSpecification: Project => Unit) extends SpecificationLoader {
  override def load(name: String): Project = {
    val path = Paths.get(name)
    val bytes = files.readAllBytes(path)
    val text = new String(bytes, charset)
    val projectSpecification = DevonMarshallerWiring.Default.stringToValue(text, classOf[Project])
    notifyEffectiveProjectSpecification(projectSpecification)
    projectSpecification
  }
}
