package ru.spbstu.wheels

import org.junit.Test
import kotlin.test.assertEquals

class Hash {

    @Test
    fun setHashCode() {
        assertEquals(emptySet<String>().hashCode(), setHashCode(emptyList<String>()))
        assertEquals((0..2000).toSet().hashCode(), setHashCode(0..2000))
    }

    @Test
    fun orderedHashCode() {
        assertEquals(emptyList<String>().hashCode(), orderedHashCode(emptyList<String>()))
        assertEquals((0..2000).toList().hashCode(), orderedHashCode(0..2000))
    }


}