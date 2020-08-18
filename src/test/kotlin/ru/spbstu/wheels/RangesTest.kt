package ru.spbstu.wheels

import org.junit.Assert
import org.junit.Test
import java.lang.IllegalArgumentException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RangesTest {
    @Test
    fun positiveInf() {
        assertTrue(listOf("a", 2, 'z').all { it in -Inf..+Inf })

        assertEquals((0..20).toList(), (0..+Inf).take(21).toList())
        assertEquals((0L..20L).toList(), (0L..+Inf).take(21).toList())

        assertEquals(listOf(0, 5, 10, 15), (0..+Inf step 5).take(4).toList())
        assertEquals(listOf<Long>(0, 5, 10, 15), (0L..+Inf step 5).take(4).toList())

        assertTrue(3 in 0..+Inf)
        assertTrue(Int.MAX_VALUE in 0..+Inf)
        assertFalse(-1 in 0..+Inf)

        assertTrue(3L in 0L..+Inf)
        assertTrue(Long.MAX_VALUE in 0L..+Inf)
        assertFalse(-1L in 0L..+Inf)

        assertFalse("a" in "z"..+Inf)
        assertTrue("z" in "a"..+Inf)
    }

    @Test
    fun negativeInf() {

        assertEquals((0 downTo -20).toList(), (0 downTo -Inf).take(21).toList())
        assertEquals((0L downTo -20L).toList(), (0L downTo -Inf).take(21).toList())

        assertEquals(listOf(0, -5, -10, -15), (0 downTo -Inf step 5).take(4).toList())
        assertEquals(listOf<Long>(0, -5, -10, -15), (0L downTo -Inf step 5).take(4).toList())

        assertTrue(0 in -Inf..3)
        assertTrue(Int.MIN_VALUE in -Inf..0)
        assertFalse(1 in -Inf..0)

        assertTrue(0L in -Inf..3L)
        assertTrue(Long.MIN_VALUE in -Inf..0L)
        assertFalse(1L in -Inf..0L)

        assertFalse("z" in -Inf.."a")
        assertTrue("a" in -Inf.."z")

    }

    @Test
    fun toStringTest() {
        assertEquals("-Inf", (-Inf).toString())
        assertEquals("+Inf", Inf.toString())

        assertEquals("-Inf..+Inf", (-Inf..+Inf).toString())
        assertEquals("0..+Inf", (0..+Inf).toString())
        assertEquals("0..+Inf step 2", (0..+Inf step 2).toString())
        assertEquals("-Inf..0", (-Inf..0).toString())

        assertEquals("0 downTo -Inf", (0 downTo -Inf).toString())
        assertEquals("0 downTo -Inf step 2", (0 downTo -Inf step 2).toString())

        assertEquals("0..+Inf", (0L..+Inf).toString())
        assertEquals("0..+Inf step 2", (0L..+Inf step 2).toString())
        assertEquals("-Inf..0", (-Inf..0L).toString())

        assertEquals("0 downTo -Inf", (0L downTo -Inf).toString())
        assertEquals("0 downTo -Inf step 2", (0L downTo -Inf step 2).toString())

        assertEquals("a..+Inf", ("a"..+Inf).toString())
        assertEquals("-Inf..a", (-Inf.."a").toString())
    }

    @Test
    fun sliceTest() {
        assertEquals((0..40).toList(), (0..40).toList().slice(0..+Inf))
        assertEquals((4..40).toList(), (0..40).toList().slice(4..+Inf))

        assertEquals((0..40).toList(), (0..40).toList().slice(-Inf..40))
        assertEquals((0..14).toList(), (0..40).toList().slice(-Inf..14))

        // empty lists
        assertFailsWith<IndexOutOfBoundsException> {
            emptyList<Int>().slice(0..+Inf)
        }
        assertFailsWith<IndexOutOfBoundsException> {
            emptyList<Int>().slice(-Inf..0)
        }

        assertFailsWith<IndexOutOfBoundsException> {
            listOf(1, 2, 3).slice(4..+Inf)
        }
        assertFailsWith<IndexOutOfBoundsException> {
            listOf(1, 2, 3).slice(-Inf..4)
        }

        assertFailsWith<IllegalArgumentException> {
            (0..50).toList().slice(-4..+Inf)
        }
        assertFailsWith<IllegalArgumentException> {
            (0..50).toList().slice(-Inf..-4)
        }
    }



    @Test
    fun toArrayTest() {
        fun assertProgression(progression: IntProgression) {
            assertEquals(progression.toList().size, progression.size)
            assertEquals(progression.toList(), progression.toTypedArray().asList())
            assertEquals(progression.toList(), progression.toIntArray().asList())

            val longVariant =
                    LongProgression.fromClosedRange(
                            progression.first.toLong(),
                            progression.last.toLong(),
                            progression.step.toLong()
                    )
            assertEquals(longVariant.toList().size, longVariant.size)
            assertEquals(longVariant.toList(), longVariant.toTypedArray().asList())
            assertEquals(longVariant.toList(), longVariant.toLongArray().asList())

            val charVariant =
                    CharProgression.fromClosedRange(
                            'a' + progression.first,
                            'a' + progression.last,
                            progression.step
                    )

            assertEquals(charVariant.toList().size, charVariant.size)
            assertEquals(charVariant.toList(), charVariant.toTypedArray().asList())
            assertEquals(charVariant.toList(), charVariant.toCharArray().asList())
        }

        assertProgression(0..0)
        assertProgression(0..1)
        assertProgression(18..56)
        assertProgression(-2 until 10)
        assertProgression(10 downTo -2)
        assertProgression(-10..10 step 2)
        assertProgression(10 downTo -10 step 2)
        assertProgression(-10..10 step 3)
        assertProgression(10 downTo -10 step 3)

        assertEquals("abc", ('a'..'c').charsToString())
        assertEquals("036", ('0'..'8' step 3).charsToString())
    }
}