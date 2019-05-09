package ru.spbstu.wheels

import kotlinx.warnings.Warnings
import kotlin.reflect.KProperty

inline class Option<out T>(val unsafeValue: Any?) {
    companion object {
        private val NOVALUE = Any()
        private val EMPTY = Option<Any?>(NOVALUE)

        @Suppress(Warnings.NOTHING_TO_INLINE)
        inline fun <T> just(value: T) = Option<T>(value)
        @Suppress(Warnings.UNCHECKED_CAST)
        fun <T> empty() = EMPTY as Option<T>

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

fun <T> Iterator<T>.nextOption() = if(hasNext()) Option.just(next()) else Option.empty()
