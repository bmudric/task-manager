package com.jazavac.taskmanager.api

import com.jazavac.taskmanager.internal.implementation.FifoTaskManager
import com.jazavac.taskmanager.internal.implementation.PriorityTaskManager
import com.jazavac.taskmanager.internal.implementation.SimpleTaskManager
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class TaskManagerTest : ShouldSpec({

    context("TaskManager factory function called for SIMPLE type") {
        shouldNotThrowAny {
            val newInstance = TaskManager.new(TaskManagerType.SIMPLE, 10)
            should("have capacity 10") {
                newInstance.capacity shouldBe 10
            }
            should("return SimpleTaskManager") {
                newInstance.shouldBeInstanceOf<SimpleTaskManager>()
            }
        }
    }

    context("TaskManager factory function called for FIFO type") {
        shouldNotThrowAny {
            val newInstance = TaskManager.new(TaskManagerType.FIFO, 11)
            should("have capacity 11") {
                newInstance.capacity shouldBe 11
            }
            should("return SimpleTaskManager") {
                newInstance.shouldBeInstanceOf<FifoTaskManager>()
            }
        }
    }

    context("TaskManager factory function called for PRIORITY type") {
        shouldNotThrowAny {
            val newInstance = TaskManager.new(TaskManagerType.PRIORITY, 12)
            should("have capacity 12") {
                newInstance.capacity shouldBe 12
            }
            should("return SimpleTaskManager") {
                newInstance.shouldBeInstanceOf<PriorityTaskManager>()
            }
        }
    }

})
