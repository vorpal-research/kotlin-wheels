package ru.spbstu.wheels

class NullableMonad {
    inline fun <T, U> T?.map(body: (T) -> U): U? = this?.let(body)
    inline fun <T, U> T?.flatMap(body: (T) -> U?): U? = this?.let(body)
    inline fun <T> T?.filter(body: (T) -> Boolean): T? = when {
        this === null || !body(this) -> null
        else -> this
    }
}
