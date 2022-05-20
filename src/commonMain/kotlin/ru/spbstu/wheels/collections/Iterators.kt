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

abstract class IndexedMutableListIterator<T>(startIndex: Int, endIndex: Int):
        IndexedListIterator<T>(startIndex, endIndex), MutableListIterator<T> {
    constructor(endIndex: Int): this(0, endIndex)

    private var lastIndex: Int = -1

    override fun next(): T {
        lastIndex = index
        return super.next()
    }

    override fun previous(): T {
        lastIndex = index - 1
        return super.previous()
    }

    override fun add(element: T) {
        add(index, element)
        ++index
    }

    override fun remove() {
        if (lastIndex == -1) throw NoSuchElementException()
        removeAt(lastIndex)
    }

    override fun set(element: T) {
        if (lastIndex == -1) throw NoSuchElementException()
        set(lastIndex, element)
    }

    abstract fun add(index: Int, element: T)
    abstract fun removeAt(index: Int)
    abstract operator fun set(index: Int, element: T)
}

inline fun <T> iteratorIndexOf(iterator: Iterator<T>, predicate: (T) -> Boolean): Int {
    var ix = 0
    for (e in iterator) {
        if (predicate(e)) return ix
        ++ix
    }
    return -1
}

fun <T> iteratorIndexOf(iterator: Iterator<T>, value: T): Int = iteratorIndexOf(iterator) { it == value }

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

inline fun <A, B> mappingIterator(base: Iterator<A>,
                                  crossinline body: (A) -> B): Iterator<B> = object : Iterator<B> {
    override fun hasNext(): Boolean = base.hasNext()
    override fun next(): B = body(base.next())
}

inline fun <A, B> mappingIterator(base: MutableIterator<A>,
                                  crossinline body: (A) -> B): MutableIterator<B> = object : MutableIterator<B> {
    override fun hasNext(): Boolean = base.hasNext()
    override fun next(): B = body(base.next())
    override fun remove() = base.remove()
}


