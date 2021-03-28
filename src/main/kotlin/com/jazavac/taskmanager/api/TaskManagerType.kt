package com.jazavac.taskmanager.api

import com.jazavac.taskmanager.internal.implementation.FifoTaskManager
import com.jazavac.taskmanager.internal.implementation.PriorityTaskManager
import com.jazavac.taskmanager.internal.implementation.SimpleTaskManager
import kotlin.reflect.KClass

/**
 * Enumeration of available [TaskManager] implementations.
 * See [TaskManagerType.description] value for behavior description.
 */
enum class TaskManagerType(val description: String, val implementation: KClass<out TaskManager>) {
    SIMPLE("When capacity is full, adding a new process fails.", SimpleTaskManager::class),
    FIFO("When capacity is full, adding a new process replaces the oldest one.", FifoTaskManager::class),
    PRIORITY(
        "When capacity is full, adding a new process replaces the oldest one with the lowest priority. " +
                "If the priority of the new process is lower than any of the currently managed processes, adding it fails.",
        PriorityTaskManager::class
    );

}