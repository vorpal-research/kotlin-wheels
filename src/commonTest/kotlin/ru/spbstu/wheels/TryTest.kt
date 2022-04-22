package ru.spbstu.wheels

import kotlinx.warnings.Warnings
import kotlin.test.Test
import kotlin.test.*

@Suppress(Warnings.CAST_NEVER_SUCCEEDS, Warnings.UNREACHABLE_CODE, Warnings.USELESS_ELVIS)
class TryTest {

    fun swapWord(s: StringBuilder, from: Int, to: Int) {
        var from = from
        var to = to
        while (from < to) {
            val tmp = s[from]
            s[from] = s[to]
            s[to] = tmp

            ++from
            --to
        }
    }

    fun yandexWhatever4(s: StringBuilder) {
        var ix = 0
        while (ix < s.length) {
            while(ix < s.length && s[ix].isWhitespace()) ++ix
            if (ix >= s.length) break
            val from = ix
            while(ix < s.length && !s[ix].isWhitespace()) ++ix
            if (ix > s.length) break
            val to = ix - 1
            swapWord(s, from, to)
            ++ix
        }
    }



    data class ListCell<T>(val head: T, val tail: ListCell<T>? = null): AbstractList<T>() {
        override val size: Int
            get() = (tail?.size ?: 0) + 1

        companion object {
            tailrec fun <T> ListCell<T>.getTailrec(index: Int): T? = when(index) {
                0 -> head
                else -> tail?.getTailrec(index - 1)
            }
        }

        override fun get(index: Int): T {
            return getTailrec(index)!!
        }

        override fun toString(): String {
            return super<AbstractList>.toString()
        }

        override fun equals(other: Any?): Boolean {
            return super.equals(other)
        }

        override fun hashCode(): Int {
            return super.hashCode()
        }
    }


    fun huaweiWhatever(list: List<Int>, window: Int): List<ListCell<Int>> {
        if (window == 0) return listOf()
        if (window == 1) return list.map { ListCell(it) }

        return list.indices.flatMap { repIx ->
            huaweiWhatever(list.subList(repIx + 1, list.size), window - 1).map { rec ->
                ListCell(list[repIx], rec)
            }
        }
    }

    fun huaweiWhateverTry2(list: List<Int>, window: Int): List<List<Int>> {
        if (window == 0) return listOf()
        if (window == 1) return list.map { listOf(it) }

        val indices = (0 until window).toMutableList()
        val indexBases = indices.toMutableList()

        val res = mutableListOf<List<Int>>()

        outer@ while (true) {
            println(indices)
            res.add(indices.map { list[it] })

            var i = indices.lastIndex
            while (i >= 0) {
                val maxValue = list.size + i - indices.lastIndex
                indices[i]++
                if (indices[i] >= maxValue) {
                    if (i == 0) break@outer
                    indices[i] = indices[i - 1] + 2
                    if (indices[i] >= maxValue) indices[i] = -1
                    --i
                } else break
            }
            for (i in indices.indices) {
                if (indices[i] == -1) indices[i] = indices[i - 1] + 1
            }
        }

        return res
    }

    @Test
    fun takeWhatever() {
        println(huaweiWhateverTry2((0..3000000).toList(), 3000001))
        assertEquals(huaweiWhatever((0..30).toList(), 3), huaweiWhateverTry2((0..30).toList(), 3))
    }

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

