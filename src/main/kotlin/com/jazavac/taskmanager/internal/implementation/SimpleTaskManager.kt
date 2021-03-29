package com.jazavac.taskmanager.internal.implementation

import com.jazavac.taskmanager.api.*
import com.jazavac.taskmanager.api.Process.Companion.comparator
import com.jazavac.taskmanager.internal.process.ProcessBuilderAdapter

class SimpleTaskManager(override val capacity: Int) : TaskManager {

    internal var processes: Map<Long, Process>

    init {
        this.processes = newBackingMap()
    }

    private fun newBackingMap(): Map<Long, Process> {
        return HashMap()
    }

    @Synchronized
    override fun add(command: String, priority: Priority): Long {
        filterOutDeadProcesses()
        if (this.processes.size >= this.capacity) {
            return NO_PROCESS
        }
        val newProcess = Process.new(command, priority)
        this.processes = this.processes + Pair(newProcess.identifier, newProcess)
        return newProcess.identifier
    }

    @Synchronized
    override fun list(order: SortOrder): List<Process> {
        filterOutDeadProcesses()
        return this.processes.values.sortedWith(comparator(order))
    }

    @Synchronized
    override fun kill(identifier: Long): Int {
        val toKill = this.processes[identifier] ?: return 0
        ProcessBuilderAdapter.killProcess(toKill.osProcess)
        this.processes = this.processes.minus(identifier)
        return 1
    }

    @Synchronized
    override fun killGroup(priority: Priority): Int {
        val toKill = this.processes.filterValues { it.priority == priority }
        toKill.values.forEach { ProcessBuilderAdapter.killProcess(it.osProcess) }
        this.processes = this.processes.minus(toKill.keys)
        return toKill.size
    }

    @Synchronized
    override fun killAll(): Int {
        val killingCount = this.processes.size
        this.processes.values.forEach { ProcessBuilderAdapter.killProcess(it.osProcess) }
        this.processes = newBackingMap()
        return killingCount
    }

    private fun filterOutDeadProcesses() {
        this.processes = this.processes.filterValues { it.osProcess.isAlive }
    }

}