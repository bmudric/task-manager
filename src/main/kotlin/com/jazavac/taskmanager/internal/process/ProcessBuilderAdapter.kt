package com.jazavac.taskmanager.internal.process

import java.io.File

/**
 * Thin wrapper around [ProcessBuilder] in order to unify the way it is used by various TaskManager implementations.
 */
object ProcessBuilderAdapter {

    /**
     * Convenience method for creating simple processes with default settings.
     *
     * For potential errors and modification considerations see [ProcessBuilder.start].
     *
     * @param command the command to run with bash
     * @return the created process instance representing the running command
     */
    fun createProcess(vararg command: String): Process {
        return ProcessBuilder("/bin/bash", "-c", *command)
            .inheritIO()
            .directory(File("/tmp"))
            .start()
    }

    /**
     * Convenience method for killing a process.
     * @param toKill the process to kill
     */
    fun killProcess(toKill: Process) {
        toKill.destroy()
    }
}