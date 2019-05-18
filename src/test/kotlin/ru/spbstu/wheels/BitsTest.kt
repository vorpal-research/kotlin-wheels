package ru.spbstu.wheels

import org.junit.Test
import java.lang.IllegalArgumentException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BitsTest {
    @Test
    fun boringOps() {
        assertEquals(
                IntBits.fromString("10010100110000000000000000000000"),
                Bits(0b1100101001).reverse()
        )

        assertEquals(
                Bits(0b11001100),
                Bits(0b10000100) or Bits(0b01001100)
        )

        assertEquals(
                Bits(0b01001100),
                Bits(0b11011100) and Bits(0b1001100)
        )


        assertEquals(
                IntBits.fromString("0000000111000000000000011001100010"),
                IntBits.fromString("1111111000111111111111100110011101").inv()
        )
    }

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

    @Test
    fun words() {
        val bits = 0x0AABBADD.asBits()

        assertEquals(0xDD.asBits(), bits.wordAt(0))
        assertEquals(0xBA.asBits(), bits.wordAt(1))
        assertEquals(0xAB.asBits(), bits.wordAt(2))
        assertEquals(0x0A.asBits(), bits.wordAt(3))

        assertFailsWith<IllegalArgumentException> { bits.wordAt(4) }

        assertEquals(0xD.asBits(), bits.wordAt(0, 4))
        assertEquals(0xA.asBits(), bits.wordAt(2, 4))
        assertEquals(0xB.asBits(), bits.wordAt(4, 4))
        assertEquals(0xA.asBits(), bits.wordAt(6, 4))

        assertFailsWith<IllegalArgumentException> { bits.wordAt(8, 4) }

        assertEquals(0xADD.asBits(), bits.wordAt(0, 12))
        assertEquals(0xABB.asBits(), bits.wordAt(1, 12))
        assertEquals(0x00A.asBits(), bits.wordAt(2, 12))

        assertFailsWith<IllegalArgumentException> { bits.wordAt(3, 12) }

        assertEquals(
                0x0AFFBADD.asBits(),
                bits.setWordAt(2, 0xFF.asBits())
        )

        assertEquals(
                0x0A000ADD.asBits(),
                bits.setWordAt(1, 0x0.asBits(), 12)
        )

        assertEquals(
                0xFFABBADDL.toInt().asBits(),
                bits.setWordAt(2, 0xFFFF.asBits(), 12)
        )

    }

}