package ru.spbstu.wheels

interface InlineArray<Outer, Inner>: Collection<Outer> {
    val realArray: Array<Inner>

    override val size: Int
        get() = realArray.size

    override fun contains(element: Outer): Boolean
    override fun containsAll(elements: Collection<Outer>): Boolean = elements.all { contains(it) }
    override fun isEmpty(): Boolean = size == 0
    operator fun get(index: Int): Outer

    class Iterator<Outer, Inner>(val array: InlineArray<Outer, Inner>): kotlin.collections.Iterator<Outer> {
        var index = 0
        override fun hasNext(): Boolean = index < array.size
        override fun next(): Outer = array[index].also { ++index }
    }

    override fun iterator(): kotlin.collections.Iterator<Outer> = Iterator(this)
}

inline fun <Outer, Inner> InlineArray<Outer, Inner>.defaultToString(converter: (Inner) -> Outer): String {
    if(size == 0) return "[]"
    val sb = StringBuilder("[")
    sb.append(converter(realArray[0]))
    for(i in 1 until size) sb.append(", ").append(converter(realArray[i]))
    sb.append("]")
    return "$sb"
}

inline fun <Outer, Inner> InlineArray<Outer, Inner>.defaultContains(element: Outer, unwrap: (Outer) -> Inner): Boolean =
    realArray.any { it == unwrap(element) }

val <Outer, Inner> InlineArray<Outer, Inner>.lastIndex
    get() = size - 1

fun <Outer, Inner> InlineArray<Outer, Inner>.getOrNull(index: Int) = when {
    index !in 0..lastIndex -> null
    else -> get(index)
}
