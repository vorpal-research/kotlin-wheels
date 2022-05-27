package ru.spbstu.wheels

private object Identity {
    override fun hashCode(): Int {
        return super<Any>.hashCode()
    }

    private val getHashOf = js("function pro(obj) { return Object.getPrototypeOf(obj).hashCode }")
    private val identityHashCode = getHashOf(Identity)

    fun identityHashCode(value: Any?): Int = identityHashCode.call(value)
}

actual fun identityHashCode(value: Any?): Int {
    return Identity.identityHashCode(value)
}
