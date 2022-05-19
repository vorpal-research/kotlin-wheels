package ru.spbstu.wheels.collections

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
public inline fun checkBounds(value: Boolean): Unit {
    contract {
        returns() implies value
    }
    checkBounds(value) { "Check failed." }
}

@OptIn(ExperimentalContracts::class)
public inline fun checkBounds(value: Boolean, lazyMessage: () -> Any): Unit {
    contract {
        returns() implies value
    }
    if (!value) {
        val message = lazyMessage()
        throw IndexOutOfBoundsException(message.toString())
    }
}

interface IAbstractCollection<out T>: Collection<T> {
    override fun contains(element: @UnsafeVariance T): Boolean =
        iteratorContains(iterator(), element)

    override fun containsAll(elements: Collection<@UnsafeVariance T>): Boolean =
        elements.all { contains(it) }

    override fun isEmpty(): Boolean = size == 0
}

interface IAbstractMutableCollection<T>: MutableCollection<T>, IAbstractCollection<T> {
    override fun addAll(elements: Collection<T>): Boolean =
        elements.any { add(it) }

    override fun clear() { removeAll(this) }

    override fun removeAll(elements: Collection<T>): Boolean {
        if (elements.size > size) return removeAll { it in elements }
        else return elements.any { remove(it) }
    }

    override fun retainAll(elements: Collection<T>): Boolean =
        removeAll { it in elements }
}

class SimpleSubList<T>(val list: List<T>, val fromIndex: Int, val toIndex: Int): IAbstractList<T> {
    init {
        checkBounds(fromIndex in list.indices)
        checkBounds(toIndex in 0 .. list.size)
        checkBounds(toIndex >= fromIndex)
    }

    private fun checkBaseIndex(index: Int) {
        checkBounds(index >= fromIndex)
        checkBounds(index < toIndex)
    }

    override val size: Int
        get() = toIndex - fromIndex

    override fun get(index: Int): T {
        val adjustedIndex = fromIndex + index
        checkBaseIndex(adjustedIndex)
        return list[adjustedIndex]
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> {
        checkBounds(toIndex >= fromIndex)
        val adjustedFrom = this.fromIndex + fromIndex
        val adjustedTo = this.fromIndex + toIndex
        checkBaseIndex(adjustedFrom)
        checkBaseIndex(adjustedTo)
        return SimpleSubList(list, adjustedFrom, adjustedTo)
    }
}

interface IAbstractList<T>: IAbstractCollection<T>, List<T> {
    private inline fun forEachIndexed(body: (index: Int, t: T) -> Unit) {
        for (i in 0 until size) body(i, get(i))
    }

    private inline fun forEachIndexedReversed(body: (Int, T) -> Unit) {
        for (i in lastIndex downTo 0) body(i, get(i))
    }

    override fun indexOf(element: T): Int {
        forEachIndexed { index, t ->
            if (element == t) return index
        }
        return -1
    }

    override fun lastIndexOf(element: T): Int {
        forEachIndexedReversed { index, t ->
            if (element == t) return index
        }
        return -1
    }

    override fun listIterator(): ListIterator<T> = listIterator(0)
    override fun listIterator(index: Int): ListIterator<T> =
        IndexedListIterator(0, size) { get(it) }

    override fun iterator(): Iterator<T> = listIterator()

    override fun subList(fromIndex: Int, toIndex: Int): List<T> = SimpleSubList(this, fromIndex, toIndex)

    override fun contains(element: T): Boolean = indexOf(element) != -1

    override fun containsAll(elements: Collection<T>): Boolean =
        super.containsAll(elements)

    override fun isEmpty(): Boolean =
        super.isEmpty()
}
