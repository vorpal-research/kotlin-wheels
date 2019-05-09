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

}
