package ru.spbstu.wheels

actual open class NoStackThrowable
actual constructor(message: String?, cause: Throwable?) :
        Throwable(message, cause) {
    actual constructor() : this(null, null)
    actual constructor(message: String) : this(message, null)
    actual constructor(cause: Throwable) : this(cause.message, cause)

    override fun fillInStackTrace(): NoStackThrowable = this
}

