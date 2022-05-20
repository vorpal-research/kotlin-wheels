package ru.spbstu.wheels.collections

import ru.spbstu.wheels.setHashCode

interface IAbstractSet<out T>: IAbstractCollection<T>, Set<T> {
    override fun isEmpty(): Boolean = super.isEmpty()
    override fun containsAll(elements: Collection<@UnsafeVariance T>) = super.containsAll(elements)

    override fun contains(element: @UnsafeVariance T): Boolean

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
    override fun toString(): String

    fun <T> IAbstractSet<T>.setEquals(other: Any?): Boolean {
        if (other !is Set<*>) return false
        if (other.size != size) return false
        return containsAll(other)
    }

    fun <T> IAbstractSet<T>.setHashCode(): Int = ru.spbstu.wheels.setHashCode(this)

    fun <T> IAbstractSet<T>.setToString(): String = collectionToString("{", "}")
}
