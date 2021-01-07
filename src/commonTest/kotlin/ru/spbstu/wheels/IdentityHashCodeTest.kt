package ru.spbstu.wheels

import kotlin.test.Test
import kotlin.test.assertEquals

class IdentityHashCodeTest {
    @Test
    fun smokeTest() {
        val one = Pair("Hello", 4.13)
        val hash1 = identityHashCode(one)
        val hash2 = identityHashCode(one)
        assertEquals(hash1, hash2)

        val lst = mutableListOf<Int>()
        val lhash1 = identityHashCode(lst)
        lst.add(2)
        val lhash2 = identityHashCode(lst)
        assertEquals(lhash1, lhash2)
    }
}
