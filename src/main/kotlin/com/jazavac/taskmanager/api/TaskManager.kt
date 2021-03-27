package com.jazavac.taskmanager.api

import kotlin.reflect.full.primaryConstructor

/**
 * Component for handling OS processes.
 *
 *
 */
interface TaskManager {
    val capacity: Int

    fun add(command: String, priority: Priority = Priority.MEDIUM): Int
    fun list(order: SortOrder): List<Process>
    fun kill(identifier: Int): Int
    fun killGroup(priority: Priority): Int
    fun killAll(): Int

    companion object {
        fun new(type: TaskManagerType, capacity: Int): TaskManager {
            return type.implementation.primaryConstructor!!.call(capacity)
        }
    }
}