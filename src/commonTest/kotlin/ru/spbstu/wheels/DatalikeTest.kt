package ru.spbstu.wheels

import kotlin.test.Test
import kotlin.test.assertEquals

private object Us {
    class Moo(val x: Int, val y: String, val z: Set<Moo>): Datalike {
        override fun equals(other: Any?): Boolean = defaultEquals(other, Moo::x, Moo::y, Moo::z)
        override fun hashCode(): Int = defaultHashCode(::x, ::y, ::z)
        override fun toString(): String = toRecordString(Moo::x, Moo::y, Moo::z)
    }
}

private object Them {
    data class Moo(val x: Int, val y: String, val z: Set<Moo>)
}

class DatalikeTest {
    @Test
    fun smokeTest() {
        val moo1 = Us.Moo(2, "Hello", setOf(Us.Moo(3, "II", setOf())))
        val moo2 = Them.Moo(2, "Hello", setOf(Them.Moo(3, "II", setOf())))

        assertEquals(moo2.hashCode(), moo1.hashCode())
        assertEquals(moo2.toString(), moo1.toString())
    }
}