package ru.spbstu.wheels

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StackTest {
    @Test
    fun simple() {
        val stack = stack<Int>()
        stack += 1..40

        assertFalse(stack.isEmpty())
        assertEquals(40, stack.top)
        assertEquals(40, stack.pop())
        assertEquals(39, stack.pop())
        for(i in 38 downTo 1) {
            assertFalse(stack.isEmpty())
            assertEquals(i, stack.pop())
        }

        assertFailsWith<NoSuchElementException> {
            stack.pop()
        }

        assertEquals(null, stack.top)
        assertTrue(stack.isEmpty())
    }

    @Test
    fun empty() {
        val stack = stack<String>()
        assertTrue(stack.isEmpty())
        assertFailsWith<NoSuchElementException> {
            stack.pop()
        }
        assertEquals(null, stack.top)

        stack.push("Hello")
        assertFalse(stack.isEmpty())
        assertEquals("Hello", stack.top)

        assertEquals("Hello", stack.pop())
        assertTrue(stack.isEmpty())
        assertFailsWith<NoSuchElementException> {
            stack.pop()
        }
        assertEquals(null, stack.top)
    }
}