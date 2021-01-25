package ru.spbstu.wheels

import kotlin.test.Test
import kotlin.test.assertEquals

class MDMapsTest {

    @Test
    fun smokeTest() {
        val mm = MapToList<Int, String>()

        mm[1].add("a")
        mm[1].add("b")
        mm[2].add("n")

        assertEquals(listOf("a", "b"), mm[1])
        assertEquals(listOf("n"), mm[2])

        val mm2 = MDMap.withDefault<Int, Int> { 0 }

        mm2[1] += 3
        mm2[2]++
        mm2[1]--

        assertEquals(2, mm2[1])
        assertEquals(1, mm2[2])

    }

}