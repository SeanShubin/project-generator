package com.seanshubin.project.generator.generator

import com.seanshubin.project.generator.commands.Command
import com.seanshubin.project.generator.core.Project

/**
 * The build-system-specific half of generation.
 *
 * [GeneratorImpl] owns what every project needs regardless of build system --
 * the overall sequence of work, validation that holds everywhere, and source
 * replication -- and delegates to an implementation of this interface for
 * everything whose shape depends on the build system: what the build files are
 * called, where sources live, how coordinates and versions are expressed, and
 * which helper scripts make sense.
 *
 * The two implementations share no base class on purpose.  A `pom.xml` per
 * module with group/artifact coordinates and a Cargo workspace with flat crate
 * names have little in common structurally; a common superclass would only be
 * able to hold a lowest common denominator, which is why what genuinely is
 * shared was pushed down into [com.seanshubin.project.generator.core] instead.
 */
interface BuildSystemGenerator {
    /**
     * Rejects spec features this build system cannot express, so that an
     * unsupported combination fails with an explanation instead of silently
     * generating something wrong.
     */
    fun validate(project: Project)

    /** The build file(s) at the project root. */
    fun rootCommands(project: Project): List<Command>

    /** The build file and source directories for one module. */
    fun moduleCommands(project: Project, module: String, dependencies: List<String>): List<Command>

    /**
     * Everything else that is fixed for the project: helper scripts, the
     * `.gitignore`, build-system-specific config files and plugin modules.
     */
    fun supplementalCommands(project: Project): List<Command>
}
