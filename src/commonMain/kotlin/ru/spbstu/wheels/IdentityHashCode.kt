package ru.spbstu.wheels

expect fun identityHashCode(value: Any?): Int

class IdentityBox(val value: Any?) {
    override fun hashCode(): Int = identityHashCode(value)
    override fun equals(other: Any?): Boolean =
            value === other || other is IdentityBox && other.value === value
    override fun toString(): String = value.toString()
}
