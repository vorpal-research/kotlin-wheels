@file: Suppress(Warnings.NOTHING_TO_INLINE)
@file: OptIn(ExperimentalStdlibApi::class, ExperimentalUnsignedTypes::class)

package ru.spbstu.wheels

import kotlinx.warnings.Warnings
import kotlin.math.ceil

typealias JLong = Unit
typealias Integer = Unit

inline fun Int.asBits() = IntBits(this)

//putting these guys as members triggers
//https://youtrack.jetbrains.com/issue/KT-31431
val IntBits.Companion.Zero get() = 0.asBits()
val IntBits.Companion.One get() = 1.asBits()
val IntBits.Companion.AllOnes get() = (-1).asBits()
val IntBits.Companion.SIZE get() = Int.SIZE_BITS

fun IntBits.Companion.fromString(s: String): IntBits = IntBits(s.toUInt(2).toInt())

@OptIn(ExperimentalStdlibApi::class)
inline class IntBits
constructor(val data: Int) {
    companion object {}

    inline fun asInt() = data

    inline infix fun shl(bitCount: Int): IntBits = IntBits(data shl bitCount)
    inline infix fun shr(bitCount: Int): IntBits = IntBits(data ushr bitCount)
    inline infix fun and(that: IntBits): IntBits = IntBits(data and that.data)
    inline infix fun andNot(that: IntBits): IntBits = IntBits(data and that.data.inv())
    inline infix fun or(that: IntBits): IntBits = IntBits(data or that.data)
    inline infix fun xor(that: IntBits): IntBits = IntBits(data xor that.data)
    inline fun inv(): IntBits = data.inv().asBits()
    //inline fun reverse(): IntBits = Integer.reverse(data).asBits()

    inline val popCount: Int get() = data.countOneBits()
    inline val lowestBitSet get() = IntBits(data.takeLowestOneBit())
    inline val highestBitSet get() = IntBits(data.takeHighestOneBit())
    inline val numberOfLeadingZeros: Int get() = data.countLeadingZeroBits()
    inline val numberOfTrailingZeros: Int get() = data.countTrailingZeroBits()

    inline fun forEachOneBit(body: (IntBits) -> Unit) {
        var mask = this
        while (mask != Zero) {
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
        val mask =
                when (val size = toExclusive - from) {
                    0 -> return Zero
                    SIZE -> return this
                    else -> ((1 shl size) - 1) shl from
                }
        return (this and mask.asBits()) shr from
    }

    inline fun slice(range: IntRange): IntBits = slice(range.start, range.endInclusive + 1)
    inline fun setSlice(from: Int = 0, toExclusive: Int = SIZE, value: IntBits): IntBits {
        require(from >= 0)
        require(toExclusive >= from)
        require(toExclusive <= SIZE)
        val ones =
                when (val size = toExclusive - from) {
                    0 -> return this
                    SIZE -> return value
                    else -> ((1 shl size) - 1).asBits()
                }
        val adjustedValue = value and ones

        return this andNot (ones shl from) or (adjustedValue shl from)
    }

    inline fun wordAt(index: Int, byteSize: Int = Byte.SIZE_BITS) = run {
        require(index >= 0)
        require(index < ceil(SIZE.toDouble() / byteSize))
        slice(byteSize * index, minOf(byteSize * index + byteSize, SIZE))
    }

    inline fun setWordAt(index: Int, value: IntBits, byteSize: Int = Byte.SIZE_BITS) =
            setSlice(byteSize * index, minOf(byteSize * index + byteSize, SIZE), value)


    @Suppress(Warnings.OVERRIDE_BY_INLINE)
    override inline fun toString(): String = data.toUInt().toString(2)
}

inline fun bits(data: Int) = data.asBits()

inline fun Long.asBits() = LongBits(this)

//putting these guys as members triggers
//https://youtrack.jetbrains.com/issue/KT-31431
val LongBits.Companion.Zero get() = 0L.asBits()
val LongBits.Companion.One get() = 1L.asBits()
val LongBits.Companion.AllOnes get() = (-1L).asBits()
val LongBits.Companion.SIZE get() = Long.SIZE_BITS

fun LongBits.Companion.fromString(s: String): LongBits = bits(s.toULong(2).toLong())

@OptIn(ExperimentalStdlibApi::class)
inline class LongBits
constructor(val data: Long) {
    companion object {}

    inline fun asLong() = data

    inline infix fun shl(bitCount: Int): LongBits = LongBits(data shl bitCount)
    inline infix fun shr(bitCount: Int): LongBits = LongBits(data ushr bitCount)
    inline infix fun and(that: LongBits): LongBits = LongBits(data and that.data)
    inline infix fun andNot(that: LongBits): LongBits = LongBits(data and that.data.inv())
    inline infix fun or(that: LongBits): LongBits = LongBits(data or that.data)
    inline infix fun xor(that: LongBits): LongBits = LongBits(data xor that.data)
    inline fun inv(): LongBits = data.inv().asBits()
    //inline fun reverse(): LongBits = JLong.reverse(data).asBits()

    inline val popCount: Int get() = data.countOneBits()
    inline val lowestBitSet get() = LongBits(data.takeLowestOneBit())
    inline val highestBitSet get() = LongBits(data.takeHighestOneBit())
    inline val numberOfLeadingZeros: Int get() = data.countLeadingZeroBits()
    inline val numberOfTrailingZeros: Int get() = data.countTrailingZeroBits()

    inline fun forEachOneBit(body: (LongBits) -> Unit) {
        var mask = this
        while (mask != Zero) {
            val bit = mask.lowestBitSet
            body(bit)
            mask = mask andNot bit
        }
    }

    // DANGER: boxing!
    fun oneBitSequence() = sequence { forEachOneBit { yield(it) } }

    inline operator fun get(index: Int) = data and (1L shl index) != 0L
    inline fun set(index: Int) = this or (One shl index)
    inline fun clear(index: Int) = this andNot (One shl index)
    inline fun slice(from: Int = 0, toExclusive: Int = SIZE): LongBits {
        require(from >= 0)
        require(toExclusive >= from)
        require(toExclusive <= SIZE)
        val mask =
                when (val size = toExclusive - from) {
                    0 -> return Zero
                    SIZE -> return this
                    else -> ((1L shl size) - 1L) shl from
                }
        return (this and mask.asBits()) shr from
    }

    inline fun slice(range: IntRange): LongBits = slice(range.start, range.endInclusive + 1)
    inline fun setSlice(from: Int = 0, toExclusive: Int = SIZE, value: LongBits): LongBits {
        require(from >= 0)
        require(toExclusive >= from)
        require(toExclusive <= SIZE)
        val ones =
                when (val size = toExclusive - from) {
                    0 -> return this
                    SIZE -> return value
                    else -> ((1L shl size) - 1).asBits()
                }
        val adjustedValue = value and ones

        return this andNot (ones shl from) or (adjustedValue shl from)
    }

    inline fun wordAt(index: Int, byteSize: Int = Byte.SIZE_BITS) = run {
        require(index >= 0)
        require(index < ceil(SIZE.toDouble() / byteSize))
        slice(byteSize * index, minOf(byteSize * index + byteSize, SIZE))
    }

    inline fun setWordAt(index: Int, value: LongBits, byteSize: Int = Byte.SIZE_BITS) =
            setSlice(byteSize * index, minOf(byteSize * index + byteSize, SIZE), value)


    @Suppress(Warnings.OVERRIDE_BY_INLINE)
    override inline fun toString(): String = data.toULong().toString(2)
}

inline fun bits(data: Long) = data.asBits()
