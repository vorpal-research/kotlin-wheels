package ru.spbstu.wheels

import kotlinx.warnings.Warnings

inline fun <A, B, R> Iterable<A>.product(that: Iterable<B>, body: (A, B) -> R): List<R> {
    val res = mutableListOf<R>()
    for (a in this)
        for (b in that)
            res.add(body(a, b))
    return res
}

infix fun <A, B> Iterable<A>.product(that: Iterable<B>): List<Pair<A, B>> = product(that, ::Pair)

fun <T> List<T>.firstOption(): Option<T> = when {
    isEmpty() -> Option.empty()
    else -> Option.just(get(0))
}

fun <T> Iterable<T>.firstOption(): Option<T> = when (this) {
    is List -> this.firstOption()
    else -> iterator().nextOption()
}

fun <T> List<T>.tail(): List<T> = subList(1, lastIndex)
fun <T> Iterable<T>.tail(): Iterable<T> = when (this) {
    is List -> this.tail()
    else -> Iterable { iterator().apply { if (hasNext()) next() } }
}

inline fun <reified T> Iterable<*>.firstInstance(): T? {
    for(e in this) if(e is T) return e
    return null
}

fun <T> List<T>.getOrNull(index: Int) = if(index in 0..lastIndex) get(index) else null

inline fun <A, reified B> Collection<A>.mapToArray(body: (A) -> B): Array<B> {
    val arr = arrayOfNulls<B>(size)
    var i = 0
    for(e in this) arr[i++] = body(e)
    @Suppress(Warnings.UNCHECKED_CAST)
    return arr as Array<B>
}
