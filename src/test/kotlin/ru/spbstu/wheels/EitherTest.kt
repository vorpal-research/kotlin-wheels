package ru.spbstu.wheels

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EitherTest {

    // fine if compiles
    fun <T> checkSubtypeOf(v: T) {}

    @Test
    fun simple() {
        val ei = Either.left(listOf(2)) ?: Either.right(setOf(""))

        checkSubtypeOf<Collection<Any>>(ei.value)

        assertTrue(ei.isLeft())
        assertEquals(Option.just(listOf(2)), ei.leftOption)
        assertEquals(Option.empty(), ei.rightOption)
        assertEquals(2, ei.rightOr { 2 })

        assertEquals(Either.left("2"), ei.mapRight { 2 }.mapLeft { it.joinToString("") })
    }


    @Test
    fun visit() {
        val ei: Either<Int, String> = if(false) Either.left(2) else Either.right("Hello")

        assertEquals(5, ei.visit(onLeft = { it * 8 }, onRight = { it.length }))

        val ei2: Either<Int, String> = if(true) Either.left(2) else Either.right("Hello")

        assertEquals(16, ei2.visit(onLeft = { it * 8 }, onRight = { it.length }))
    }

}