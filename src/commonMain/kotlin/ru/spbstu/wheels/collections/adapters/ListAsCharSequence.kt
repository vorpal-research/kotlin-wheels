package ru.spbstu.wheels.collections.adapters

import ru.spbstu.wheels.collections.ICharSequence

class ListAsCharSequence(val list: List<Char>): ICharSequence.Impl() {
    override val length: Int
        get() = list.size

    override fun get(index: Int): Char = list[index]
}

fun List<Char>.asCharSequence(): CharSequence = ListAsCharSequence(this)
