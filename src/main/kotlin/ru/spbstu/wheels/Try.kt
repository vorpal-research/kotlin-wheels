package ru.spbstu.wheels

import kotlinx.warnings.Warnings
import kotlinx.warnings.Warnings.UNCHECKED_CAST
import kotlin.reflect.KProperty

inline class Try<out T>(val unsafeValue: Any?) {
    @PublishedApi
    internal data class Failure(val exception: Exception) {
        override fun toString(): String = "Try.exception($exception)"
    }

    @PublishedApi
    internal val failure: Failure?
        get() = unsafeValue as? Failure

    companion object {
        @Suppress(Warnings.NOTHING_TO_INLINE)
        inline fun <T> just(value: T) = Try<T>(value)
        @Suppress(Warnings.UNCHECKED_CAST)
        fun exception(exception: Exception) = Try<Nothing>(Failure(exception))
    }

    fun isException() = unsafeValue is Failure
    fun isNotException() = unsafeValue !is Failure

    fun getOrNull(): T? = getOrElse { null }
    fun getExceptionOrNull(): Exception? = failure?.exception

    fun getOrThrow(): T {
        failure?.apply { throw exception }
        @Suppress(UNCHECKED_CAST)
        return unsafeValue as T
    }

    inline fun <reified E: Exception> catch(body: (E) -> @UnsafeVariance T): Try<T> = tryWrap {
        val ex = failure?.exception
        if(ex is E) Try.just(body(ex))
        else this
    }

    override fun toString(): String =
            failure?.toString() ?: "Try.just($unsafeValue)"
}

@PublishedApi
internal inline fun Try.Failure.asTry(): Try<Nothing> = Try(this)

@Suppress(Warnings.UNCHECKED_CAST)
inline fun <T> Try<T>.getOrElse(body: () -> T): T = when {
    isException() -> body()
    else -> unsafeValue as T
}

inline fun <T> tryEx(body: () -> T) = try {
    Try.just(body())
} catch (ex: Exception) {
    Try.exception(ex)
}

@PublishedApi
internal inline fun <T> tryWrap(body: () -> Try<T>): Try<T> =
        try { body() } catch (ex: Exception) { Try.exception(ex) }

inline fun <T, U> Try<T>.map(body: (T) -> U): Try<U> = tryWrap {
    failure?.asTry() ?: Try.just(body(getOrThrow()))
}

inline fun <T, U> Try<T>.flatMap(body: (T) -> Try<U>): Try<U> = tryWrap {
    failure?.asTry() ?: body(getOrThrow())
}

inline fun <T> Try<T>.recover(body: (Exception) -> Try<T>): Try<T> = tryWrap {
    failure?.exception?.let(body) ?: this
}

fun <T> Try<Try<T>>.flatten(): Try<T> = flatMap { it }

inline fun <T> Try<T>.require(body: (T) -> Boolean): Try<T> = tryWrap {
    if(isException()) return this
    kotlin.require(body(getOrThrow()))
    return this
}

inline fun <T> Try<T>.check(body: (T) -> Boolean): Try<T> = tryWrap {
    if(isException()) return this
    kotlin.check(body(getOrThrow()))
    return this
}

inline fun <A, B, R> Try<A>.zip(that: Try<B>, body: (A, B) -> R): Try<R> = tryWrap {
    this.failure?.asTry() ?: that.failure?.asTry() ?: Try.just(body(this.getOrThrow(), that.getOrThrow()))
}

infix fun <A, B> Try<A>.zip(that: Try<B>): Try<Pair<A, B>> = zip(that, ::Pair)

inline fun <A, B, C, R> zip3(a: Try<A>, b: Try<B>, c: Try<C>, body: (A, B, C) -> R): Try<R> = tryWrap {
    a.failure?.asTry() ?: b.failure?.asTry() ?: c.failure?.asTry() ?:
            Try.just(body(a.getOrThrow(), b.getOrThrow(), c.getOrThrow()))
}

fun <A, B, C> zip3(a: Try<A>, b: Try<B>, c: Try<C>): Try<Triple<A, B, C>> = zip3(a, b, c, ::Triple)

operator fun <T> Try<T>.getValue(thisRef: Any?, prop: KProperty<*>) = getOrThrow()
