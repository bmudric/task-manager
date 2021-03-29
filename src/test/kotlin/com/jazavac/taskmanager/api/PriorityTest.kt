package com.jazavac.taskmanager.api

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly

class PriorityTest : ShouldSpec({

    context("sorting a Priority list") {
        val unsorted = listOf(Priority.MEDIUM, Priority.HIGH, Priority.LOW)
        context("ascending") {
            should("return in order from low to high") {
                val sorted = unsorted.sorted()
                sorted shouldContainExactly listOf(Priority.LOW, Priority.MEDIUM, Priority.HIGH)
            }
        }
        context("descending") {
            should("return in order from high to low") {
                val sorted = unsorted.sortedDescending()
                sorted shouldContainExactly listOf(Priority.HIGH, Priority.MEDIUM, Priority.LOW)
            }
        }
    }

})
