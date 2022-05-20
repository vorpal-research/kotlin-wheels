package ru.spbstu.wheels.collections

interface IAbstractMutableSet<T>: IAbstractSet<T>, MutableSet<T>, IAbstractMutableCollection<T> {
    override fun addAll(elements: Collection<T>): Boolean = super.addAll(elements)
    override fun clear() = super.clear()
    override fun removeAll(elements: Collection<T>): Boolean = super.removeAll(elements)
    override fun retainAll(elements: Collection<T>): Boolean = super.retainAll(elements)

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
    override fun toString(): String
}
