package ru.spbstu.wheels

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReificationTest {
    @Test
    fun smokeTest() {

        val reification = Reification<String?>()

        assertTrue("Hello" in reification)
        assertEquals(null, reification.safeCast(2.0))
        assertEquals("Hello", reification.safeCast("Hello"))

    }
}