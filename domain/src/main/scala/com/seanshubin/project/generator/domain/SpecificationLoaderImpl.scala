package com.seanshubin.project.generator.domain

import java.nio.file.Path

import com.seanshubin.project.generator.domain.GlobalConstants.{charset, devonMarshaller}
import com.seanshubin.project.generator.domain.Specification.Project

class SpecificationLoaderImpl(files: FilesContract,
                              notifyEffectiveProjectSpecification: Project => Unit) extends SpecificationLoader {
  override def load(path: Path): Project = {
    val bytes = files.readAllBytes(path)
    val text = new String(bytes, charset)
    val projectSpecification = devonMarshaller.stringToValue(text, classOf[Project]).nullSafe
    notifyEffectiveProjectSpecification(projectSpecification)
    projectSpecification
  }
}
