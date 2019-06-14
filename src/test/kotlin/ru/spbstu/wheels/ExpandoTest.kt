package ru.spbstu.wheels

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

data class Foo(val i: Int): Expando() {
    override fun toString(): String {
        return "{i=$i, $expansion}"
    }
}

val Foo.s: String by Expando.lazy { "" }
var Foo.ss: String by Expando
var Foo.sss: String by Expando

class ExpandoTest {
    @Test
    fun testSimple() {
        val foo = Foo(2)
        assertEquals("", foo.s)
        foo.ss = "Hello"
        assertEquals("Hello", foo.ss)
        assertFailsWith<IllegalStateException> {
            foo.sss
        }
        foo.sss = "World"
        assertEquals("World", foo.sss)
    }

    @Test
    fun testToString() {

        val foo = Foo(2)

        assertEquals("{i=2, }", "$foo")

        foo.ss = "a"
        foo.s

        assertEquals("{i=2, ss=a, s=}", "$foo")
    }
}