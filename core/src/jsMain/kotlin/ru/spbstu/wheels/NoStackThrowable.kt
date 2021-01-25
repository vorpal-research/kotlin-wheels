package ru.spbstu.wheels

// js Errors do not collect stack during construction, so we don't have to do anything
actual open class NoStackThrowable
    actual constructor(message: String?, cause: Throwable?) : Throwable(message, cause) {
    actual constructor() : this(null, null)
    actual constructor(message: String) : this(message, null)
    actual constructor(cause: Throwable) : this(cause.message, cause)
}