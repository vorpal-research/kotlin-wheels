package ru.spbstu.wheels

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class FunctionsTest {
    @Test
    fun runOnce() {
        var x: Int = 0

        fun interm() {
            runOnce { x = Random.Default.nextInt(1, 5000) }
        }

        interm()
        assertNotEquals(0, x)
        val current = x
        interm()
        assertEquals(current, x)
        interm()
        assertEquals(current, x)

        var y = 0
        fun interm2() {
            runOnce { y = Random.Default.nextInt(1, Int.MAX_VALUE) }
        }

        fun interm3() {
            runOnce { y = Random.Default.nextInt(1, Int.MAX_VALUE) }
        }

        interm3()
        val i1 = y
        interm2()
        /* well, it may actually randomly be equal, but the possibility is miniscule... */
        assertNotEquals(i1, y)
    }
}