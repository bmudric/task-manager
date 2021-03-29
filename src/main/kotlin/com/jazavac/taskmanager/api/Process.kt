package com.jazavac.taskmanager.api

import com.jazavac.taskmanager.internal.process.ProcessBuilderAdapter
import java.time.Instant

/**
 * Represents a user-created process that is managed by [TaskManager]
 */
class Process(
    val identifier: Long,
    val priority: Priority,
    internal val osProcess: java.lang.Process,
    internal val creationTime: Instant
) {
    override fun toString(): String {
        return "${this.identifier}_${this.priority}"
    }

    companion object {
        /**
         * Convenience method for creating a new process.
         * @param command the command to run
         * @param priority the priority of the new process
         * @return newly created process
         */
        internal fun new(command: String, priority: Priority): Process {
            val newOsProcess = ProcessBuilderAdapter.createProcess(command)
            return Process(newOsProcess.pid(), priority, newOsProcess, Instant.now())
        }

        fun comparator(order: SortOrder): Comparator<Process> {
            return when (order) {
                SortOrder.ID -> idComparator
                SortOrder.PRIORITY -> priorityComparator
                SortOrder.CREATION_TIME -> creationTimeComparator
            }
        }

        private val idComparator = compareBy<Process> { it.identifier }
        private val priorityComparator = compareByDescending<Process> { it.priority }.thenBy(idComparator) { it }
        private val creationTimeComparator = compareBy<Process> { it.creationTime }
        internal val leastPriorityComparator = compareBy<Process> { it.priority }.thenBy(creationTimeComparator) { it }
    }
}