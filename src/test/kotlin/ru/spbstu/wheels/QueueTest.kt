package ru.spbstu.wheels

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class QueueTest {
    @Test
    fun simple() {
        val que = queue<Int>()
        que += 1..40

        assertFalse(que.isEmpty())
        assertEquals(1, que.current)
        assertEquals(1, que.take())
        assertEquals(2, que.take())
        for(i in 3..40) {
            assertFalse(que.isEmpty())
            assertEquals(i, que.take())
        }

        assertFailsWith<NoSuchElementException> {
            que.take()
        }

        assertEquals(null, que.current)
        assertTrue(que.isEmpty())
    }

    @Test
    fun empty() {
        val que = queue<String>()
        assertTrue(que.isEmpty())
        assertFailsWith<NoSuchElementException> {
            que.take()
        }
        assertEquals(null, que.current)

        que.put("Hello")
        assertFalse(que.isEmpty())
        assertEquals("Hello", que.current)

        assertEquals("Hello", que.take())
        assertTrue(que.isEmpty())
        assertFailsWith<NoSuchElementException> {
            que.take()
        }
        assertEquals(null, que.current)
    }

    @Test
    fun mixing() {
        val que = queue<Int>()

        que += 0..10

        val elems = (0..5).map { que.take() }
        assertEquals((0..5).toList(), elems)

        que += 11..40

        val elems2 = (0..25).map { que.take() }
        assertEquals((6..31).toList(), elems2)

        que += 41..500

        val allElems = generateSequence { if(que.isEmpty()) null else que.take() }.toList()

        assertEquals((32..500).toList(), allElems)
        assertTrue(que.isEmpty())
    }

    @Test
    fun mixingSmall() { // the point is doing mixed operations _without_ array reallocation
        val que = queue<Int>()

        assertTrue(que.isEmpty())

        que += 0..10

        assertEquals(11, que.size)

        val elems = (0..5).map { que.take() }
        assertEquals((0..5).toList(), elems)

        que += (11..14).asSequence()

        val elems2 = (0..8).map { que.take() }
        assertEquals((6..14).toList(), elems2)
    }

    @Test
    fun mixingDeque() {
        val que = deque<Int>()

        for (i in 0..49) {
            que.putFirst(i)
            que.putLast(i + 50)
        }

        assertEquals(100, que.size)

        for (i in 49 downTo 0) {
            assertEquals(i, que.first)
            assertEquals(i, que.takeFirst())
        }
        for (i in 50..99) {
            assertEquals(i, que.first)
            assertEquals(i, que.takeFirst())
        }
    }
}