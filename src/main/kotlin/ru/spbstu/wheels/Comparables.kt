package ru.spbstu.wheels

inline class ComparatorScope<T>(val comparator: Comparator<T>) {
    operator fun T.compareTo(that: T) = comparator.compare(this, that)

    fun Iterable<T>.max() = maxWith(comparator)
    fun Collection<T>.sorted() = sortedWith(comparator)
    fun MutableList<T>.sort() = sortWith(comparator)
}

inline fun <T, R> Comparator<T>.use(body: ComparatorScope<T>.() -> R) = ComparatorScope(this).body()

fun combineCompares(cmp0: Int, cmp1: Int): Int = when {
    cmp0 != 0 -> cmp0
    else -> cmp1
}

fun combineCompares(cmp0: Int, cmp1: Int, cmp2: Int): Int = when {
    cmp0 != 0 -> cmp0
    cmp1 != 0 -> cmp1
    else -> cmp2
}

fun combineCompares(vararg cmps: Int): Int = cmps.find { it != 0 } ?: 0

fun <A: Comparable<A>> Pair<A, A>.sorted() = when {
    first <= second -> this
    else -> second to first
}

fun <A> Pair<A, A>.sortedWith(cmp: Comparator<A>) = cmp.use {
    when {
        first <= second -> this@sortedWith
        else -> second to first
    }
}

fun <A> Triple<A, A, A>.sortedWith(cmp: Comparator<A>) = cmp.use {
    when {
        first <= second -> when {
            // first <= second && second <= third
            second <= third -> this@sortedWith // Triple(first, second, third)
            // first <= second && third < second && first <= third
            // => first <= third < second
            first <= third -> Triple(first, third, second)
            // first <= second && third < second && third < first
            // => third < first <= second
            else -> Triple(third, first, second)
        }
        // second < first
        else -> when {
            // second < first && third <= second
            // => third <= second < first
            third <= second -> Triple(third, second, first)
            // second < first && second < third && third <= first
            // => second < third <= first
            third <= first -> Triple(second, third, first)
            // second < first && second < third && first < third
            // => second < first < third
            else -> Triple(second, first, third)
        }
    }
}

fun <A: Comparable<A>> Triple<A, A, A>.sorted() = sortedWith(naturalOrder())
