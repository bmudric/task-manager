package com.jazavac.taskmanager.api

/**
 * Sort order when listing processes, see [SortOrder.description] for more details.
 */
enum class SortOrder(val description: String) {
    ID("Sort process list by PID, ascending."),
    PRIORITY("Sort process list by priority, descending; secondary sort by PID, ascending."),
    CREATION_TIME("Sort process list by creation time, ascending.")
}