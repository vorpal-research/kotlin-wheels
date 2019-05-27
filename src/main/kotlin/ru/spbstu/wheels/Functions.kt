package ru.spbstu.wheels

inline fun <T, R> memo(cache: MutableMap<T, R>, input: T, body: () -> R): R =
        cache.getOrPut(input, body)
