package ru.spbstu.wheels

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SequencesTest {
    @Test
    fun testMemoize() {

        assertEquals(emptyList(), emptySequence<String>().memoize().toList())
        assertEquals((0..1).toList(), (0..1).asSequence().memoize().toList())
        assertEquals((0..1000).toList(), (0..1000).asSequence().memoize().toList())

        // check constrain once
        run {
            val seq = (0..1000).asSequence().constrainOnce()
            assertFailsWith<IllegalStateException> {
                seq.forEach { _ -> }
                seq.forEach { _ -> }
            }
        }

        run {
            val seq = (0..1000).asSequence().constrainOnce().memoize()
            seq.forEach { _ -> }
            seq.forEach { _ -> }
        }

        // infinite sequences
        run {
            val seq = generateSequence(0) { it + 3 }.memoize()

            assertEquals(seq.take(30).toList(), (0 until 90 step 3).toList())
            assertEquals(seq.take(40).toList(), (0 until 120 step 3).toList())
        }

    }

    @Test
    fun testProduct() {

        assertEquals(
                listOf(2, 3, 4, 3, 4, 5, 4, 5, 6),
                sequenceOf(1, 2, 3).product(sequenceOf(1, 2, 3), Int::plus).toList()
        )

        assertEquals(
                listOf(),
                sequenceOf(1, 2, 3).product(emptySequence<Int>(), Int::plus).toList()
        )

        assertEquals(
                listOf(),
                emptySequence<Int>().product(sequenceOf(1, 2, 3), Int::plus).toList()
        )

        assertEquals(
                listOf(
                        1 to 'a', 1 to 'b', 1 to 'c', 1 to 'd',
                        2 to 'a', 2 to 'b', 2 to 'c', 2 to 'd',
                        3 to 'a', 3 to 'b', 3 to 'c', 3 to 'd'
                ),
                ((1..3).asSequence() product ('a'..'d').asSequence()).toList()
        )

        // check reentrability
        val tseq = (1..3).asSequence() product ('a'..'d').asSequence()
        tseq.find { it.second == 'b' }
        tseq.find { it.second == 'c' }

        assertEquals(
                listOf(
                        1 to 'a', 1 to 'b', 1 to 'c', 1 to 'd',
                        2 to 'a', 2 to 'b', 2 to 'c', 2 to 'd',
                        3 to 'a', 3 to 'b', 3 to 'c', 3 to 'd'
                ),
                ((1..3).asSequence() product ('a'..'d')).toList()
        )
    }

    @Test
    fun testPeekSome() {
        val seq = 1..+Inf
        val (first10, drop10) = seq.peekSome(10)
        assertEquals((1..10).toList(), first10)
        val (second20, drop30) = drop10.peekSome(20)
        assertEquals((11..30).toList(), second20)
        val third30 = mutableListOf<Int>()
        val drop60 = drop30.peekSomeTo(30, third30)
        assertEquals((31..60).toList(), third30)
        val fourth30 = mutableSetOf<Int>()
        drop60.peekSomeTo(30, fourth30)
        assertEquals((61..90).toSet(), fourth30)
        // empty sequences
        val (lst, _) = emptySequence<Int>().peekSome(10)
        assertEquals(listOf(), lst)
        // too little sequences
        val mlst = mutableListOf<Int>()
        (0..5).asSequence().peekSomeTo(10, mlst)
        assertEquals((0..5).toList(), mlst)
    }

    @Test
    fun testPeekFirst() {
        val seq = 1..+Inf
        val (first, rest) = seq.peekFirst()
        assertEquals(1, first)

        assertEquals(listOf(2,3,4), rest.take(3).toList())

        assertFailsWith<NoSuchElementException> {
            emptySequence<String>().peekFirst()
        }

        assertEquals(null, emptySequence<String>().peekFirstOrNull().first)

    }

    @Test
    fun testPeekWhile() {
        // sequences
        run {
            val seq = (1..+Inf).constrainOnce()
            val (first10, drop10) = seq.peekWhile { it <= 10 }
            assertEquals((1..10).toList(), first10)
            val (second20, drop30) = drop10.peekWhile { it <= 30 }
            assertEquals((11..30).toList(), second20)
            val third30 = mutableListOf<Int>()
            val drop60 = drop30.peekWhileTo(third30) { it <= 60 }
            assertEquals((31..60).toList(), third30)
            val fourth30 = mutableSetOf<Int>()
            drop60.peekWhileTo(fourth30) { it <= 90 }
            assertEquals((61..90).toSet(), fourth30)
            // empty sequences
            val (lst, _) = emptySequence<Int>().peekWhile { it > 0 }
            assertEquals(listOf(), lst)
        }

        // iterators
        run {
            val seq = (1..+Inf).iterator()
            val (first10, drop10) = seq.peekWhile { it <= 10 }
            assertEquals((1..10).toList(), first10)
            val (second20, drop30) = drop10.peekWhile { it <= 30 }
            assertEquals((11..30).toList(), second20)
            val third30 = mutableListOf<Int>()
            val drop60 = drop30.peekWhileTo(third30) { it <= 60 }
            assertEquals((31..60).toList(), third30)
            val fourth30 = mutableSetOf<Int>()
            drop60.peekWhileTo(fourth30) { it <= 90 }
            assertEquals((61..90).toSet(), fourth30)
            // empty sequences
            val (lst, _) = emptySequence<Int>().iterator().peekWhile { it > 0 }
            assertEquals(listOf(), lst)
        }
    }

    @Test
    fun testIntersperse() {
        assertEquals(
                listOf(0, 2, 1, 1, 2, 0, 3, -1, 4, -2, 5),
                intersperse(0..+Inf, (2 downTo -2).asSequence()).toList()
        )

        assertEquals(
                listOf(0, 2, 10, 1, 1, 11, 2, 0, 12),
                intersperse(
                        (0..2).asSequence(),
                        (2 downTo -2).asSequence(),
                        (10..12).asSequence()
                ).toList()
        )

        assertFailsWith<IllegalArgumentException> { intersperse<Int>() }

        assertEquals(
                listOf(),
                intersperse(
                        emptySequence(),
                        0..+Inf
                ).toList()
        )

        assertEquals(
                listOf(0),
                intersperse(
                        0..+Inf,
                        emptySequence()
                ).toList()
        )

    }
    
}
