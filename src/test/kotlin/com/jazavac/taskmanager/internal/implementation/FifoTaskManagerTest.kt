package com.jazavac.taskmanager.internal.implementation

import com.jazavac.taskmanager.api.Priority
import com.jazavac.taskmanager.api.SortOrder
import com.jazavac.taskmanager.api.TaskManager
import com.jazavac.taskmanager.api.TaskManagerType
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

@Suppress("BlockingMethodInNonBlockingContext")
class FifoTaskManagerTest : ShouldSpec({

    context("adding a new process in an empty FifoTaskManager") {
        val taskManager = FifoTaskManager(2)
        val id1 = taskManager.add("sleep 1")
        val process1 = taskManager.processQueue.find { it.identifier == id1 }
        should("have started the process") {
            process1 shouldNotBe null
            process1!!.osProcess.isAlive shouldBe true
        }
        context("adding more processes") {
            taskManager.add("sleep 1")
            taskManager.add("sleep 1")
            should("observe capacity") {
                taskManager.processQueue.size shouldBeExactly taskManager.capacity
            }
            should("have removed the oldest one when capacity reached") {
                taskManager.processQueue.contains(process1) shouldBe false
            }
            should("have killed the oldest process") {
                process1!!.osProcess.isAlive shouldBe false
            }
        }
    }

    context("listing processes") {
        should("return only active processes").config(invocations = 10) {
            val taskManager = TaskManager.new(TaskManagerType.FIFO, 10)
            taskManager.add("echo -n")
            taskManager.add("echo -n")
            Thread.sleep(10)
            taskManager.add("sleep 1")
            val list = taskManager.list()
            list.size shouldBeExactly 1
        }
    }

    context("killing a single process") {
        val taskManager = FifoTaskManager(10)
        val id = taskManager.add("sleep 1")
        should("remove the process and return 1") {
            taskManager.kill(id) shouldBeExactly 1
            taskManager.processQueue.size shouldBeExactly 0
        }
        context("that does not exist") {
            should("return 0") {
                taskManager.kill(id) shouldBeExactly 0
            }
        }
    }

    context("killing all processes") {
        val taskManager = FifoTaskManager(5)
        val createCount = taskManager.capacity * 2
        for (i in 1..createCount) {
            taskManager.add("sleep 1")
        }
        val killCount = taskManager.killAll()
        should("return the total count of previously tracked processes") {
            killCount shouldBeExactly taskManager.capacity
        }
        should("result in an empty backing map") {
            taskManager.processQueue.shouldBeEmpty()
        }
    }

    context("killing processes by priority") {
        val taskManager = FifoTaskManager(10)
        taskManager.add("sleep 1", Priority.MEDIUM)
        taskManager.add("sleep 1", Priority.HIGH)
        taskManager.add("sleep 1", Priority.MEDIUM)
        taskManager.add("sleep 1", Priority.LOW)
        taskManager.add("sleep 1", Priority.MEDIUM)
        val totalCount = taskManager.processQueue.size
        val mediumCount = taskManager.processQueue.fold(0) { sum, proc ->
            if (proc.priority == Priority.MEDIUM) sum + 1 else sum
        }
        val killCount = taskManager.killGroup(Priority.MEDIUM)
        should("return the total previous active count of that priority") {
            killCount shouldBeExactly mediumCount
        }
        should("keep the remaining processes of other priorities") {
            taskManager.processQueue.size shouldBeExactly totalCount - killCount
            taskManager.processQueue.forEach {
                it.priority shouldNotBe Priority.MEDIUM
            }
        }
    }

})
