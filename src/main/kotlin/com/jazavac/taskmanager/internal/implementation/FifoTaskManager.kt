package com.jazavac.taskmanager.internal.implementation

import com.jazavac.taskmanager.api.Priority
import com.jazavac.taskmanager.api.Process
import com.jazavac.taskmanager.internal.process.ProcessBuilderAdapter
import java.util.concurrent.LinkedBlockingQueue

class FifoTaskManager(override val capacity: Int) : AbstractQueueTaskManager() {

    override val processQueue: LinkedBlockingQueue<Process> = LinkedBlockingQueue(capacity)

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

}