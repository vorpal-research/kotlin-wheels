package ru.spbstu.wheels

import kotlinx.warnings.Warnings
import kotlin.test.Test
import kotlin.test.*

@Suppress(Warnings.CAST_NEVER_SUCCEEDS, Warnings.UNREACHABLE_CODE, Warnings.USELESS_ELVIS)
class TryTest {
    @Test
    fun tryEx() {

        assertTrue(
                tryEx { 2 as String }.isException()
        )
        assertFalse(
                tryEx { 2 as String }.isNotException()
        )
        assertFalse(
                tryEx { listOf(1,2,3) }.isException()
        )
        assertTrue(
                tryEx { listOf(1,2,3) }.isNotException()
        )

    }

    @Test
    fun get() {

        val ex: Try<Int> = tryEx { (null as Int?)!! }
        val ok: Try<String> = tryEx { "${2}" }

        assertEquals(null, ex.getOrNull())
        assertEquals("2", ok.getOrNull())

        assertFailsWith<NullPointerException> {
            ex.getOrThrow()
        }
        assertEquals("2", ok.getOrThrow())

        assertTrue(ex.getExceptionOrNull() is NullPointerException)
        assertEquals(null, ok.getExceptionOrNull())

        assertEquals("2", ok.getOrElse { "3" })
        assertEquals(3, ex.getOrElse { 3 })

    }

    @Test
    fun map() {
        val ex = tryEx { (null as String?)!! }
        val ok = tryEx { "${2}" }

        assertEquals(Try.just(2), ok.map { it.toInt() })
        assertTrue(ex.map { it.toInt() }.isException())
        assertEquals(ex.getExceptionOrNull(), ex.map { it.toInt() }.getExceptionOrNull())

        val failedMap = tryEx { "Hello" }.map { it.toInt() }
        assertTrue(failedMap.getExceptionOrNull() is NumberFormatException)
    }

    @Test
    fun flatMap() {
        val ex = tryEx { (null as String?)!! }
        val ok = tryEx { "${2}" }

        assertEquals(Try.just(2), ok.flatMap { tryEx { it.toInt() } })
        assertTrue(ex.flatMap { tryEx { it.toInt() } }.isException())
        assertEquals(ex.getExceptionOrNull(), ex.flatMap { tryEx { it.toInt() } }.getExceptionOrNull())

        val failedMap = tryEx { "Hello" }.flatMap { it.toInt().let { Try.just(it) } }
        assertTrue(failedMap.getExceptionOrNull() is NumberFormatException)
    }

    @Test
    fun recover() {
        val recovered = tryEx { "Hello" }.map { it.toInt() }.recover { Try.just(it) }
        assertEquals(null, recovered.getExceptionOrNull())
        assertTrue(recovered.getOrNull() is NumberFormatException)

        val recoveredToEx = tryEx { "Hello" }.map { it.toInt() }.recover { throw NullPointerException() }
        assertNotEquals(null, recoveredToEx.getExceptionOrNull())
        assertEquals(null, recoveredToEx.getOrNull())
        assertTrue(recoveredToEx.getExceptionOrNull() is NullPointerException)

        val recoveredToEx2 = tryEx { "Hello" }.map { it.toInt() }.recover { Try.exception(NullPointerException()) }
        assertNotEquals(null, recoveredToEx2.getExceptionOrNull())
        assertEquals(null, recoveredToEx2.getOrNull())
        assertTrue(recoveredToEx2.getExceptionOrNull() is NullPointerException)
    }

    @Test
    fun catch() {
        val ok = tryEx { "${2}" }
        val ex1 = tryEx { (null as? String)!! }
        val ex2 = tryEx { "Hello".toInt().toString() }

        assertEquals(ok, ok.catch<Exception> { "a" })
        assertEquals(
                Try.just("Hello"),
                ex1.catch<NumberFormatException> { "World" }
                        .catch<NullPointerException> { "Hello" }
        )
        assertEquals(
                Try.just("World"),
                ex2.catch<NumberFormatException> { "World" }
                        .catch<NullPointerException> { "Hello" }
        )

        assertEquals(
                Try.just("Raw"),
                ex2.catch<Exception> { "Raw" }
                        .catch<NumberFormatException> { "World" }
                        .catch<NullPointerException> { "Hello" }
        )

        assertTrue(
                ex1.catch<NullPointerException> { "Hello".toInt().toString() }.isException()
        )

        assertEquals(
                Try.just("Raw"),
                ex1.catch<NullPointerException> { "Hello".toInt().toString() } // this throws
                        .catch<NumberFormatException> { "Raw" } // this catches
        )
    }

    @Test
    fun flatten() {
        assertEquals(Try.just(2), tryEx { tryEx { tryEx { 2 }}}.flatten().flatten())
        val nested = tryEx { tryEx { null!! }}
        assertFalse(nested.isException())
        assertTrue(nested.flatten().isException())
        assertTrue(nested.flatten().getExceptionOrNull() is NullPointerException)
    }

    @Test
    fun require() {
        assertTrue(tryEx { 2 }.require { it < 0 }.getExceptionOrNull() is IllegalArgumentException)
        assertTrue(tryEx { null!! ?: 0 }.require { it < 0 }.getExceptionOrNull() is NullPointerException)

        assertEquals(tryEx { 2 }, tryEx { 2 }.require { it > 0 })
    }

    @Test
    fun check() {
        assertTrue(tryEx { 2 }.check { it < 0 }.getExceptionOrNull() is IllegalStateException)
        assertTrue(tryEx { null!! ?: 0 }.check { it < 0 }.getExceptionOrNull() is NullPointerException)

        assertEquals(tryEx { 2 }, tryEx { 2 }.check { it > 0 })
    }

    @Test
    fun zip() {
        val ok1 = tryEx { 2 }
        val ok2 = tryEx { 50 }
        val ex1 = tryEx { null!! ?: 0 }
        val ex2 = tryEx { "".toInt() }

        assertEquals(tryEx { 100 }, ok1.zip(ok2) { a, b -> a * b })
        assertTrue(ok1.zip(ex1) { a, b -> a * b }.getExceptionOrNull() is NullPointerException )
        assertTrue(ex1.zip(ok2) { a, b -> a * b }.getExceptionOrNull() is NullPointerException )
        assertTrue(ex1.zip(ex2) { a, b -> a * b }.getExceptionOrNull() is NullPointerException )

        assertEquals(tryEx { 2 to 50 }, ok1 zip ok2)
        assertTrue(ok1.zip(ex1).getExceptionOrNull() is NullPointerException )
        assertTrue(ex1.zip(ok2).getExceptionOrNull() is NullPointerException )
        assertTrue(ex1.zip(ex2).getExceptionOrNull() is NullPointerException )
        assertTrue(ex2.zip(ex1).getExceptionOrNull() is NumberFormatException )
    }

    private data class Delegate(val trye: Try<String>) {
        val value by trye
    }

    @Test
    fun tryDelegate() {

        val ok = Delegate(tryEx { "Hello" })
        val ex = Delegate(tryEx { null!! })

        assertEquals("Hello", ok.value)
        assertFailsWith<NullPointerException> {
            ex.value
        }

    }
}

