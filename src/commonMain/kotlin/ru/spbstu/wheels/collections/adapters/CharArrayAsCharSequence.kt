package ru.spbstu.wheels.collections.adapters

import ru.spbstu.wheels.collections.ICharSequence
import ru.spbstu.wheels.collections.checkBounds

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
