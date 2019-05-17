package ru.spbstu.wheels

// == values.toSet().hashCode()
fun <T> setHashCode(values: Iterable<T>): Int = values.fold(0) { acc, c -> acc + c.hashCode() }

// == values.toList().hashCode()
fun <T> orderedHashCode(values: Iterable<T>): Int = values.fold(1) { acc, c -> 31 * acc + c.hashCode() }
