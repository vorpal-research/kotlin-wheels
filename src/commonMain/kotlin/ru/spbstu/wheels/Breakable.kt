@file:OptIn(ExperimentalContracts::class)
package ru.spbstu.wheels

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

object Break : NoStackThrowable("break")
object Continue : NoStackThrowable("continue")

object BreakableContext {
    inline val break_: Nothing get() = throw Break
    inline val continue_: Nothing get() = throw Continue

    inline fun <T> iteration(body: BreakableContext.() -> T): T? {
        contract { callsInPlace(body, InvocationKind.EXACTLY_ONCE) }
        return try {
            this.body()
        } catch (_: Continue) {
            null
        }
    }

    inline fun <T> loop(body: BreakableContext.() -> T): T? {
        contract { callsInPlace(body, InvocationKind.EXACTLY_ONCE) }
        return try {
            this.body()
        } catch (_: Break) {
            null
        }
    }
}

inline fun <T> Iterable<T>.forEachB(body: BreakableContext.(T) -> Unit) {
    contract { callsInPlace(body) }
    BreakableContext.loop {
        forEach {
            iteration { body(it) }
        }
    }
}

inline fun <T> Sequence<T>.forEachB(body: BreakableContext.(T) -> Unit) {
    contract { callsInPlace(body) }
    BreakableContext.loop {
        forEach {
            iteration { body(it) }
        }
    }
}

inline fun <T> Iterable<T>.forEachIndexedB(body: BreakableContext.(Int, T) -> Unit) {
    contract { callsInPlace(body) }
    BreakableContext.loop {
        forEachIndexed { i, e ->
            iteration { body(i, e) }
        }
    }
}

inline fun <T> Sequence<T>.forEachIndexedB(body: BreakableContext.(Int, T) -> Unit) {
    contract { callsInPlace(body) }
    BreakableContext.loop {
        forEachIndexed { i, e ->
            iteration { body(i, e) }
        }
    }
}


inline fun repeatB(times: Int, body: BreakableContext.(Int) -> Unit) {
    contract { callsInPlace(body) }
    BreakableContext.loop {
        repeat(times) {
            iteration { body(it) }
        }
    }
}

inline fun <T, U, C: MutableCollection<U>> Iterable<T>.mapOrBreakTo(to: C, body: BreakableContext.(T) -> U): C {
    contract { callsInPlace(body) }
    BreakableContext.loop {
        forEach {
            iteration { to.add(body(it)) }
        }
    }
    return to
}

inline fun <T, U> Iterable<T>.mapOrBreak(body: BreakableContext.(T) -> U): List<U> =
        mapOrBreakTo(mutableListOf(), body)

inline fun <T, U, C: MutableCollection<U>> Sequence<T>.mapOrBreakTo(to: C, body: BreakableContext.(T) -> U): C {
    contract { callsInPlace(body) }
    BreakableContext.loop {
        forEach {
            iteration { to.add(body(it)) }
        }
    }
    return to
}

inline fun <T, U> Sequence<T>.mapOrBreak(crossinline body: BreakableContext.(T) -> U): Sequence<U> = sequence {
    BreakableContext.loop {
        forEach {
            iteration { yield(body(it)) }
        }
    }
}

inline fun <T, U, C: MutableCollection<U>> Iterable<T>.mapIndexedOrBreakTo(to: C, body: BreakableContext.(Int, T) -> U): C {
    contract { callsInPlace(body) }
    BreakableContext.loop {
        forEachIndexed { i, it ->
            iteration { to.add(body(i, it)) }
        }
    }
    return to
}

inline fun <T, U> Iterable<T>.mapIndexedOrBreak(body: BreakableContext.(Int, T) -> U): List<U> =
        mapIndexedOrBreakTo(mutableListOf(), body)

inline fun <T, U, C: MutableCollection<U>> Sequence<T>.mapIndexedOrBreakTo(to: C, body: BreakableContext.(Int, T) -> U): C {
    BreakableContext.loop {
        forEachIndexed { i, it ->
            iteration { to.add(body(i, it)) }
        }
    }
    return to
}

inline fun <T, U> Sequence<T>.mapIndexedOrBreak(crossinline body: BreakableContext.(Int, T) -> U): Sequence<U> = sequence {
    BreakableContext.loop {
        forEachIndexed { i, it ->
            iteration { yield(body(i, it)) }
        }
    }
}
