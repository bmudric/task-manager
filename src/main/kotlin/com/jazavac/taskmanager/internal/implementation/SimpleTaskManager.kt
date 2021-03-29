package com.jazavac.taskmanager.internal.implementation

import com.jazavac.taskmanager.api.*
import com.jazavac.taskmanager.api.Process.Companion.comparator
import com.jazavac.taskmanager.internal.process.ProcessBuilderAdapter
import java.time.Instant

class SimpleTaskManager(override val capacity: Int) : TaskManager {

    private var processes: Map<Long, Process>

    init {
        this.processes = HashMap()
    }

    @Synchronized
    override fun add(command: String, priority: Priority): Long {
        filterOutDeadProcesses()
        if (processes.size >= capacity) {
            return NO_PROCESS
        }
        val newOsProcess = ProcessBuilderAdapter.createProcess(command)
        val id = newOsProcess.pid()
        val newProcess = Process(id, priority, newOsProcess, Instant.now())
        this.processes = this.processes + Pair(id, newProcess)
        return id
    }

    override fun list(order: SortOrder): List<Process> {
        filterOutDeadProcesses()
        return this.processes.values.sortedWith(comparator(order))
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

    private fun filterOutDeadProcesses() {
        this.processes = this.processes.filterValues { it.osProcess.isAlive }
    }

}