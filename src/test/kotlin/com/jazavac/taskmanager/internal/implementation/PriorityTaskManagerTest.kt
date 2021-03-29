package com.jazavac.taskmanager.internal.implementation

import com.jazavac.taskmanager.api.NO_PROCESS
import com.jazavac.taskmanager.api.Priority
import com.jazavac.taskmanager.api.TaskManager
import com.jazavac.taskmanager.api.TaskManagerType
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.longs.shouldBeExactly
import io.kotest.matchers.shouldNotBe

@Suppress("BlockingMethodInNonBlockingContext")
class PriorityTaskManagerTest : ShouldSpec({

    context("adding a new process in a PriorityTaskManager") {
        val taskManager = PriorityTaskManager(4)
        taskManager.add("sleep 1", Priority.HIGH)
        val m1 = taskManager.add("sleep 1", Priority.MEDIUM)
        taskManager.add("sleep 1", Priority.HIGH)
        should("add all candidates up to capacity") {
            taskManager.processQueue.size shouldBeExactly 3
            taskManager.processQueue.peek().identifier shouldBeExactly m1
        }
        val l1 = taskManager.add("sleep 1", Priority.LOW)
        should("have the lowest priority element at the head") {
            taskManager.processQueue.peek().identifier shouldBeExactly l1
        }
        taskManager.add("sleep 1")
        should("result in popping the lowest priority out when higher is added") {
            taskManager.processQueue.peek().identifier shouldBeExactly m1
        }
        taskManager.add("sleep 1", Priority.HIGH)
        taskManager.add("sleep 1", Priority.HIGH)
        val hOverCapacity = taskManager.add("sleep 1", Priority.HIGH)
        should("not add any more max priority processes once full") {
            hOverCapacity shouldBeExactly NO_PROCESS
        }
    }

    context("listing processes") {
        should("return only active processes").config(invocations = 10) {
            val taskManager = TaskManager.new(TaskManagerType.PRIORITY, 10)
            taskManager.add("echo -n")
            taskManager.add("echo -n")
            Thread.sleep(10)
            taskManager.add("sleep 1")
            val list = taskManager.list()
            list.size shouldBeExactly 1
        }
    }

    context("killing a single process") {
        val taskManager = PriorityTaskManager(10)
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
        val taskManager = PriorityTaskManager(5)
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
        val taskManager = PriorityTaskManager(10)
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
