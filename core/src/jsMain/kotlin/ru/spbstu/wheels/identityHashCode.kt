package ru.spbstu.wheels

actual fun identityHashCode(value: Any?): Int = js("Kotlin.identityHashCode")(value)
