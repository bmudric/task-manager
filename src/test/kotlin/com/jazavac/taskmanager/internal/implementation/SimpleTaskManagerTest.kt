package com.jazavac.taskmanager.internal.implementation

import com.jazavac.taskmanager.api.NO_PROCESS
import com.jazavac.taskmanager.api.Priority
import com.jazavac.taskmanager.api.TaskManager
import com.jazavac.taskmanager.api.TaskManagerType
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.longs.shouldNotBeExactly
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

@Suppress("BlockingMethodInNonBlockingContext")
class SimpleTaskManagerTest : ShouldSpec({

    context("adding a new process in an empty SimpleTaskManager") {
        val taskManager = SimpleTaskManager(1)
        should("return a PID") {
            val identifier = taskManager.add("sleep 1")
            identifier shouldNotBeExactly NO_PROCESS
            identifier shouldBeGreaterThan 0
            taskManager.processes.size shouldBeExactly 1
        }
        context("adding a process over capacity") {
            should("return $NO_PROCESS") {
                val failId = taskManager.add("echo over capacity")
                failId shouldBe NO_PROCESS
            }
        }
    }

    context("swarming the SimpleTaskManager add function") {
        val coroutines = 1000
        val taskManager = SimpleTaskManager(10)
        val counterAdded = AtomicInteger(0)
        val counterFailed = AtomicInteger(0)
        val countdown = CountDownLatch(coroutines)
        for (i in 1..coroutines) {
            launch(Dispatchers.Default) {
                val result = taskManager.add("sleep 1")
                if (result == NO_PROCESS) counterFailed.incrementAndGet()
                else counterAdded.incrementAndGet()
                countdown.countDown()
            }
        }
        countdown.await()
        should("add processes up to capacity") {
            counterAdded.get() shouldBeExactly taskManager.capacity
        }
        should("fail adding processes over capacity") {
            counterFailed.get() shouldBeExactly coroutines - taskManager.capacity
        }
    }

    context("listing processes") {
        should("return only active processes").config(invocations = 10) {
            val taskManager = TaskManager.new(TaskManagerType.SIMPLE, 10)
            taskManager.add("echo -n")
            taskManager.add("echo -n")
            Thread.sleep(10)
            taskManager.add("sleep 1")
            val list = taskManager.list()
            list.size shouldBeExactly 1
        }
    }

    context("killing a single process") {
        val taskManager = SimpleTaskManager(10)
        val id = taskManager.add("sleep 1")
        should("remove the process and return 1") {
            taskManager.kill(id) shouldBeExactly 1
            taskManager.processes.size shouldBeExactly 0
        }
        context("that does not exist") {
            should("return 0") {
                taskManager.kill(id) shouldBeExactly 0
            }
        }
    }

    context("killing all processes") {
        val taskManager = SimpleTaskManager(10)
        val createCount = 3
        for (i in 1..createCount) {
            taskManager.add("sleep 1")
        }
        val killCount = taskManager.killAll()
        should("return the total count of previously tracked processes") {
            killCount shouldBeExactly createCount
        }
        should("result in an empty backing map") {
            taskManager.processes.shouldBeEmpty()
        }
    }

    context("killing processes by priority") {
        val taskManager = SimpleTaskManager(10)
        taskManager.add("sleep 1", Priority.MEDIUM)
        taskManager.add("sleep 1", Priority.HIGH)
        taskManager.add("sleep 1", Priority.MEDIUM)
        taskManager.add("sleep 1", Priority.LOW)
        taskManager.add("sleep 1", Priority.MEDIUM)
        val totalCount = taskManager.processes.size
        val mediumCount = taskManager.processes.values.fold(0) { sum, proc ->
            if (proc.priority == Priority.MEDIUM) sum + 1 else sum
        }
        val killCount = taskManager.killGroup(Priority.MEDIUM)
        should("return the total previous active count of that priority") {
            killCount shouldBeExactly mediumCount
        }
        should("keep the remaining processes of other priorities") {
            taskManager.processes.size shouldBeExactly totalCount - killCount
            taskManager.processes.values.forEach {
                it.priority shouldNotBe Priority.MEDIUM
            }
        }
    }

})