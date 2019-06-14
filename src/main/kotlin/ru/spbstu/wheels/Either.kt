package ru.spbstu.wheels

import kotlinx.warnings.Warnings

inline class Either<out A, out B>
@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
@PublishedApi
internal constructor(@PublishedApi internal val unsafeValue: Any?) {
    companion object {
        @Suppress(Warnings.NOTHING_TO_INLINE)
        inline fun <A> left(value: A): Either<A, Nothing> = Either(value)

        @Suppress(Warnings.NOTHING_TO_INLINE)
        inline fun <B> right(value: B): Either<Nothing, B> = Either(value)
    }
}

@Suppress(Warnings.UNCHECKED_CAST)
val <T> Either<T, T>.value: T
    get() = unsafeValue as T

@Suppress(Warnings.UNCHECKED_CAST, Warnings.NOTHING_TO_INLINE)
inline fun <A> Either<A, *>.asLeft(): A = value as A

@Suppress(Warnings.UNCHECKED_CAST, Warnings.NOTHING_TO_INLINE)
inline fun <B> Either<*, B>.asRight(): B = value as B

inline fun <reified A> Either<A, *>.isLeft(): Boolean = value is A
inline fun <reified B> Either<*, B>.isRight(): Boolean = value is B

inline val <reified T> Either<T, *>.leftOption: Option<T>
    get() = if (unsafeValue is T) Option.just(unsafeValue) else Option.empty()
inline val <reified T> Either<*, T>.rightOption: Option<T>
    get() = if (unsafeValue is T) Option.just(unsafeValue) else Option.empty()

// why R here is a separate parameter? cos otherwise it may infer to Any or Any?
// and the check will succeed on anything, which we don't want to happen
inline fun <R, reified T : R> Either<T, *>.leftOr(other: () -> R): R =
        if (unsafeValue is T) unsafeValue else other()

inline fun <R, reified T : R> Either<*, T>.rightOr(other: () -> R): R =
        if (unsafeValue is T) unsafeValue else other()

inline fun <reified T> Either<T, *>.leftOrNull(): T? = checkedCast<T>(unsafeValue)
inline fun <reified T> Either<*, T>.rightOrNull(): T? = checkedCast<T>(unsafeValue)

inline fun <reified A, B, R> Either<A, B>.mapLeft(body: (A) -> R): Either<R, B> =
        if (isLeft()) Either.left(body(asLeft())) else Either(unsafeValue)

inline fun <A, reified B, R> Either<A, B>.mapRight(body: (B) -> R): Either<A, R> =
        if (isRight()) Either.right(body(asRight())) else Either(unsafeValue)

inline operator fun <
        reified A : Comparable<A>,
        reified B : Comparable<B>
        > Either<A, B>.compareTo(that: Either<A, B>): Int =
        when {
            isLeft() -> when {
                that.isRight() -> -1
                else -> asLeft().compareTo(that.asLeft())
            }
            else -> when {
                that.isLeft() -> 1
                else -> asRight().compareTo(that.asRight())
            }
        }
