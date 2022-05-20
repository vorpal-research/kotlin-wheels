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

    override fun equals(other: Any?): Boolean = listEquals(other)
    override fun hashCode(): Int = listHashCode()
    override fun toString(): String = listToString()
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

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
    override fun toString(): String

    fun <T> IAbstractList<T>.listEquals(other: Any?): Boolean {
        if (other !is List<*>) return false
        if (other.size != size) return false
        for (i in indices) {
            if (this[i] != other[i]) return false
        }
        return true
    }

    fun <T> IAbstractList<T>.listHashCode(): Int {
        var hash = 1
        for (i in indices) {
            hash = 31 * hash + this[i].hashCode()
        }
        return hash
    }

    fun <T> IAbstractList<T>.listToString(): String {
        val sb = StringBuilder("[")
        forEachIndexed { i, t ->
            if (i != 0) sb.append(", ")
            sb.append(t)
        }
        sb.append(']')
        return sb.toString()
    }

}




