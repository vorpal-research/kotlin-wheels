package ru.spbstu.wheels

import kotlinx.warnings.Warnings
import kotlin.math.abs

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

fun <T> Iterable<Iterable<T>>.product(): List<List<T>> =
    fold(mutableListOf(listOf())) { acc, set ->
        acc.flatMapTo(mutableListOf()) { list: List<T> ->
            set.map { element -> list + element }
        }
    }


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

inline fun <T> MutableList<T>.resize(newSize: Int, fill: (Int) -> T) = when {
    size >= newSize -> {
        repeat(size - newSize) {
            removeAt(lastIndex)
        }
    }
    else -> {
        if(this is ArrayList) {
            ensureCapacity(newSize)
        }
        val startingSize = size
        repeat(newSize - startingSize) {
            add(fill(it + startingSize))
        }
    }
}

fun <T> MutableList<T>.assign(other: Collection<T>) {
    if (other.isEmpty()) return clear()

    while (other.size < size) removeLast()

    /* now size <= other.size */

    val iter = other.iterator()
    var i = 0
    while (i < size) {
        this[i] = iter.next()
        ++i
    }
    // native has a buggy ensureCapacity implementation
    if (currentPlatform != Platform.NATIVE && this is ArrayList<*>) ensureCapacity(other.size)
    while (other.size > size) add(iter.next())
}

infix fun <T> List<T>.identityEquals(that: List<T>): Boolean {
    if(size != that.size) return false
    for(i in 0..lastIndex) {
        if(this[i] !== that[i]) return false
    }
    return true
}

infix fun <T> Iterable<T>.identityEquals(that: Iterable<T>): Boolean {
    val thisIt = this.iterator()
    val thatIt = that.iterator()
    while (thisIt.hasNext() || thatIt.hasNext()) {
        if(!thisIt.hasNext()) return false
        if(!thatIt.hasNext()) return false

        if(thisIt.next() !== thatIt.next()) return false
    }
    return true
}

inline fun <T> Iterable<T>.allIndexed(body: (Int, T) -> Boolean): Boolean {
    var ix = 0
    for (e in this) {
        if (!body(ix, e)) return false
        ++ix
    }
    return true
}

inline fun <T> Iterable<T>.anyIndexed(body: (Int, T) -> Boolean): Boolean {
    var ix = 0
    for (e in this) {
        if (body(ix, e)) return true
        ++ix
    }
    return false
}

inline fun <T, C1: MutableCollection<T>, C2: MutableCollection<T>> Iterable<T>.partitionTo(
    c1: C1, c2: C2, body: (T) -> Boolean
): Pair<C1, C2> {
    for (e in this) {
        if (body(e)) {
            c1.add(e)
        } else {
            c2.add(e)
        }
    }
    return Pair(c1, c2)
}
