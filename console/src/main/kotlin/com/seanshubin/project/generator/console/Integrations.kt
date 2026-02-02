package com.seanshubin.project.generator.console

import com.seanshubin.project.generator.di.contract.FilesContract
import com.seanshubin.project.generator.http.HttpClientFactory

/**
 * I/O boundary interfaces for the application.
 *
 * Separates pure I/O operations (files, network, console output) from business logic,
 * enabling the entire application to be tested with fake implementations.
 *
 * Following the Staged Dependency Injection pattern, Integrations is the first stage
 * that provides the fundamental I/O capabilities needed by all subsequent stages.
 */
interface Integrations {
    /**
     * Emit a line to standard output.
     * In production: System.out::println
     * In tests: capture to list
     */
    val emit: (String) -> Unit

    /**
     * Emit a line to standard error.
     * In production: System.err::println
     * In tests: capture to list
     */
    val emitError: (String) -> Unit

    /**
     * File system operations.
     * In production: real file system via FilesDelegate
     * In tests: in-memory file system
     */
    val files: FilesContract

    /**
     * HTTP client factory for network operations.
     * In production: HttpClientFactoryImpl
     * In tests: fake HTTP client returning canned responses
     */
    val httpClientFactory: HttpClientFactory
}
