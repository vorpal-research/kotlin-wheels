@file:OptIn(ExperimentalContracts::class)
package ru.spbstu.wheels

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

inline infix fun Int.times(body: (Int) -> Unit) {
    contract { callsInPlace(body) }
    repeat(this, body)
}

inline fun <T> runIf(condition: Boolean, body: () -> T): T? {
    contract {
        returnsNotNull() implies condition
        callsInPlace(body, InvocationKind.AT_MOST_ONCE)
    }
    return if (condition) body() else null
}
