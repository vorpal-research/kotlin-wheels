package ru.spbstu.wheels

import org.junit.Test
import kotlin.test.*

class MapsTest {
    @Test
    fun simpleEntry() {
        val map = ('a'..'z').withIndex().map { (i, c) -> "$c" to i }.toMap()

        assertTrue(SimpleEntry("h", 7) in map.entries)
        assertFalse(SimpleEntry("a", 5) in map.entries)

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
}