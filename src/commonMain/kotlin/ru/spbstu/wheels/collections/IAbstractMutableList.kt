package ru.spbstu.wheels.collections

class MutableSubList<T>(val list: MutableList<T>, val fromIndex: Int, var toIndex: Int): IAbstractMutableList<T> {
    init {
        checkBounds(fromIndex in list.indices)
        checkBounds(toIndex in 0 .. list.size)
        checkBounds(toIndex >= fromIndex)
    }

    override fun clear() {
        for (i in (toIndex - 1) downTo fromIndex) {
            list.removeAt(i)
        }
        toIndex = fromIndex
    }

    private fun checkBaseIndex(index: Int, includeEnd: Boolean = false) {
        checkBounds(index >= fromIndex)
        if (!includeEnd) checkBounds(index < toIndex)
        else checkBounds(index <= toIndex)
    }

    override val size: Int
        get() = toIndex - fromIndex

    private fun adjustIndex(index: Int, includeEnd: Boolean = false): Int {
        val adjustedIndex = fromIndex + index
        checkBaseIndex(adjustedIndex, includeEnd)
        return adjustedIndex
    }

    override fun get(index: Int): T = list.get(adjustIndex(index))

    override fun add(index: Int, element: T) =
        list.add(adjustIndex(index, includeEnd = true), element).also { ++toIndex }

    override fun removeAt(index: Int): T =
        list.removeAt(adjustIndex(index)).also { --toIndex }

    override fun set(index: Int, element: T): T = list.set(adjustIndex(index), element)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        checkBounds(toIndex >= fromIndex)
        val adjustedFrom = this.fromIndex + fromIndex
        val adjustedTo = this.fromIndex + toIndex
        checkBaseIndex(adjustedFrom)
        checkBaseIndex(adjustedTo)
        return MutableSubList(list, adjustedFrom, adjustedTo)
    }

    override fun equals(other: Any?): Boolean = listEquals(other)
    override fun hashCode(): Int = listHashCode()
    override fun toString(): String = listToString()
}

interface IAbstractMutableList<T>: IAbstractList<T>, MutableList<T>, IAbstractMutableCollection<T> {
    override fun add(element: T): Boolean {
        add(size, element)
        return true
    }

    override fun addAll(elements: Collection<T>): Boolean = addAll(size, elements)

    override fun addAll(index: Int, elements: Collection<T>): Boolean = elements.any { add(it) }

    override fun remove(element: T): Boolean = when (val index = indexOf(element)) {
        -1 -> false
        else -> {
            removeAt(index)
            true
        }
    }

    override fun listIterator(): MutableListIterator<T> = listIterator(0)

    override fun listIterator(index: Int): MutableListIterator<T> = object: IndexedMutableListIterator<T>(index, size) {
        override fun add(index: Int, element: T) {
            this@IAbstractMutableList.add(index, element)
        }

        override fun removeAt(index: Int) {
            this@IAbstractMutableList.removeAt(index)
        }

        override fun set(index: Int, element: T) {
            this@IAbstractMutableList.set(index, element)
        }

        override fun get(index: Int): T = this@IAbstractMutableList.get(index)
    }
    override fun iterator(): MutableIterator<T> = listIterator()

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> =
        MutableSubList(this, fromIndex, toIndex)

    override fun clear() {
        for (i in lastIndex downTo 0) {
            removeAt(i)
        }
    }
    override fun removeAll(elements: Collection<T>): Boolean = super.removeAll(elements)
    override fun retainAll(elements: Collection<T>): Boolean = super.retainAll(elements)

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
    override fun toString(): String

    abstract class Impl<T> : IAbstractMutableList<T> {
        override fun equals(other: Any?): Boolean = listEquals(other)
        override fun hashCode(): Int = listHashCode()
        override fun toString(): String = listToString()
    }

}