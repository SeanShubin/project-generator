package com.seanshubin.project.generator.replicator

import com.seanshubin.project.generator.di.contract.FilesContract
import com.seanshubin.project.generator.di.delegate.FilesDelegate
import com.seanshubin.project.generator.http.HttpClientFactory
import com.seanshubin.project.generator.http.HttpClientFactoryImpl

object ProductionIntegrations : ReplicatorIntegrations {
    override val emit: (String) -> Unit = System.out::println
    override val emitError: (String) -> Unit = System.err::println
    override val files: FilesContract = FilesDelegate.defaultInstance()
    override val httpClientFactory: HttpClientFactory = HttpClientFactoryImpl()
}
