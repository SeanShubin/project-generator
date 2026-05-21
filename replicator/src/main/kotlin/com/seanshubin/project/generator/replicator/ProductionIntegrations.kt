package com.seanshubin.project.generator.replicator

import com.seanshubin.project.generator.di.contract.FilesContract
import com.seanshubin.project.generator.di.delegate.FilesDelegate

object ProductionIntegrations : ReplicatorIntegrations {
    override val emit: (String) -> Unit = System.out::println
    override val emitError: (String) -> Unit = System.err::println
    override val files: FilesContract = FilesDelegate.defaultInstance()
}
