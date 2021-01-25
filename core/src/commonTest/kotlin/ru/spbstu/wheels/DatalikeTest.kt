package ru.spbstu.wheels

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private class Moo(val x: Int, val y: String, val z: Set<Moo>) {
    override fun equals(other: Any?): Boolean = defaultEquals(other, Moo::x, Moo::y, Moo::z)
    override fun hashCode(): Int = defaultHashCode(::x, ::y, ::z)
    override fun toString(): String = toTupleString(Moo::x, Moo::y, Moo::z)
}

private data class Moo2(val x: Int, val y: String, val z: Set<Moo2>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Moo2) return false

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }
}

class DatalikeTest {
    @Test
    fun smokeTest() {
        val moo1 = Moo(2, "Hello", setOf())
        val moo2 = Moo2(2, "Hello", setOf())

        assertEquals(moo1.hashCode(), moo2.hashCode())
    }
}