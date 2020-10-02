package ru.spbstu.wheels

import kotlin.native.identityHashCode

actual fun identityHashCode(value: Any?): Int = value.identityHashCode()