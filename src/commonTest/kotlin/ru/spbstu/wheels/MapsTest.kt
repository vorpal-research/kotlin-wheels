package ru.spbstu.wheels

import kotlin.test.Test
import kotlin.test.*

class MapsTest {
    @Test
    fun simpleEntry() {
        val map = ('a'..'z').withIndex().map { (i, c) -> "$c" to i }.toMap()

        // contains for map.entries works weirdly on native, see
        // https://youtrack.jetbrains.com/issue/KT-42428
        assertTrue(SimpleEntry("h", 7) in map.entries.toSet())
        assertFalse(SimpleEntry("a", 5) in map.entries.toSet())

        val actualEntry = map.entries.find { it.key == "h" }
        val ourEntry = SimpleEntry("h", 7)
        assertEquals(actualEntry, ourEntry)
        assertTrue(ourEntry == actualEntry)
        assertEquals("$actualEntry", "$ourEntry")
        assertEquals(actualEntry.hashCode(), ourEntry.hashCode())
    }

    @Test
    fun getEntry() {
        val map = ('a'..'z').withIndex().map { (i, c) -> "$c" to i }.toMap()

        assertEquals("h", map.getEntry("h")?.key)
        assertEquals(null, map.getEntry("bonanza"))

        val map2 = map + ("h" to null)
        assertNotNull(map2.getEntry("h"))
        assertEquals("h" to null, map2.getEntry("h")?.toPair())
    }

    @Test
    fun getOption() {
        val map = ('a'..'z').withIndex().map { (i, c) -> "$c" to i }.toMap()

        assertEquals(Option.just(7), map.getOption("h"))
        assertEquals(Option.empty(), map.getOption("bonanza"))

        val map2 = map + ("h" to null)
        assertNotEquals(Option.empty(), map2.getOption("h"))
        assertEquals(Option.just(null), map2.getOption("h"))
    }

    @Test
    fun joinTo() {

        val m0 = mapOf<Int, String>()

        assertEquals(m0.entries.joinToString(), m0.joinToString())

        val m1 = mapOf("Hello" to 1.0, "World" to 3.0)

        assertEquals(m1.entries.joinToString(), m1.joinToString())
        assertEquals(
                m1.entries.joinToString(prefix = "<<", postfix = ">>", separator = ";"),
                m1.joinToString(prefix = "<<", postfix = ">>", separator = ";")
        )

        val m2 = (0..100).map { it to "y - x${it + 1}" }.toMap()

        assertEquals(m2.entries.joinToString(), m2.joinToString())
        assertEquals(
                m2.entries.joinToString(prefix = "<<", postfix = ">>", separator = ";"),
                m2.joinToString(prefix = "<<", postfix = ">>", separator = ";")
        )
        assertEquals(
                m2.entries.joinToString(prefix = "<<", postfix = ">>", separator = ";", limit = 2),
                m2.joinToString(prefix = "<<", postfix = ">>", separator = ";", limit = 2)
        )
        assertEquals(
                m2.entries.joinToString(prefix = "<<", postfix = ">>", separator = ";", limit = 2) { (k, v) -> "$v[$k]" },
                m2.joinToString(prefix = "<<", postfix = ">>", separator = ";", limit = 2) { k, v -> "$v[$k]" }
        )
    }
}