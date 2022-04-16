package ru.spbstu.wheels

interface Stack<T> {
    fun push(value: T)
    fun pop(): T
    val top: T?
    val size: Int
    fun isEmpty(): Boolean

    fun clear()
}

operator fun <T> Stack<T>.plusAssign(element: T) = push(element)
operator fun <T> Stack<T>.plusAssign(iterable: Iterable<T>) = iterable.forEach { push(it) }
operator fun <T> Stack<T>.plusAssign(sequence: Sequence<T>) = sequence.forEach { push(it) }

fun <T> Stack<T>.isNotEmpty() = !isEmpty()

class DefaultStack<T>(val list: MutableList<T> = mutableListOf()): Stack<T> {
    override fun push(value: T) { list.add(value) }

    override fun pop(): T = top?.also { list.removeAt(list.lastIndex) }
            ?: throw NoSuchElementException("Stack is empty")

    override val top: T?
        get() = list.lastOrNull()

    override val size: Int
        get() = list.size
    override fun isEmpty() = list.isEmpty()
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is DefaultStack<*> -> false
        list != other.list -> false
        else -> true
    }

    override fun hashCode(): Int = list.hashCode()

    override fun clear() {
        list.clear()
    }
}

fun <T> stack(): Stack<T> = DefaultStack()
