package ru.spbstu.wheels.collections

import ru.spbstu.wheels.get
import ru.spbstu.wheels.size

class CharSequenceAsList(val charSequence: CharSequence): IAbstractList.Impl<Char>() {
    override val size: Int
        get() = charSequence.length

    override fun get(index: Int): Char = charSequence[index]
}

fun CharSequence.asList(): List<Char> = CharSequenceAsList(this)

class StringBuilderAsList(val sb: StringBuilder): IAbstractMutableList.Impl<Char>() {
    override val size: Int
        get() = sb.length

    override fun get(index: Int): Char = sb.get(index)
    override fun add(index: Int, element: Char) { sb.insert(index, element) }
    override fun removeAt(index: Int): Char {
        val cur = get(index)
        sb.deleteAt(index)
        return cur
    }

    override fun set(index: Int, element: Char): Char {
        val cur = get(index)
        sb.set(index, element)
        return cur
    }

    override fun clear() {
        sb.clear()
    }
}

fun StringBuilder.asList(): MutableList<Char> = StringBuilderAsList(this)

class ListAsCharSequence(val list: List<Char>): ICharSequence.Impl() {
    override val length: Int
        get() = list.size

    override fun get(index: Int): Char = list[index]
}

fun List<Char>.asCharSequence(): CharSequence = ListAsCharSequence(this)

class CharArrayAsCharSequence(val array: CharArray,
                              val offset: Int = 0,
                              val limit: Int = array.size): ICharSequence.Impl() {
    private fun checkIndex(index: Int) = index.also {
        checkBounds(index in array.indices)
        checkBounds(index < limit)
    }
    private fun checkLimit(limit: Int) = limit.also {
        checkBounds(limit in 0..array.size)
    }

    init {
        checkIndex(offset)
        checkLimit(limit)
    }

    override val length: Int
        get() = limit - offset

    override fun get(index: Int): Char = array[checkIndex(index + offset)]

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        checkBounds(startIndex in 0..endIndex)
        checkBounds(startIndex < length)
        checkBounds(endIndex <= length)
        return CharArrayAsCharSequence(array, offset + startIndex, offset + endIndex)
    }
}

fun CharArray.asCharSequence(offset: Int = 0, limit: Int = size): CharSequence =
    CharArrayAsCharSequence(this, offset, limit)

fun IntRange.asList(): List<Int> = object : IAbstractList.Impl<Int>() {
    override val size: Int
        get() = this@asList.size

    override fun get(index: Int): Int = this@asList.get(index)
}

