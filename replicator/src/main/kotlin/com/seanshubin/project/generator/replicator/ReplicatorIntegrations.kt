package com.seanshubin.project.generator.replicator

import com.seanshubin.project.generator.di.contract.FilesContract

interface ReplicatorIntegrations {
    val emit: (String) -> Unit
    val emitError: (String) -> Unit
    val files: FilesContract
}
