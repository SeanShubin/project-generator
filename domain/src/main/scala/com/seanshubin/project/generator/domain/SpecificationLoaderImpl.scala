package com.seanshubin.project.generator.domain

import java.nio.charset.Charset
import java.nio.file.Path

import com.seanshubin.devon.domain.DevonMarshallerWiring
import com.seanshubin.project.generator.domain.Specification.Project

class SpecificationLoaderImpl(files: FilesContract,
                              charset: Charset,
                              notifyEffectiveProjectSpecification: Project => Unit) extends SpecificationLoader {
  override def load(path: Path): Project = {
    val bytes = files.readAllBytes(path)
    val text = new String(bytes, charset)
    val projectSpecification = DevonMarshallerWiring.Default.stringToValue(text, classOf[Project])
    notifyEffectiveProjectSpecification(projectSpecification)
    projectSpecification
  }
}
