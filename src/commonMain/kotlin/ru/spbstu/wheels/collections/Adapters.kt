package ru.spbstu.wheels.collections

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
