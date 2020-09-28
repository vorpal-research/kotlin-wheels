package ru.spbstu.wheels

expect open class NoStackThrowable(message: String?, cause: Throwable?): Throwable {
    constructor()
    constructor(message: String)
    constructor(cause: Throwable)
}
