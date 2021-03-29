package com.jazavac.taskmanager.api

import com.jazavac.taskmanager.api.Process.Companion.comparator
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldExistInOrder
import io.mockk.mockk
import java.time.Instant

class ProcessTest : ShouldSpec({

    context("sorting Processes") {
        val unsorted = listOf(
            Process(2, Priority.MEDIUM, mockk(), Instant.now().minusSeconds(15)),
            Process(1, Priority.MEDIUM, mockk(), Instant.now().minusSeconds(5)),
            Process(4, Priority.HIGH, mockk(), Instant.now()),
            Process(3, Priority.LOW, mockk(), Instant.now().minusSeconds(10))
        )
        context("by id") {
            should("return a collection sorted by identifiers (ascending)") {
                val sorted = unsorted.sortedWith(comparator(SortOrder.ID))
                sorted shouldExistInOrder listOf(
                    { it.identifier == 1L },
                    { it.identifier == 2L },
                    { it.identifier == 3L },
                    { it.identifier == 4L })
            }
        }
        context("by priority") {
            should("return a collection sorted by priority (descending), then id (ascending)") {
                val sorted = unsorted.sortedWith(comparator(SortOrder.PRIORITY))
                sorted shouldExistInOrder listOf(
                    { it.identifier == 4L },
                    { it.identifier == 1L },
                    { it.identifier == 2L },
                    { it.identifier == 3L })
            }
        }
        context("by creation time") {
            should("return a collection sorted by creation time (ascending)") {
                val sorted = unsorted.sortedWith(comparator(SortOrder.CREATION_TIME))
                sorted shouldExistInOrder listOf(
                    { it.identifier == 2L },
                    { it.identifier == 3L },
                    { it.identifier == 1L },
                    { it.identifier == 4L })
            }
        }
    }

})
