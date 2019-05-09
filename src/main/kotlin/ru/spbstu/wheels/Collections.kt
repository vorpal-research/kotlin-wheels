package ru.spbstu.wheels

inline fun <A, B, R> Iterable<A>.product(that: Iterable<B>, body: (A, B) -> R): List<R> {
    val res = mutableListOf<R>()
    for(a in this)
        for(b in that)
            res.add(body(a, b))
    return res
}

infix fun <A, B> Iterable<A>.product(that: Iterable<B>): List<Pair<A, B>> = product(that, ::Pair)
