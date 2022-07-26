package ru.spbstu.wheels.collections.adapters

import ru.spbstu.wheels.collections.IAbstractList
import ru.spbstu.wheels.collections.IAbstractMutableList

class MapAsList<T>(val inner: Map<Int, T>): IAbstractList.Impl<T?>() {
    override val size: Int
        get() = when(inner.size) {
            0 -> 0
            else -> inner.maxOf { it.key } + 1
        }

    override fun get(index: Int): T? = inner.get(index)
}

