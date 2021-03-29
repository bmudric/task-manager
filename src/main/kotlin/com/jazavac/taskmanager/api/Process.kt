package com.jazavac.taskmanager.api

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

    companion object {
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
    }
}