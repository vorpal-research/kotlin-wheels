package ru.spbstu.wheels

import org.junit.Test
import kotlin.test.assertEquals

class TArrayTest {
    @Test
    fun simple() {
        val tarr: TArray<String> = TArray(20)

        tarr[10] = "Hello"
        tarr[15] = "World"

        assertEquals("Hello", tarr[10])
        assertEquals("World", tarr[15])

        assertEquals(tarr.size, 20)
    }

    @Test
    fun secondary() {
        val tarr: TArray<String> = TArray(20) { "$it" }

        assertEquals("10", tarr[10])
        assertEquals("15", tarr[15])
        assertEquals("0", tarr[0])

        assertEquals(tarr.size, 20)
    }

    @Test
    fun iterator() {
        val tarr: TArray<String> = TArray(20) { "$it" }

        for((i, e) in tarr.iterator().asSequence().withIndex()) {
            assertEquals("$i", e)
        }
    }

    @Test
    fun tarrayOf() {
        val tarr = tarrayOf(1, 2, 3, 4)
        assertEquals(listOf(1, 2, 3, 4), tarr.asList())
    }

    @Test
    fun asList() {
        val tarr: TArray<String> = TArray(20) { "$it" }

        for((i, e) in tarr.asList().withIndex()) {
            assertEquals("$i", e)
        }
    }

    @Test
    fun copying() {
        val tarr: TArray<Int> = TArray(20) { it }

        val tarr2: TArray<Int> = TArray(10)
        tarr.copyInto(tarr2, 0, 5, 15)

        assertEquals((5..14).toList<Int?>(), tarr2.asList())

        val tarr3 = tarr.copyOf(12)

        assertEquals((0..11).toList<Int?>(), tarr3.asList())
    }

    @Test
    fun toStringy() {
        val tarr = TArray(10) { it }
        assertEquals("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]", "$tarr")

        val emptyArr = TArray<Int>(0)
        assertEquals("[]", "$emptyArr")
    }
}
