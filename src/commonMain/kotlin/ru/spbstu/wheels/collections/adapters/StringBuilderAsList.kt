package ru.spbstu.wheels.collections.adapters

import ru.spbstu.wheels.collections.IAbstractMutableList

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

