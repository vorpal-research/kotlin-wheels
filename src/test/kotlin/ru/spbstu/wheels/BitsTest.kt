package ru.spbstu.wheels

import org.junit.Test
import kotlin.test.assertEquals

class BitsTest {
    @Test
    fun singleBitOps() {
        assertEquals(
                0b10000000000.asBits(),
                0b11001100000.asBits().highestBitSet
        )

        assertEquals(IntBits.Zero, IntBits.Zero.highestBitSet)
        assertEquals(IntBits.Zero, IntBits.Zero.lowestBitSet)

        assertEquals(
                0b00000100000.asBits(),
                0b11001100000.asBits().lowestBitSet
        )

        Bits(0b11001100000).forEachOneBit { bit ->
            assertEquals(1, bit.popCount)
        }

        Bits(0).forEachOneBit { throw Exception() }

        assertEquals(
                listOf(
                        0b00000000001,
                        0b00000100000,
                        0b00001000000,
                        0b01000000000,
                        0b10000000000
                ).map(::Bits),
                Bits(0b11001100001).oneBitSequence().toList()
        )

    }

    @Test
    fun getsAndSets() {
        val bits = 0b11001100000.asBits()
        assertEquals(true, bits[5])
        assertEquals(true, bits[6])
        assertEquals(false, bits[0])
        assertEquals(false, bits[22])

        assertEquals(
                0b11001100010.asBits(),
                bits.set(1)
        )

        assertEquals(
                0b11001000000.asBits(),
                bits.clear(5)
        )
    }

    @Test
    fun slices() {
        val bits = 0b11001100000.asBits()
        assertEquals(
                bits,
                bits.slice(0, 32)
        )

        assertEquals(
                bits,
                bits.slice(0, 17)
        )

        assertEquals(
                0b110011.asBits(),
                bits.slice(from = 5)
        )

        assertEquals(
                0b11.asBits(),
                bits.slice(5, 8)
        )

        assertEquals(
                0b11.asBits(),
                bits.slice(5..7)
        )
    }

}