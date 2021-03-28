package com.jazavac.taskmanager.internal.process

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.util.concurrent.TimeUnit

@Suppress("BlockingMethodInNonBlockingContext")
class ProcessBuilderAdapterTest : ShouldSpec({

    context("calling createProcess with a valid command") {
        shouldNotThrowAny {
            val newProcess = ProcessBuilderAdapter.createProcess("echo 'test createProcess'")
            should("return a Process instance") {
                newProcess.shouldNotBeNull()
                newProcess.shouldBeInstanceOf<Process>()
            }
        }
    }

    context("calling killProcess with a valid running process") {
        shouldNotThrowAny {
            val newProcess = ProcessBuilderAdapter.createProcess("sleep 10")
            ProcessBuilderAdapter.killProcess(newProcess)
            should("have ended the process") {
                newProcess.waitFor(1, TimeUnit.SECONDS) shouldBe true
                newProcess.isAlive shouldBe false
            }
        }
    }

})
