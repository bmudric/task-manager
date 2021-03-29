package com.jazavac.taskmanager.internal.implementation

import com.jazavac.taskmanager.api.NO_PROCESS
import com.jazavac.taskmanager.api.SortOrder
import com.jazavac.taskmanager.api.TaskManager
import com.jazavac.taskmanager.api.TaskManagerType
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

@Suppress("BlockingMethodInNonBlockingContext")
class SimpleTaskManagerTest : ShouldSpec({

    context("adding a new process in an empty SimpleTaskManager") {
        shouldNotThrowAny {
            val taskManager = TaskManager.new(TaskManagerType.SIMPLE, 1)
            should("return a PID") {
                val identifier = taskManager.add("sleep 1")
                identifier shouldNotBe NO_PROCESS
                identifier shouldBeGreaterThan 0
            }
            context("adding a process over capacity") {
                should("return $NO_PROCESS") {
                    val failId = taskManager.add("echo over capacity")
                    failId shouldBe NO_PROCESS
                }
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
            counterAdded.get() shouldBe taskManager.capacity
        }
        should("fail adding processes over capacity") {
            counterFailed.get() shouldBe coroutines - taskManager.capacity
        }
    }

    context("listing processes") {
        should("return only active processes").config(invocations = 10) {
            val taskManager = TaskManager.new(TaskManagerType.SIMPLE, 10)
            taskManager.add("echo first")
            taskManager.add("echo second")
            Thread.sleep(10)
            taskManager.add("sleep 1")
            val list = taskManager.list(SortOrder.ID)
            list.size shouldBe 1
        }
    }

})