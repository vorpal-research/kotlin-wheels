package ru.spbstu.wheels

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EitherTest {
    @Test
    fun simple() {
        val ei = Either.left(listOf(2)) ?: Either.right(setOf(""))

        fun checkExactType(c: Collection<Any>) {}

        checkExactType(ei.value)

        assertTrue(ei.isLeft())
        assertEquals(Option.just(listOf(2)), ei.leftOption)
        assertEquals(Option.empty(), ei.rightOption)
        assertEquals(2, ei.rightOr { 2 })

        assertEquals(Either.left("2"), ei.mapRight { 2 }.mapLeft { it.joinToString("") })
    }
}