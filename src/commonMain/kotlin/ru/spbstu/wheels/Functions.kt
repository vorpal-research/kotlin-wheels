package ru.spbstu.wheels

inline fun <T> (() -> T).runAtMostOnce(): () -> T {
    val lazyRes = lazy(LazyThreadSafetyMode.SYNCHRONIZED, this)
    return { lazyRes.value }
}
