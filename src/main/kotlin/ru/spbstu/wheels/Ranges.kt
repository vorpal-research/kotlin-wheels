@file:Suppress(Warnings.UNUSED_PARAMETER)
package ru.spbstu.wheels

import kotlinx.warnings.Warnings
import kotlin.math.abs

object PositiveInfinity {
    override fun toString(): String = "+Inf"
}
object NegativeInfinity {
    override fun toString(): String = "-Inf"
}

@Suppress(Warnings.NOTHING_TO_INLINE)
inline operator fun PositiveInfinity.unaryMinus() = NegativeInfinity
@Suppress(Warnings.NOTHING_TO_INLINE)
inline operator fun NegativeInfinity.unaryMinus() = PositiveInfinity

@Suppress(Warnings.NOTHING_TO_INLINE)
inline operator fun PositiveInfinity.unaryPlus() = PositiveInfinity
@Suppress(Warnings.NOTHING_TO_INLINE)
inline operator fun NegativeInfinity.unaryPlus() = NegativeInfinity

val Inf = PositiveInfinity

operator fun NegativeInfinity.rangeTo(inf: PositiveInfinity) = UniversalRange

operator fun Int.rangeTo(inf: PositiveInfinity) = IntInfiniteRangeAfter(this)
operator fun Long.rangeTo(inf: PositiveInfinity) = LongInfiniteRangeAfter(this)

infix fun Int.downTo(inf: NegativeInfinity) = IntInfiniteRangeDownTo(this)
infix fun Long.downTo(inf: NegativeInfinity) = LongInfiniteRangeDownTo(this)

operator fun <T: Comparable<T>> T.rangeTo(inf: PositiveInfinity) = ComparableInfiniteRangeAfter(this)
operator fun <T: Comparable<T>> NegativeInfinity.rangeTo(endInclusive: T) = InfiniteRangeBefore(endInclusive)

object UniversalRange {
    operator fun <T> contains(value: T) = true

    override fun toString(): String = "-Inf..+Inf"
}

interface OpenRangeBefore<T : Comparable<T>> {
    abstract val endInclusive: T
    operator fun contains(value: T) = value <= endInclusive
}

interface OpenRangeAfter<T : Comparable<T>> {
    abstract val start: T
    operator fun contains(value: T) = value >= start
}

open class IntOpenProgression(val from: Int, val step: Int = 1): Sequence<Int> {
    init {
        require(step != 0)
    }

    private inner class TheIterator : Iterator<Int> {
        override fun hasNext(): Boolean = true
        override fun next(): Int = current.also { current += step }
        var current = from
    }

    override fun iterator(): Iterator<Int> = TheIterator()

    override fun toString(): String = when {
        step == 1 -> "$from..+Inf"
        step > 0 -> "$from..+Inf step $step"
        step == -1 -> "$from downTo -Inf"
        step < 0 -> "$from downTo -Inf step ${-step}"
        else -> throw IllegalStateException()
    }

    infix fun step(newStep: Int) = IntOpenProgression(from = from, step = when {
        step < 0 -> -abs(newStep)
        step > 0 -> abs(newStep)
        else -> throw IllegalArgumentException("Zero step is not allowed")
    })
}

open class LongOpenProgression(val from: Long, val step: Long = 1): Sequence<Long> {
    init {
        require(step != 0L)
    }

    private inner class TheIterator : Iterator<Long> {
        override fun hasNext(): Boolean = true
        override fun next(): Long = current.also { current += step }
        var current = from
    }

    override fun iterator(): Iterator<Long> = TheIterator()

    override fun toString(): String = when {
        step == 1L -> "$from..+Inf"
        step > 0L -> "$from..+Inf step $step"
        step == -1L -> "$from downTo -Inf"
        step < 0L -> "$from downTo -Inf step ${-step}"
        else -> throw IllegalStateException()
    }

    infix fun step(newStep: Long) = LongOpenProgression(from = from, step = when {
        step < 0 -> -abs(newStep)
        step > 0 -> abs(newStep)
        else -> throw IllegalArgumentException("Zero step is not allowed")
    })
}

class InfiniteRangeBefore<T: Comparable<T>>(override val endInclusive: T): OpenRangeBefore<T> {
    override fun toString(): String = "-Inf..$endInclusive"
}
class IntInfiniteRangeAfter(override val start: Int):
        OpenRangeAfter<Int>, IntOpenProgression(start)
class LongInfiniteRangeAfter(override val start: Long):
        OpenRangeAfter<Long>, LongOpenProgression(start)
class IntInfiniteRangeDownTo(override val endInclusive: Int):
        OpenRangeBefore<Int>, IntOpenProgression(endInclusive, -1)
class LongInfiniteRangeDownTo(override val endInclusive: Long):
        OpenRangeBefore<Long>, LongOpenProgression(endInclusive, -1)
class ComparableInfiniteRangeAfter<T: Comparable<T>>(override val start: T): OpenRangeAfter<T> {
    override fun toString(): String = "$start..+Inf"
}

fun <T> List<T>.slice(range: IntInfiniteRangeAfter) = run {
    require(range.start >= 0) { "Index range cannot be negative" }
    subList(range.start, size.coerceAtLeast(range.start).coerceAtLeast(1)).toList()
}

fun <T> List<T>.slice(range: InfiniteRangeBefore<Int>) = run {
    require(range.endInclusive >= 0) { "Index range cannot be negative" }
    subList(0, range.endInclusive + 1).toList()
}
