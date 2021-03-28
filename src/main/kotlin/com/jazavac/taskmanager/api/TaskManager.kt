package com.jazavac.taskmanager.api

import com.jazavac.taskmanager.api.TaskManager.Companion.new
import kotlin.reflect.full.primaryConstructor

/**
 * Component for handling OS processes.
 *
 * Usage: create a new instance using the [new] function.
 * The TaskManager will keep track of created processes and manage them based on the desired implementation type.
 *
 */
interface TaskManager {
    val capacity: Int

    /**
     * Create a new process running the desired command.
     * Adding behavior depends on the chosen implementation, for more details see [TaskManagerType].
     * @param command the command to run, e.g. "echo 'I'm a teapot'"
     * @param priority the desired process priority that will affect deletion of processes at manager capacity,
     * subject to chosen implementation
     */
    fun add(command: String, priority: Priority = Priority.MEDIUM): Int

    /**
     * List the processes managed by this [TaskManager] instance.
     * @param order the desired sorting order, see [SortOrder.description] for details
     * @return the list of currently managed processes
     */
    fun list(order: SortOrder): List<Process>

    /**
     * Kill a single process.
     * @param identifier the id of the process to kill
     * @return the number of processes killed, 1 if the process id was found, otherwise 0
     */
    fun kill(identifier: Int): Int

    /**
     * Kill all the managed processes of the desired [Priority]
     * @param priority the [Priority] of the processes to kill
     * @return the number of processes found and killed
     */
    fun killGroup(priority: Priority): Int

    /**
     * Kill all managed processes.
     * @return the number of processes killed
     */
    fun killAll(): Int

    companion object {
        /**
         * Creates a new TaskManager instance.
         * @param type the [TaskManagerType] defines the behaviour of the [add] function
         * @param capacity the maximum number of processes that will be tracked by the manager
         */
        fun new(type: TaskManagerType, capacity: Int): TaskManager {
            return type.implementation.primaryConstructor!!.call(capacity)
        }
    }
}