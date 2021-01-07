@file: Suppress(Warnings.NOTHING_TO_INLINE)

package ru.spbstu.wheels

import kotlinx.warnings.Warnings
import kotlin.reflect.KClass

/*
* Essentially, your good ol' array as you know it, but T is not reified.
* Is needed to avoid dealing with Array<Any?> when implementing array-based data structures.
* */
@Suppress(Warnings.UNCHECKED_CAST, Warnings.NOTHING_TO_INLINE)
inline class TArray<T>
@Deprecated(message = "Do not use")
@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
@PublishedApi
internal constructor(@PublishedApi internal val inner: Array<Any?>): Collection<T?> {

    @Suppress("DEPRECATION")
    constructor(size: Int) : this(arrayOfNulls(size))

//    @Suppress("DEPRECATION")
//    constructor(size: Int, init: (Int) -> T) : this(Array<Any?>(size, init))

    inline operator fun get(index: Int): T? = inner[index] as T?
    inline operator fun set(index: Int, value: T?) {
        inner[index] = value
    }

    @Suppress(Warnings.OVERRIDE_BY_INLINE)
    override inline val size: Int get() = inner.size

    @Suppress(Warnings.OVERRIDE_BY_INLINE)
    override inline operator fun iterator(): Iterator<T?> = object: Iterator<T?> {
        private var index: Int = 0
        override fun hasNext(): Boolean = index < inner.lastIndex

        @Suppress(Warnings.UNCHECKED_CAST)
        override fun next(): T? = inner[index].also { ++index } as T?
    }

    override fun toString(): String = joinToString(prefix = "[", postfix = "]")

    override fun contains(element: T?): Boolean {
        for (i in 0 until size)
            if (element == get(i)) return true
        return false
    }

    override fun containsAll(elements: Collection<T?>): Boolean =
            elements.all { contains(it) }

    override fun isEmpty(): Boolean = size == 0
}


inline fun <T> TArray(size: Int, init: (Int) -> T): TArray<T> {
    val res = TArray<T>(size)
    for(i in 0 until size) res[i] = init(i)
    return res
}

@Suppress(Warnings.DEPRECATION, Warnings.UNCHECKED_CAST)
fun <T> tarrayOf(vararg values: T): TArray<T> = TArray(values as Array<Any?>)

inline operator fun <T> TArray<out T>.component1() = get(0)
inline operator fun <T> TArray<out T>.component2() = get(1)
inline operator fun <T> TArray<out T>.component3() = get(2)
inline operator fun <T> TArray<out T>.component4() = get(3)
inline operator fun <T> TArray<out T>.component5() = get(4)

inline fun <T> TArray<out T>.elementAt(index: Int): T? = get(index)
inline fun <T> TArray<out T>.elementAtOrElse(index: Int, body: (Int) -> T): T = when {
    index < size -> get(index) ?: body(index)
    else -> body(index)
}

inline fun <T> TArray<out T>.contentEquals(that: TArray<T>): Boolean {
    if (this.size != that.size) return false
    for (i in 0 until this.size) {
        if (this[i] != that[i]) return false
    }
    return true
}

inline operator fun <T> TArray<out T>.plus(rhv: TArray<out T>): TArray<T> {
    val res = TArray<T>(this.size + rhv.size)
    this.copyInto(res)
    rhv.copyInto(res, destinationOffset = this.size)
    return res
}

inline fun <T, A : Appendable> TArray<out T>.joinTo(
        buffer: A,
        separator: CharSequence = ", ",
        prefix: CharSequence = "",
        postfix: CharSequence = "",
        limit: Int = -1,
        truncated: CharSequence = "...",
        transform: ((T?) -> CharSequence)): A {
    buffer.append(prefix)
    var count = 0
    for (i in 0 until size) {
        val element = get(i)
        if (++count > 1) buffer.append(separator)
        if (limit < 0 || count <= limit) {
            buffer.append(transform(element))
        } else break
    }
    if (limit >= 0 && count > limit) buffer.append(truncated)
    buffer.append(postfix)
    return buffer
}

fun <T, A : Appendable> TArray<out T>.joinTo(
        buffer: A,
        separator: CharSequence = ", ",
        prefix: CharSequence = "",
        postfix: CharSequence = "",
        limit: Int = -1,
        truncated: CharSequence = "..."): A = joinTo(buffer, separator, prefix, postfix, limit, truncated) { "$it" }

inline fun <T> TArray<out T>.joinToString(
        separator: CharSequence = ", ",
        prefix: CharSequence = "",
        postfix: CharSequence = "",
        limit: Int = -1,
        truncated: CharSequence = "...",
        transform: (T?) -> CharSequence
): String = joinTo(StringBuilder(), separator, prefix, postfix, limit, truncated, transform).toString()

inline fun <T> TArray<out T>.joinToString(
        separator: CharSequence = ", ",
        prefix: CharSequence = "",
        postfix: CharSequence = "",
        limit: Int = -1,
        truncated: CharSequence = "..."
): String = joinTo(StringBuilder(), separator, prefix, postfix, limit, truncated).toString()

@Suppress(Warnings.UNCHECKED_CAST, Warnings.NOTHING_TO_INLINE)
inline fun <T> TArray<out T>.asList(): List<T?> = inner.asList() as List<T?>

inline fun <T> TArray<out T>.copyInto(destination: TArray<T>,
                                      destinationOffset: Int = 0,
                                      startIndex: Int = 0,
                                      endIndex: Int = size) {
    inner.copyInto(destination.inner, destinationOffset, startIndex, endIndex)
}

@Suppress(Warnings.DEPRECATION)
inline fun <T> TArray<out T>.copyOf(): TArray<T> = TArray(inner.copyOf())

@Suppress(Warnings.DEPRECATION)
inline fun <T> TArray<out T>.copyOf(newSize: Int): TArray<T> = TArray(inner.copyOf(newSize))

@Suppress(Warnings.DEPRECATION)
inline fun <T> TArray<out T>.copyOfRange(fromIndex: Int, toIndex: Int): TArray<T> =
        TArray(inner.copyOfRange(fromIndex, toIndex))

inline fun <T> TArray<T>.fill(element: T, fromIndex: Int = 0, toIndex: Int = size) {
    inner.fill(element, fromIndex, toIndex)
}

inline fun <T : Comparable<T>> TArray<T>.sort() {
    @Suppress(Warnings.UNCHECKED_CAST)
    inner.sortBy { it as Comparable<Any> }
}

inline fun <T> TArray<T>.sortWith(cmp: Comparator<T?>) {
    @Suppress(Warnings.UNCHECKED_CAST)
    inner.sortWith(Comparator { a, b -> cmp.compare(a as T?, b as T?) })
}

fun <T> Collection<T>.toTArray(): TArray<T> {
    val res = TArray<T>(size)
    for ((i, e) in this.withIndex()) res[i] = e
    return res
}

inline fun <reified T> TArray<out T>.toTypedArray(): Array<T?> = Array(size) { get(it) }

fun <T> TArray<out T>.withIndex(): Iterable<IndexedValue<T?>> = Iterable {
    iterator {
        for (i in 0 until size) yield(IndexedValue(i, get(i)))
    }
}

inline fun <A, B> TArray<out A>.map(body: (A?) -> B?): TArray<B> {
    val res = TArray<B>(size)
    for (i in 0 until size) res[i] = body(this[i])
    return res
}

fun <T> TArray<out T>.asIterable(): Iterable<T?> = Iterable { iterator() }
fun <T> TArray<out T>.asSequence(): Sequence<T?> = Sequence { iterator() }
