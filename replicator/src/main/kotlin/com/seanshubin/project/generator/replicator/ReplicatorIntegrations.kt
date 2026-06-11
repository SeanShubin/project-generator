package com.seanshubin.project.generator.replicator

import com.seanshubin.project.generator.di.contract.FilesContract
import com.seanshubin.project.generator.http.HttpClientFactory

interface ReplicatorIntegrations {
    val emit: (String) -> Unit
    val emitError: (String) -> Unit
    val files: FilesContract
    val httpClientFactory: HttpClientFactory
}
