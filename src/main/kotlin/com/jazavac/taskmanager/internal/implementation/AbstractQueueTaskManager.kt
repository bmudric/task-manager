package com.jazavac.taskmanager.internal.implementation

import com.jazavac.taskmanager.api.Priority
import com.jazavac.taskmanager.api.Process
import com.jazavac.taskmanager.api.SortOrder
import com.jazavac.taskmanager.api.TaskManager
import com.jazavac.taskmanager.internal.process.ProcessBuilderAdapter
import java.util.*

abstract class AbstractQueueTaskManager : TaskManager {

    internal abstract val processQueue: Queue<Process>

    @Synchronized
    override fun list(order: SortOrder): List<Process> {
        filterOutDeadProcesses()
        return this.processQueue.sortedWith(Process.comparator(order))
    }

    @Synchronized
    override fun kill(identifier: Long): Int {
        val foundProcess = this.processQueue.find { it.identifier == identifier }
        foundProcess?.let {
            ProcessBuilderAdapter.killProcess(it.osProcess)
            this.processQueue.remove(it)
            return 1
        }
        return 0
    }

    @Synchronized
    override fun killGroup(priority: Priority): Int {
        val toKill = this.processQueue.filter { it.priority == priority }
        toKill.forEach { ProcessBuilderAdapter.killProcess(it.osProcess) }
        this.processQueue.removeAll(toKill)
        return toKill.size
    }

    @Synchronized
    override fun killAll(): Int {
        val killingCount = this.processQueue.size
        this.processQueue.forEach { ProcessBuilderAdapter.killProcess(it.osProcess) }
        this.processQueue.clear()
        return killingCount
    }

    internal fun filterOutDeadProcesses() {
        this.processQueue.removeIf { !it.osProcess.isAlive }
    }
}