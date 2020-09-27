package ru.spbstu.wheels

import kotlin.test.Test
import kotlin.test.assertEquals

class HashTest {

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