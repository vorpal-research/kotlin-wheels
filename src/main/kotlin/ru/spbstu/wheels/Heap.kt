package ru.spbstu.wheels

import kotlinx.warnings.Warnings

interface Heap<T>: Queue<T> {
    val comparator: Comparator<T>
}

fun <T> heap(comparator: Comparator<T>): Heap<T> = BinaryHeap(comparator)
fun <T: Comparable<T>> heap(): Heap<T> = BinaryHeap()

class BinaryHeap<T>(override val comparator: Comparator<T>): Heap<T>, AbstractMutableCollection<T>() {
    companion object {
        const val MIN_SIZE = 16
    }

    private var data: TArray<T> = TArray(MIN_SIZE)
    private inline val capacity: Int get() = data.size
    override var size: Int = 0

    private fun growTo(size: Int) {
        var newCapacity = capacity
        while(newCapacity <= size) newCapacity *= 2

        data = data.copyOf(newCapacity)
    }

    private inline val Int.leftIndex get() = this * 2 + 1
    private inline val Int.rightIndex get() = (this + 1) * 2
    private inline val Int.parentIndex get() = (this - 1) / 2

    @Suppress(Warnings.UNCHECKED_CAST)
    private operator fun Any?.compareTo(other: Any?) =
            comparator.compare(this as T, other as T)

    override fun add(element: T): Boolean {
        val i = size
        ++size
        if(capacity < size) growTo(size)
        if(i == 0) data[0] = element
        else siftUp(i, element)
        return true
    }

    fun removeAt(i: Int): T? {
        val s = --size
        if(s == i) data[i] = null
        else {
            val moved = data[s]
            data[s] = null
            siftDown(i, moved)
            if(data[i] === moved) {
                siftUp(i, moved)
                @Suppress(Warnings.UNCHECKED_CAST)
                if(data[i] !== moved) return moved as T
            }
        }
        return null
    }

    override fun iterator(): MutableIterator<T> = object : MutableIterator<T> {
        var index = 0

        override fun hasNext(): Boolean = index < size
        @Suppress(Warnings.UNCHECKED_CAST)
        override fun next(): T = data[index].also { ++index } as T

        override fun remove() {
            check(index > 0)
            removeAt(index - 1)
        }
    }

    private fun siftUp(kk: Int, v: T?) {
        var k = kk
        while(k > 0) {
            val parent = k.parentIndex
            val e = data[parent]
            if(v > e) break
            data[k] = e
            k = parent
        }
        data[k] = v
    }

    private fun siftDown(kk: Int, v: T?) {
        val half = size / 2
        var k = kk
        while(k < half) {
            var child = k.leftIndex
            var c = data[child]
            val right = child + 1
            if(right < size && c > data[right]) {
                child = right
                c = data[child]
            }
            if(v <= c) break
            data[k] = c
            k = child
        }
        data[k] = v
    }

    /* Queue implementation */
    override fun put(value: T) { add(value) }
    override fun take(): T = current?.also { removeAt(0) } ?: throw IllegalArgumentException("Heap is empty")
    @Suppress(Warnings.UNCHECKED_CAST)
    override val current: T?
        get() = data[0]

    override fun isEmpty(): Boolean = size == 0
}

fun <T: Comparable<T>> BinaryHeap() = BinaryHeap<T>(naturalOrder())
