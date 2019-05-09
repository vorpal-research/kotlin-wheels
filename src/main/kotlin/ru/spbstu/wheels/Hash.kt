package ru.spbstu.wheels

fun <T> hashCode(value: T): Int = value?.hashCode() ?: 0 // java.util.Objects.hashCode(value)

// == values.toSet().hashCode()
fun <T> setHashCode(values: Iterable<T>): Int = values.fold(0) { acc, c -> acc + hashCode(c) }

// == values.toList().hashCode()
fun <T> orderedHashCode(values: Iterable<T>): Int = values.fold(1) { acc, c -> 31 * acc + hashCode(c) }
