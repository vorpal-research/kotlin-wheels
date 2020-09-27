package ru.spbstu.wheels

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ComparablesTest {
    private data class Foo(val s: String, val d: Int)

    @Test
    fun comparatorUse() {
        val cmp = compareBy<Foo> { it.s }.thenByDescending { it.d }

        cmp.use {
            assertTrue(Foo("a", 1) <= Foo("b", 2))
            assertTrue(Foo("", 2) < Foo("", 1))
            assertTrue(Foo("", 3) >= Foo("", 3))
            assertTrue(Foo("a", 1) <= Foo("a", 1))

            val lst = listOf(Foo("a", 1), Foo("a", 2), Foo("b", 0))
            assertEquals(listOf(Foo("a", 2), Foo("a", 1), Foo("b", 0)), lst.sorted())
            val mut = lst.toMutableList()
            mut.sort()
            assertEquals(listOf(Foo("a", 2), Foo("a", 1), Foo("b", 0)), mut)

            assertEquals(
                    Foo("z", -1),
                    listOf("z", "b", "z", "b", "c", "x").zip(-1..4, ::Foo).max()
            )
            assertEquals(
                    Foo("b", 2),
                    listOf("z", "b", "z", "b", "c", "x").zip(-1..4, ::Foo).min()
            )
        }
    }

    @Test
    fun combineCompares() {

        data class Test(val s: String, val d: Double, val i: Int): Comparable<Test> {
            override operator fun compareTo(other: Test) =
                    combineCompares(s.compareTo(other.s), d.compareTo(other.d), i.compareTo(other.i))
        }

        val tdata = listOf(
                Test("a", 1.0, 3),
                Test("a", 2.0, 3),
                Test("b", 3.0, 1),
                Test("b", 3.0, 2),
                Test("a", 2.0, 2)
        )

        assertEquals(
                listOf(
                        Test("a", 1.0, 3),
                        Test("a", 2.0, 2),
                        Test("a", 2.0, 3),
                        Test("b", 3.0, 1),
                        Test("b", 3.0, 2)
                ),
                tdata.sorted()
        )
    }

    @Test
    fun tupleSorts() {
        fun <T: Comparable<T>> testPair(a: T, b: T) {
            val pair = Pair(a, b)
            assertEquals(pair.toList().sorted(), pair.sorted().toList())
            val rpair = Pair(b, a)
            assertEquals(rpair.toList().sorted(), rpair.sorted().toList())
        }

        testPair(2, 3)
        testPair("a", "b")
        testPair(1, -1)
        testPair("caa", "cab")
        testPair(3.0, 2.0)
        testPair(1, 1)

        fun <T: Comparable<T>> testTriple(a: T, b: T, c: T) {
            val abc = Triple(a, b, c)
            assertEquals(abc.toList().sorted(), abc.sorted().toList())
            val bca = Triple(b, c, a)
            assertEquals(bca.toList().sorted(), bca.sorted().toList())
            val cba = Triple(c, b, a)
            assertEquals(cba.toList().sorted(), cba.sorted().toList())
        }

        testTriple(1, 2, 3)
        testTriple("aa", "ab", "aaa")
        testTriple(2.0, 3.0, 2.0)
        testTriple(2, 2, 2)
    }
}