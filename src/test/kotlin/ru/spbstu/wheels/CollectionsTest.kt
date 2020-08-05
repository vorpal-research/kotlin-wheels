package ru.spbstu.wheels

import org.junit.Test
import kotlin.test.assertEquals

class CollectionsTest {
    @Test
    fun product() {

        assertEquals(
                listOf<Int>(),
                listOf<Int>().product(0..4) { a, b -> a * b }
        )

        assertEquals(
                listOf<Int>(),
                (0..4).product(listOf<Int>()) { a, b -> a * b }
        )

        assertEquals(
                listOf(0, 0, 0, 0, 0, 1, 2, 3, 0, 2, 4, 6),
                (0..2).product(0..3) { a, b -> a * b }
        )

        assertEquals(
                listOf("", "", "", "a", "b", "c", "aa", "bb", "cc"),
                (0..2).product(('a'..'c').map { "$it" }) { i, s -> s.repeat(i) }
        )

        assertEquals(
                (0..2).toList(),
                (1..1).product(0..2) { a, b -> a * b }
        )

        assertEquals(
                listOf(
                        0 to 'a', 0 to 'b', 0 to 'c',
                        1 to 'a', 1 to 'b', 1 to 'c',
                        2 to 'a', 2 to 'b', 2 to 'c'
                ),
                (0..2).product('a'..'c')
        )

        val lst1 = (0..200).toList()
        val lst2 = ('a'..'z').map { "$it" }

        assertEquals(
                lst1.flatMap { a -> lst2.map { b -> a to b } },
                lst1 product lst2
        )

        assertEquals(
                lst1.flatMap { a -> lst2.map { b -> b + a } },
                lst1.product(lst2) { a, b -> b + a }
        )

        assertEquals(lst1.size * lst2.size, (lst1 product lst2).size)
        assertEquals(
                (lst1 product lst2).toSet(),
                (lst2 product lst1).map { (a, b) -> b to a }.toSet()
        )
    }

    @Test
    fun resize() {
        val lst = (1..220).toMutableList()
        assertEquals(lst.size, 220)
        lst.resize(10) { 0 }
        assertEquals((1..10).toList(), lst)
        lst.resize(20) { it + 1 }
        assertEquals((1..20).toList(), lst)
        lst.resize(0) { 0 }
        assertEquals(listOf<Int>(), lst)
        lst.resize(12) { it }
        assertEquals(List(12) { it }, lst)
    }
}