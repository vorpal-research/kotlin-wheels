@file: Suppress(Warnings.NOTHING_TO_INLINE)
package ru.spbstu.wheels

import kotlinx.warnings.Warnings

inline fun Int.asBits() = IntBits(this)

//putting these guys as members triggers
//https://youtrack.jetbrains.com/issue/KT-31431
val IntBits.Companion.Zero get() = 0.asBits()
val IntBits.Companion.One get() = 1.asBits()
val IntBits.Companion.AllOnes get() = (-1).asBits()
val SIZE get() = Int.SIZE_BITS

inline class IntBits
@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
@PublishedApi
internal constructor(val data: Int) {
    companion object{}

    inline fun asInt() = data

    inline infix fun shl(bitCount: Int): IntBits = IntBits(data shl bitCount)
    inline infix fun shr(bitCount: Int): IntBits = IntBits(data ushr bitCount)
    inline infix fun and(that: IntBits): IntBits = IntBits(data and that.data)
    inline infix fun andNot(that: IntBits): IntBits = IntBits(data and that.data.inv())
    inline infix fun or(that: IntBits): IntBits = IntBits(data or that.data)
    inline infix fun xor(that: IntBits): IntBits = IntBits(data xor that.data)
    inline fun inv(): IntBits = data.inv().asBits()
    inline fun reverse(): IntBits = Integer.reverse(data).asBits()

    inline val popCount: Int get() = Integer.bitCount(data)
    inline val lowestBitSet get() = IntBits(Integer.lowestOneBit(data))
    inline val highestBitSet get() = IntBits(Integer.highestOneBit(data))
    inline val numberOfLeadingZeros: Int get() = Integer.numberOfLeadingZeros(data)
    inline val numberOfTrailingZeros: Int get() = Integer.numberOfTrailingZeros(data)

    inline fun forEachOneBit(body: (IntBits) -> Unit) {
        var mask = this
        while(mask != Zero) {
            val bit = mask.lowestBitSet
            body(bit)
            mask = mask andNot bit
        }
    }

    // DANGER: boxing!
    fun oneBitSequence() = sequence { forEachOneBit { yield(it) } }

    inline operator fun get(index: Int) = data and (1 shl index) != 0
    inline fun set(index: Int) = this or (One shl index)
    inline fun clear(index: Int) = this andNot (One shl index)
    inline fun slice(from: Int = 0, toExclusive: Int = SIZE): IntBits {
        require(from >= 0)
        require(toExclusive >= from)
        require(toExclusive <= SIZE)
        val range = (toExclusive - from)
        val mask =
                when (range) {
                    0 -> return Zero
                    SIZE -> return this
                    else -> ((1 shl (toExclusive - from)) - 1) shl from
                }
        return (this and mask.asBits()) shr from
    }
    inline fun slice(range: IntRange): IntBits = slice(range.start, range.endInclusive + 1)

    @Suppress(Warnings.OVERRIDE_BY_INLINE)
    override inline fun toString(): String = data.toLong().toString(2)
}

inline fun Bits(data: Int) = data.asBits()
