@file: Suppress(Warnings.NOTHING_TO_INLINE)

package ru.spbstu.wheels

import kotlinx.warnings.Warnings

inline class EitherArray<A, B>
@PublishedApi
internal constructor(@PublishedApi internal val inner: Array<Any?>): InlineArray<Either<A, B>, Any?> {
    @PublishedApi
    internal constructor(size: Int) : this(arrayOfNulls(size))

    override val realArray: Array<Any?>
        get() = inner

    override inline operator fun get(index: Int): Either<A, B> = Either(inner[index])
    inline operator fun set(index: Int, value: Either<A, B>) {
        inner[index] = value.unsafeValue
    }

    override fun contains(element: Either<A, B>): Boolean = defaultContains(element) { element.unsafeValue }
    override fun toString(): String = defaultToString { Either(it) }
}

inline fun <A, B> EitherArray(size: Int, init: (Int) -> Either<A, B>): EitherArray<A, B> {
    val res = EitherArray<A, B>(size)
    for(i in 0 until size) res[i] = init(i)
    return res
}

inline fun <A, B> EitherArray<out A, out B>.copyInto(destination: EitherArray<A, B>,
                                      destinationOffset: Int = 0,
                                      startIndex: Int = 0,
                                      endIndex: Int = size) {
    inner.copyInto(destination.inner, destinationOffset, startIndex, endIndex)
}

@Suppress(Warnings.DEPRECATION)
inline fun <A, B> EitherArray<out A, out B>.copyOf(): EitherArray<A, B> = EitherArray(inner.copyOf())

@Suppress(Warnings.DEPRECATION)
inline fun <A, B> EitherArray<out A, out B>.copyOf(newSize: Int): EitherArray<A, B> = EitherArray(inner.copyOf(newSize))

@Suppress(Warnings.DEPRECATION)
inline fun <A, B> EitherArray<out A, out B>.copyOfRange(fromIndex: Int, toIndex: Int): EitherArray<A, B> =
        EitherArray(inner.copyOfRange(fromIndex, toIndex))

inline fun <A, B> EitherArray<A, B>.fill(element: Either<A, B>, fromIndex: Int = 0, toIndex: Int = size) {
    inner.fill(element.value, fromIndex, toIndex)
}

inline fun <reified A, B, C> EitherArray<out A, out B>.mapLeft(body: (A) -> C): EitherArray<C, B> {
    val res = EitherArray<C, B>(inner.copyOf())
    for (i in 0 until size) {
        val ii = this[i]
        if(ii.isLeft()) res.inner[i] = body(ii.asLeft())
    }
    return res
}

inline fun <A, reified B, C> EitherArray<out A, out B>.mapRight(body: (B) -> C): EitherArray<A, C> {
    val res = EitherArray<A, C>(inner.copyOf())
    for (i in 0 until size) {
        val ii = this[i]
        if(ii.isRight()) res.inner[i] = body(ii.asRight())
    }
    return res
}
