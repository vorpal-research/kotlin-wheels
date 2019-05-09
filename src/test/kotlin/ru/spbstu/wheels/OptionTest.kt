package ru.spbstu.wheels

import org.junit.Test
import java.lang.IllegalStateException
import kotlin.test.*

class OptionTest {
    @Test
    fun smokeTest() {
        assertTrue(Option.just(2).isNotEmpty())
        assertTrue(Option.empty<String>().isEmpty())

        val nullString: String? = null
        // nulls are valid values
        assertNotEquals(Option.ofNullable(nullString), Option.just(nullString))
        assertTrue(Option.ofNullable(nullString).isEmpty())
        assertFalse(Option.just(nullString).isEmpty())
    }

    @Test
    fun gets() {
        val nullString: String? = null

        assertFailsWith<IllegalStateException> {
            Option.ofNullable(nullString).get()
        }

        // but:
        assertEquals(null, Option.just(nullString).get())

        assertEquals(null, Option.empty<String>().getOrNull())
        assertEquals(2, Option.just(2).getOrNull())

        assertEquals(3, Option.empty<Int>().getOrElse { 3 })
        assertEquals(2, Option.just(2).getOrElse { 3 })
    }

    @Test
    fun map() {
        assertEquals("23", Option.just(20).map { it + 3 }.map { "$it" }.getOrNull())
        assertEquals(Option.empty(), Option.empty<Int>().map { it + 3 }.map { "$it" })
    }

    @Test
    fun flatMap() {
        val o2 = Option.just(2)
        val o3 = Option.just(3)
        val oe = Option.empty<Int>()

        assertEquals(Option.just(5), o2.flatMap { v2 -> o3.flatMap { v3 -> Option.just(v2 + v3) } })
        assertEquals(Option.empty(),
                o2.flatMap { v2 -> o3.flatMap { v3 -> oe.flatMap { ve -> Option.just(v2 + v3 * ve) } } }
        )
    }

    @Test
    fun flatten() {
        assertEquals(Option.empty(), Option.just(Option.just(Option.just(Option.empty<Int>()))).flatten().flatten().flatten())
        assertEquals(Option.just(2), Option.just(Option.just(Option.just(Option.just(2)))).flatten().flatten().flatten())
    }

    @Test
    fun filter() {
        assertEquals(Option.empty(), Option.just(40).filter { it < 30 })
        assertEquals(Option.just(40), Option.just(40).filter { it > 30 })
    }

    @Test
    fun zip() {
        assertEquals(Option.just(30), Option.just(10).zip(Option.just(20)) { a, b -> a + b })
        assertEquals(Option.empty(), Option.just(10).zip(Option.empty<Int>()) { a, b -> a + b })

        assertEquals(Option.just("aaaa"), Option.just("a").zip(Option.just(4)) { s, i -> s.repeat(i) })

        assertEquals(Option.just(2 to '3'), Option.just(2).zip(Option.just('3')))
        assertEquals(Option.empty(), Option.just(3).zip(Option.empty<Char>()))
    }

    @Test
    fun zip3() {
        assertEquals(
                Option.just(30),
                zip3(Option.just(15), Option.just(10), Option.just(5)) { a, b, c -> a + b + c }
        )
        assertEquals(Option.empty(),
                zip3(Option.just(15), Option.empty<Int>(), Option.just(5)) { a, b, c -> a + b + c }
        )

        assertEquals(
                Option.just(Triple(1.0, "Hello", 3)),
                zip3(Option.just(1.0), Option.just("Hello"), Option.just(3))
        )
        assertEquals(
                Option.empty(),
                zip3(Option.just(1.0), Option.empty<String>(), Option.just(3))
        )
    }

    @Test
    fun nextOption() {
        assertEquals(Option.empty(), listOf<Int>().iterator().nextOption())
        assertEquals(Option.just(2), listOf(2,3,4).iterator().nextOption())
    }

    private data class Delegated(val opt: Option<String>) {
        val value by opt
    }

    @Test
    fun optionDelegate() {
        assertEquals("3", Delegated(Option.just("3")).value)
        assertFailsWith<IllegalStateException> {
            Delegated(Option.empty()).value
        }
    }

    @Test
    fun toStringTest() {
        assertEquals("Option.just(3)", Option.just(3).toString())
        assertEquals("Option.empty()", Option.empty<Any?>().toString())
    }
}