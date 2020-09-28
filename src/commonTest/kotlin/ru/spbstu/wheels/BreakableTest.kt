package ru.spbstu.wheels

import kotlin.test.Test
import kotlin.test.assertEquals

class BreakableTest {
    @Test
    fun smokeTest() {

        var i = 0
        repeatB(15) {
            ++i
            if (i > 10) break_
            if (i > 0) continue_
            ++i
        }
        assertEquals(11, i)
        val mut = mutableListOf<Int>()
        (1..300).forEachB {
            mut += it
            if (it > 200) break_
        }
        assertEquals((1..201).toList(), mut)

    }

    @Test
    fun mapTest() {

        val actual = (0..300).mapOrBreak {
            if (it % 3 == 0) continue_
            if (it == 200) break_
            "$it"
        }

        assertEquals(
                (0..300).take(200).filter { it % 3 != 0 }.map { "$it" },
                actual
        )

        val actualS = (0..300).asSequence().mapOrBreak {
            if (it % 3 == 0) continue_
            if (it == 200) break_
            "$it"
        }

        assertEquals(
                (0..300).take(200).filter { it % 3 != 0 }.map { "$it" },
                actualS.toList()
        )

    }
}
