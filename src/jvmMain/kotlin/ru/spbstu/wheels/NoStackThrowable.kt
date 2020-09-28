package ru.spbstu.wheels

actual open class NoStackThrowable
actual constructor(message: String?, cause: Throwable?) :
        Throwable(message, cause,
                enableSuppression = false,
                writableStackTrace = false) {
    actual constructor() : this(null, null)
    actual constructor(message: String) : this(message, null)
    actual constructor(cause: Throwable) : this(cause.message, cause)
}

