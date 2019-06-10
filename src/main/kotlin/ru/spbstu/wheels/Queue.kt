package ru.spbstu.wheels

import kotlinx.warnings.Warnings

interface Queue<T> {
    fun put(value: T)
    fun take(): T
    val current: T?
    val size: Int
    fun isEmpty(): Boolean = size == 0
}

operator fun <T> Queue<T>.plusAssign(element: T) = put(element)
operator fun <T> Queue<T>.plusAssign(iterable: Iterable<T>) = iterable.forEach { put(it) }
operator fun <T> Queue<T>.plusAssign(sequence: Sequence<T>) = sequence.forEach { put(it) }

fun <T> Queue<T>.isNotEmpty() = !isEmpty()

interface Deque<T> {
    fun putFirst(value: T)
    fun putLast(value :T)
    fun takeFirst(): T
    fun takeLast(): T
    val first: T?
    val last: T?
    val size: Int
    fun isEmpty(): Boolean = size == 0
}

fun <T> Deque<T>.isNotEmpty() = !isEmpty()

class ArrayDeque<T>: Deque<T>, Queue<T> {
    companion object {
        private const val INITIAL_CAPACITY = 16
    }

    private var data: TArray<T> = TArray(INITIAL_CAPACITY)
    private var firstPtr: Int = 0
    private var lastPtr: Int = 0

    private inline val capacity get() = data.size
    // special properties that have values wrapped-around capacity
    // so you can do simple ++ or -- on them and it cycles automatically
    private inline var firstIndex
        get() = firstPtr
        set(value) { firstPtr = value and (capacity - 1) }
    private inline var lastIndex
        get() = (lastPtr - 1) and (capacity - 1)
        set(value) { lastPtr = (value + 1) and (capacity - 1) }

    private fun doubleCapacity() {
        check(firstPtr == lastPtr)
        val p = firstPtr
        val n = capacity
        val r = n - p // number of elements to the right of p
        val newCapacity = capacity * 2
        if (newCapacity < 0)
            throw IllegalStateException("Sorry, deque too big")
        val a = TArray<T>(newCapacity)
        data.copyInto(a, 0, p, p + r) // data[p..p+r] -> a[0..r]
        data.copyInto(a, r, 0, p) // data[0..p] -> a[r..r+p]
        data = a
        firstPtr = 0
        lastPtr = n
    }

    @Suppress(Warnings.UNCHECKED_CAST)
    override var first: T?
        get() = if(isEmpty()) null else data[firstIndex]
        private set(value) { data[firstIndex] = value }

    @Suppress(Warnings.UNCHECKED_CAST)
    override var last: T?
        get() = if(isEmpty()) null else data[lastIndex]
        private set(value) { data[lastIndex] = value }

    override fun putFirst(value: T) {
        --firstIndex
        first = value
        if(firstPtr == lastPtr) doubleCapacity()
    }

    override fun putLast(value: T) {
        ++lastIndex
        last = value
        if(firstPtr == lastPtr) doubleCapacity()
    }

    override fun takeFirst(): T = first?.also {
        first = null
        ++firstIndex
    } ?: throw NoSuchElementException("takeFirst")

    override fun takeLast(): T = last?.also {
        last = null
        --lastIndex
    } ?: throw NoSuchElementException("takeLast")

    override fun isEmpty(): Boolean = firstPtr == lastPtr

    override val size: Int
        get() = (lastPtr - firstPtr) and (capacity - 1)

    /* Queue implementation */

    override fun put(value: T) = putFirst(value)

    override fun take(): T = takeLast()

    override val current: T?
        get() = last
}

fun <T> queue(): Queue<T> = ArrayDeque()
fun <T> deque(): Deque<T> = ArrayDeque()
