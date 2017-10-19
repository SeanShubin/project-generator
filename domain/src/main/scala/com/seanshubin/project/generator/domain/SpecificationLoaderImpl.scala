package com.seanshubin.project.generator.domain

import java.nio.charset.Charset
import java.nio.file.Paths

import com.seanshubin.devon.domain.DevonMarshallerWiring
import com.seanshubin.project.generator.domain.Specification.ProjectSpecification

class SpecificationLoaderImpl(files: FilesContract,
                              charset: Charset,
                              notifyEffectiveProjectSpecification: ProjectSpecification => Unit) extends SpecificationLoader {
  override def load(name: String): ProjectSpecification = {
    val path = Paths.get(name)
    val bytes = files.readAllBytes(path)
    val text = new String(bytes, charset)
    val projectSpecification = DevonMarshallerWiring.Default.stringToValue(text, classOf[ProjectSpecification])
    notifyEffectiveProjectSpecification(projectSpecification)
    projectSpecification
  }
}
