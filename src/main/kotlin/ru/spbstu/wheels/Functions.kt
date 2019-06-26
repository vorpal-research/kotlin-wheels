package ru.spbstu.wheels

import kotlin.reflect.KClass

inline fun <T, R> memo(cache: MutableMap<T, R>, input: T, body: () -> R): R =
        cache.getOrPut(input, body)

private val runOnceCache: MutableMap<KClass<out () -> Unit>, Unit> = mutableMapOf()
fun runOnce(body: () -> Unit) = runOnceCache.getOrPut(body::class) { body() }
