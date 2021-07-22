@file:OptIn(ExperimentalContracts::class)
package ru.spbstu.wheels

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

inline infix fun Int.times(body: (Int) -> Unit) {
    contract { callsInPlace(body) }
    repeat(this, body)
}
