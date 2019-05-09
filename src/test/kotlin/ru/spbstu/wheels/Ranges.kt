package ru.spbstu.wheels

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Ranges {
    @Test
    fun positiveInf() {
        assertEquals((0..20).toList(), (0..+Inf).take(21).toList())

        assertTrue(3 in 0..+Inf)
        assertTrue(Int.MAX_VALUE in 0..+Inf)
        assertFalse(-1 in 0..+Inf)

        assertFalse("a" in "z"..+Inf)
        assertTrue("z" in "a"..+Inf)
    }

    @Test
    fun negativeInf() {

        assertEquals((0 downTo -20).toList(), (0 downTo -Inf).take(21).toList())

        assertTrue(0 in -Inf..3)
        assertTrue(Int.MIN_VALUE in -Inf..0)
        assertFalse(1 in -Inf..0)

        assertFalse("z" in -Inf.."a")
        assertTrue("a" in -Inf.."z")
    }
}