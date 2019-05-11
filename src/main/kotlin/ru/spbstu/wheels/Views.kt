@file: Suppress(Warnings.NOTHING_TO_INLINE)
package ru.spbstu.wheels

import kotlinx.warnings.Warnings

fun <T> List<T>.view() = ListView(this)

inline class ListView<T>(val list: List<T>) {
    inline operator fun iterator(): Iterator<T> = list.iterator()

    inline operator fun get(index: Int) = list.get(index)
    inline operator fun get(range: IntRange) = list.subList(range.start, range.endInclusive + 1)
    inline operator fun get(range: IntInfiniteRangeAfter) =
            list.subList(range.start, list.size)
    inline operator fun get(range: InfiniteRangeBefore<Int>) =
            list.subList(0, range.endInclusive + 1)
}

@JvmName("mutableView")
fun <T> MutableList<T>.view() = MutableListView(this)

inline class MutableListView<T>(val list: MutableList<T>) {
    inline operator fun iterator(): MutableIterator<T> = list.iterator()

    inline operator fun get(index: Int) = list.get(index)
    inline operator fun get(range: IntRange) = list.subList(range.start, range.endInclusive + 1)
    inline operator fun get(range: IntInfiniteRangeAfter) =
            list.subList(range.start, list.size)
    inline operator fun get(range: InfiniteRangeBefore<Int>) =
            list.subList(0, range.endInclusive + 1)
}
