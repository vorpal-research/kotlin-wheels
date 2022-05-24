package ru.spbstu.wheels

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

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

        run {
            val l1 = listOf(1, 2, 3)
            val l2 = listOf(4, 5, 6)
            val l3 = mutableListOf<Pair<Int, Int>>()

            val l3copy = l1.productTo(l2, l3)

            assertSame(l3, l3copy)
            assertEquals(
                listOf(
                    1 to 4, 1 to 5, 1 to 6,
                    2 to 4, 2 to 5, 2 to 6,
                    3 to 4, 3 to 5, 3 to 6
                ),
                l3
            )
        }

        run {
            val base = listOf(listOf(1, 2, 3), listOf(4, 5, 6))
            val p = base.product()

            assertEquals(
                listOf(
                    listOf(1, 4), listOf(1, 5), listOf(1, 6),
                    listOf(2, 4), listOf(2, 5), listOf(2, 6),
                    listOf(3, 4), listOf(3, 5), listOf(3, 6),
                ),
                p
            )
        }

        run {
            val base = listOf(listOf(1, 2), listOf(4, 5), listOf(7, 8))
            val p = base.product()

            assertEquals(
                listOf(
                    listOf(1, 4, 7), listOf(1, 4, 8),
                    listOf(1, 5, 7), listOf(1, 5, 8),
                    listOf(2, 4, 7), listOf(2, 4, 8),
                    listOf(2, 5, 7), listOf(2, 5, 8),
                ),
                p
            )
        }
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

    operator fun <T> MutableList<T>.get(range: IntRange) = subList(range.start, range.endInclusive + 1)
    operator fun <T> MutableList<T>.get(range: IntInfiniteRangeAfter) = subList(range.from, size)
    operator fun <T> MutableList<T>.set(range: IntRange, collection: Collection<T>) =
        subList(range.start, range.endInclusive + 1).assign(collection)
    operator fun <T> MutableList<T>.get(range: IntInfiniteRangeAfter, collection: Collection<T>) =
        subList(range.from, size).assign(collection)

    @Test
    fun slices() {
        val lst = (1..220).toMutableList()
        lst[14..45] = listOf(1,2,3)
        lst[70..Inf].clear()
        assertEquals((1..14) + (1..3) + (47..99), lst)
        lst[69..69] = listOf(99, 100, 101)
        assertEquals((1..14) + (1..3) + (47..101), lst)
    }
}