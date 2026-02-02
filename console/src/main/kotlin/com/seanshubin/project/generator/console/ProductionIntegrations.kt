package com.seanshubin.project.generator.console

import com.seanshubin.project.generator.di.contract.FilesContract
import com.seanshubin.project.generator.di.delegate.FilesDelegate
import com.seanshubin.project.generator.http.HttpClientFactory
import com.seanshubin.project.generator.http.HttpClientFactoryImpl

/**
 * Production implementation of Integrations using real I/O.
 *
 * Uses actual console output (System.out/System.err), real file system,
 * and real HTTP client for production deployments.
 */
object ProductionIntegrations : Integrations {
    override val emit: (String) -> Unit = System.out::println
    override val emitError: (String) -> Unit = System.err::println
    override val files: FilesContract = FilesDelegate.defaultInstance()
    override val httpClientFactory: HttpClientFactory = HttpClientFactoryImpl()
}
