package ru.spbstu.wheels.collections.adapters

import ru.spbstu.wheels.collections.IAbstractList

class CharSequenceAsList(val charSequence: CharSequence): IAbstractList.Impl<Char>() {
    override val size: Int
        get() = charSequence.length

    override fun get(index: Int): Char = charSequence[index]
}

fun CharSequence.asList(): List<Char> = CharSequenceAsList(this)
