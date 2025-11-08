package com.seanshubin.project.generator.domain

import com.seanshubin.project.generator.contract.FilesContract

interface Environment {
    val files: FilesContract
}