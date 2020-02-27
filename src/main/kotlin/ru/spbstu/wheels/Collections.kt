package ru.spbstu.wheels

import kotlinx.warnings.Warnings

inline fun <A, B, R> Iterable<A>.product(that: Iterable<B>, body: (A, B) -> R): List<R> =
        productTo(that, mutableListOf(), body).asList()

infix fun <A, B> Iterable<A>.product(that: Iterable<B>): List<Pair<A, B>> = product(that, ::Pair)

inline fun <A, B, R, C: MutableCollection<R>> Iterable<A>.productTo(that: Iterable<B>, to: C, body: (A, B) -> R): C {
    for (a in this)
        for (b in that)
            to.add(body(a, b))
    return to
}

fun <A, B, C: MutableCollection<Pair<A, B>>> Iterable<A>.productTo(that: Iterable<B>, to: C): C =
        productTo(that, to, ::Pair)

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

inline fun <A, B, R, C: MutableCollection<R>> Iterable<A>.zipTo(that: Iterable<B>, to: C, transform: (A, B) -> R): C {
    val thisIt = this.iterator()
    val thatIt = that.iterator()
    while(thisIt.hasNext() && thatIt.hasNext()) {
        to.add(transform(thisIt.next(), thatIt.next()))
    }
    return to
}

@Suppress(Warnings.NOTHING_TO_INLINE)
inline fun <T> Collection<T>.asCollection(): Collection<T> = this
@Suppress(Warnings.NOTHING_TO_INLINE)
inline fun <T> Set<T>.asSet(): Set<T> = this
@Suppress(Warnings.NOTHING_TO_INLINE)
inline fun <T> List<T>.asList(): List<T> = this
