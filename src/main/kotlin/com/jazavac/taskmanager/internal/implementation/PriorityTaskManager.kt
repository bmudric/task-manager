package com.jazavac.taskmanager.internal.implementation

import com.jazavac.taskmanager.api.NO_PROCESS
import com.jazavac.taskmanager.api.Priority
import com.jazavac.taskmanager.api.Process
import com.jazavac.taskmanager.internal.process.ProcessBuilderAdapter
import java.util.*

class PriorityTaskManager(override val capacity: Int) : AbstractQueueTaskManager() {

    override val processQueue: Queue<Process> = PriorityQueue(Process.leastPriorityComparator)

    @Synchronized
    override fun add(command: String, priority: Priority): Long {
        filterOutDeadProcesses()
        // remove oldest when capacity full
        if (this.processQueue.size >= capacity) {
            this.processQueue.peek()?.let {
                if (it.priority < priority) {
                    val oldest = this.processQueue.poll()
                    ProcessBuilderAdapter.killProcess(oldest.osProcess)
                } else {
                    return NO_PROCESS
                }
            }
        }
        // when capacity allows, add new process
        val newProcess = Process.new(command, priority)
        this.processQueue.add(newProcess)
        return newProcess.identifier
    }

}