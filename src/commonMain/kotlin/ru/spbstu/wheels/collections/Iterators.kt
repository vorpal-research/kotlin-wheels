package ru.spbstu.wheels.collections

fun interface InfiniteIterator<T>: Iterator<T> {
    override fun hasNext(): Boolean = true
    override fun next(): T
}

abstract class IndexedIterator<T>(startIndex: Int, val endIndex: Int): Iterator<T> {
    constructor(endIndex: Int): this(0, endIndex)

    init {
        check(startIndex <= endIndex)
    }

    var index = startIndex
    override fun hasNext(): Boolean = index < endIndex

    override fun next(): T = this[index]

    abstract operator fun get(index: Int): T
}

inline fun <T> IndexedIterator(startIndex: Int, endIndex: Int, crossinline body: (index: Int) -> T) =
    object: IndexedIterator<T>(startIndex, endIndex) {
        override fun get(index: Int): T = body(index)
    }

inline fun <T> IndexedIterator(endIndex: Int, crossinline body: (index: Int) -> T) =
    IndexedIterator(0, endIndex, body)

abstract class IndexedListIterator<T>(val startIndex: Int, val endIndex: Int): ListIterator<T> {
    constructor(endIndex: Int): this(0, endIndex)

    init {
        check(startIndex <= endIndex)
    }

    var index = startIndex
    override fun hasNext(): Boolean = index < endIndex

    override fun next(): T = this[index].also { index++ }

    override fun hasPrevious(): Boolean = index > startIndex

    override fun previous(): T = this[--index]

    override fun nextIndex(): Int = index
    override fun previousIndex(): Int = index - 1

    abstract operator fun get(index: Int): T
}

inline fun <T> IndexedListIterator(startIndex: Int, endIndex: Int, crossinline body: (index: Int) -> T) =
    object: IndexedListIterator<T>(startIndex, endIndex) {
        override fun get(index: Int): T = body(index)
    }

inline fun <T> IndexedListIterator(endIndex: Int, crossinline body: (index: Int) -> T) =
    IndexedListIterator(0, endIndex, body)

fun <T> iteratorIndexOf(iterator: Iterator<T>, value: T): Int {
    var ix = 0
    for (e in iterator) {
        if (e == value) return ix
        ++ix
    }
    return -1
}

fun <T> iteratorContains(iterator: Iterator<T>, value: T): Boolean =
    iteratorIndexOf(iterator, value) != -1

fun <T> iteratorEquals(lhv: Iterator<T>, rhv: Iterator<T>): Boolean {
    while (true) {
        val lhvHasNext = lhv.hasNext()
        val rhvHasNext = rhv.hasNext()
        if (lhvHasNext != rhvHasNext) return false
        if (!lhvHasNext) return true

        if (lhv.next() != rhv.next()) return false
    }
}


