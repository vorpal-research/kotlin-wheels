package ru.spbstu.wheels

import kotlin.test.Test
import kotlin.test.assertEquals

class MiscTest {
    @Test
    fun repeat() {
        var i = 0
        3 times {
            ++i
        }
        assertEquals(3, i)
    }

    @Test
    fun makeIf() {
        val numbers = 1..80

        assertEquals(
            (3..80 step 3).toList(),
            numbers.mapNotNull { runIf(it % 3 == 0) { it } }
        )
    }
}