@file: Suppress(Warnings.NOTHING_TO_INLINE)

package ru.spbstu.wheels

import kotlinx.warnings.Warnings

@RequiresOptIn()
private annotation class OptionArrayInaccessible

/*
* Essentially, your good ol' array as you know it, but T is not reified.
* Is needed to avoid dealing with Array<Any?> when implementing array-based data structures.
* */
@Suppress(Warnings.UNCHECKED_CAST, Warnings.NOTHING_TO_INLINE)
inline class OptionArray<T>
@OptionArrayInaccessible
@PublishedApi
internal constructor(@PublishedApi internal val inner: Array<Any?>) {

    @OptIn(OptionArrayInaccessible::class)
    constructor(size: Int) : this(Array<Any?>(size) { Option.NOVALUE })

//    @Suppress("DEPRECATION")
//    constructor(size: Int, init: (Int) -> T) : this(Array<Any?>(size, init))

    inline operator fun get(index: Int): Option<T> = Option(inner[index])
    inline operator fun set(index: Int, value: Option<T>) {
        inner[index] = value.unsafeValue
    }

    inline val size: Int get() = inner.size

    // danger: boxing!
    inline operator fun iterator(): Iterator<Option<T>> =
            iterator {
                for (i in 0 until size) yield(get(i))
            }

    override fun toString(): String {
        if(size == 0) return "[]"
        val sb = StringBuilder("[")
        sb.append(get(0))
        for(i in 1 until size) sb.append(", ").append(get(i))
        sb.append("]")
        return "$sb"
    }
}

inline fun <T> OptionArray(size: Int, init: (Int) -> Option<T>): OptionArray<T> {
    val res = OptionArray<T>(size)
    for(i in 0 until size) res[i] = init(i)
    return res
}

inline fun <T> OptionArray<out T>.copyInto(destination: OptionArray<T>,
                                      destinationOffset: Int = 0,
                                      startIndex: Int = 0,
                                      endIndex: Int = size) {
    inner.copyInto(destination.inner, destinationOffset, startIndex, endIndex)
}

@OptIn(OptionArrayInaccessible::class)
inline fun <T> OptionArray<out T>.copyOf(): OptionArray<T> = OptionArray(inner.copyOf())

@OptIn(OptionArrayInaccessible::class)
inline fun <T> OptionArray<out T>.copyOf(newSize: Int): OptionArray<T> = OptionArray(inner.copyOf(newSize))

@OptIn(OptionArrayInaccessible::class)
inline fun <T> OptionArray<out T>.copyOfRange(fromIndex: Int, toIndex: Int): OptionArray<T> =
        OptionArray(inner.copyOfRange(fromIndex, toIndex))

inline fun <T> OptionArray<T>.fill(element: T, fromIndex: Int = 0, toIndex: Int = size) {
    inner.fill(element, fromIndex, toIndex)
}

@OptIn(OptionArrayInaccessible::class)
inline fun <A, B> OptionArray<out A>.mapValues(body: (A) -> B): OptionArray<B> {
    val res = OptionArray<B>(inner.copyOf())
    for (i in 0 until size)
        if(this[i].isNotEmpty()) res.inner[i] = body(this[i].get())
    return res
}
