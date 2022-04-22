@file: Suppress(Warnings.NOTHING_TO_INLINE)

package ru.spbstu.wheels

import kotlinx.warnings.Warnings

/*
* Essentially, your good ol' array as you know it, but T is not reified.
* Is needed to avoid dealing with Array<Any?> when implementing array-based data structures.
* */
@Suppress(Warnings.UNCHECKED_CAST, Warnings.NOTHING_TO_INLINE)
inline class OptionArray<T>
@PublishedApi
internal constructor(@PublishedApi internal val inner: Array<Any?>): InlineArray<Option<T>, Any?> {

    override val realArray: Array<Any?>
        get() = inner

    constructor(size: Int) : this(Array<Any?>(size) { Option.NOVALUE })

//    @Suppress("DEPRECATION")
//    constructor(size: Int, init: (Int) -> T) : this(Array<Any?>(size, init))

    @Suppress(Warnings.OVERRIDE_BY_INLINE)
    override inline operator fun get(index: Int): Option<T> = Option(inner[index])
    inline operator fun set(index: Int, value: Option<T>) {
        inner[index] = value.unsafeValue
    }

    override fun contains(element: Option<T>): Boolean = defaultContains(element) { element.unsafeValue }
    override fun toString(): String = defaultToString { Option(it) }
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

inline fun <T> OptionArray<out T>.copyOf(): OptionArray<T> = OptionArray(inner.copyOf())

inline fun <T> OptionArray<out T>.copyOf(newSize: Int): OptionArray<T> = OptionArray(inner.copyOf(newSize))

inline fun <T> OptionArray<out T>.copyOfRange(fromIndex: Int, toIndex: Int): OptionArray<T> =
        OptionArray(inner.copyOfRange(fromIndex, toIndex))

inline fun <T> OptionArray<T>.fill(element: T, fromIndex: Int = 0, toIndex: Int = size) {
    inner.fill(element, fromIndex, toIndex)
}

inline fun <A, B> OptionArray<out A>.mapValues(body: (A) -> B): OptionArray<B> {
    val res = OptionArray<B>(inner.copyOf())
    for (i in 0 until size)
        if(this[i].isNotEmpty()) res.inner[i] = body(this[i].get())
    return res
}
