package ru.spbstu.wheels.collections

interface IAbstractMutableCollection<T>: MutableCollection<T>, IAbstractCollection<T> {
    override fun addAll(elements: Collection<T>): Boolean =
        elements.any { add(it) }

    override fun clear() { removeAll(this) }

    override fun removeAll(elements: Collection<T>): Boolean = when {
        elements.size > size -> removeAll { it in elements }
        else -> elements.any { remove(it) }
    }

    override fun retainAll(elements: Collection<T>): Boolean =
        removeAll { it !in elements }

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
    override fun toString(): String
}
