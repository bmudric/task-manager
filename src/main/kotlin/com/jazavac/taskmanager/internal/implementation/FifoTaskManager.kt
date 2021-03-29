package com.jazavac.taskmanager.internal.implementation

import com.jazavac.taskmanager.api.Priority
import com.jazavac.taskmanager.api.Process
import com.jazavac.taskmanager.api.Process.Companion.comparator
import com.jazavac.taskmanager.api.SortOrder
import com.jazavac.taskmanager.api.TaskManager
import com.jazavac.taskmanager.internal.process.ProcessBuilderAdapter
import java.util.concurrent.LinkedBlockingQueue

class FifoTaskManager(override val capacity: Int) : TaskManager {

    internal val processQueue: LinkedBlockingQueue<Process> = LinkedBlockingQueue(capacity)

    @Synchronized
    override fun add(command: String, priority: Priority): Long {
        filterOutDeadProcesses()
        val newProcess = Process.new(command, priority)
        if (this.processQueue.offer(newProcess)) {
            return newProcess.identifier
        }
        // if max capacity reached, oldest is killed and removed
        val oldest = this.processQueue.poll()
        ProcessBuilderAdapter.killProcess(oldest.osProcess)
        this.processQueue.add(newProcess)
        return newProcess.identifier
    }

    @Synchronized
    override fun list(order: SortOrder): List<Process> {
        filterOutDeadProcesses()
        return this.processQueue.sortedWith(comparator(order))
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

    private fun filterOutDeadProcesses() {
        this.processQueue.removeIf { !it.osProcess.isAlive }
    }
}