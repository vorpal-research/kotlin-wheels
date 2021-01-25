package ru.spbstu.wheels

// seems like there is no way of avoiding collecting stack trace on native now
actual open class NoStackThrowable actual constructor(message: String?, cause: Throwable?) : Throwable() {
    actual constructor() : this(null, null)

    actual constructor(message: String) : this(message, null)

    actual constructor(cause: Throwable) : this(cause.message, cause)
}