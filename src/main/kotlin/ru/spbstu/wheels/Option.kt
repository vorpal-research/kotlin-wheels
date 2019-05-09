package ru.spbstu.wheels

import kotlinx.warnings.Warnings
import kotlin.reflect.KProperty

inline class Option<out T>
@Deprecated(replaceWith = ReplaceWith("Option.just(unsafeValue)"), message = "Do not use")
constructor(val unsafeValue: Any?) {
    companion object {
        private val NOVALUE = Any()
        @Suppress(Warnings.DEPRECATION)
        private val EMPTY = Option<Nothing>(NOVALUE)

        @Suppress(Warnings.NOTHING_TO_INLINE, Warnings.DEPRECATION)
        inline fun <T> just(value: T) = Option<T>(value)

        fun <T> empty(): Option<T> = EMPTY

        fun <T> ofNullable(value: T) = value?.let(::just) ?: empty()
    }

    fun isEmpty() = NOVALUE === unsafeValue
    fun isNotEmpty() = NOVALUE !== unsafeValue

    fun getOrNull(): T? = getOrElse { null }
    fun get(): T = getOrElse { throw IllegalStateException("Option.empty().get()") }

    override fun toString(): String = when {
        isEmpty() -> "Option.empty()"
        else -> "Option.just(${get()})"
    }
}

@Suppress(Warnings.UNCHECKED_CAST)
inline fun <T> Option<T>.getOrElse(body: () -> T): T = when {
    isEmpty() -> body()
    else -> unsafeValue as T
}

@Suppress(Warnings.UNCHECKED_CAST)
inline fun <T> Option<T>.orElse(body: () -> Option<T>): Option<T> = when {
    isEmpty() -> body()
    else -> this
}

inline fun <T, U> Option<T>.map(body: (T) -> U): Option<U> = when {
    isEmpty() -> Option.empty()
    else -> Option.just(body(get()))
}

inline fun <T, U> Option<T>.flatMap(body: (T) -> Option<U>): Option<U> = when {
    isEmpty() -> Option.empty()
    else -> body(get())
}

fun <T> Option<Option<T>>.flatten(): Option<T> = flatMap { it }

inline fun <T> Option<T>.filter(body: (T) -> Boolean): Option<T> = when {
    isEmpty() || !body(get()) -> Option.empty()
    else -> this
}

inline fun <A, B, R> Option<A>.zip(that: Option<B>, body: (A, B) -> R): Option<R> = when {
    this.isEmpty() || that.isEmpty() -> Option.empty()
    else -> Option.just(body(this.get(), that.get()))
}

infix fun <A, B> Option<A>.zip(that: Option<B>): Option<Pair<A, B>> = zip(that, ::Pair)

inline fun <A, B, C, R> zip3(a: Option<A>, b: Option<B>, c: Option<C>, body: (A, B, C) -> R): Option<R> = when {
    a.isEmpty() || b.isEmpty() || c.isEmpty() -> Option.empty()
    else -> Option.just(body(a.get(), b.get(), c.get()))
}

fun <A, B, C> zip3(a: Option<A>, b: Option<B>, c: Option<C>): Option<Triple<A, B, C>> = zip3(a, b, c, ::Triple)

operator fun <T> Option<T>.getValue(thisRef: Any?, prop: KProperty<*>) = get()

fun <T> Iterator<T>.nextOption() = if (hasNext()) Option.just(next()) else Option.empty()
