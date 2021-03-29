package com.jazavac.taskmanager.internal.implementation

import com.jazavac.taskmanager.api.Priority
import com.jazavac.taskmanager.api.Process
import com.jazavac.taskmanager.api.SortOrder
import com.jazavac.taskmanager.api.TaskManager

class PriorityTaskManager(override val capacity: Int) : TaskManager {

    @Synchronized
    override fun add(command: String, priority: Priority): Long {
        TODO("Not yet implemented")
    }

    override fun list(order: SortOrder): List<Process> {
        TODO("Not yet implemented")
    }

    override fun kill(identifier: Int): Int {
        TODO("Not yet implemented")
    }

    override fun killGroup(priority: Priority): Int {
        TODO("Not yet implemented")
    }

    override fun killAll(): Int {
        TODO("Not yet implemented")
    }
}