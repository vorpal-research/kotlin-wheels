package ru.spbstu.wheels

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

