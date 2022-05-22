package ru.spbstu.wheels.collections

class SimpleCharSubSequence(
    val base: CharSequence,
    val fromIx: Int = 0,
    val toIx: Int = base.length) : ICharSequence.Impl() {
    init {
        checkBounds(fromIx in base.indices)
        checkBounds(toIx in 0 .. base.length)
        checkBounds(toIx >= fromIx)
    }

    override val length: Int
        get() = toIx - fromIx

    private fun adjustIndex(index: Int): Int {
        val adjusted = index + fromIx
        checkBounds(adjusted in 0 .. base.length)
        return adjusted
    }

    override fun get(index: Int): Char = base.get(adjustIndex(index))

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        checkBounds(startIndex >= 0)
        checkBounds(endIndex >= startIndex)
        val adjustedStart = adjustIndex(startIndex)
        val adjustedEnd = adjustIndex(endIndex)

        return SimpleCharSubSequence(base, adjustedStart, adjustedEnd)
    }
}

interface ICharSequence : CharSequence {
    private inline fun forEachIndexed(body: (Int, Char) -> Unit) {
        for (i in indices) {
            body(i, get(i))
        }
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence =
        SimpleCharSubSequence(this, startIndex, endIndex)

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
    override fun toString(): String

    fun ICharSequence.charSequenceEquals(other: Any?): Boolean {
        if (other !is CharSequence) return false
        if (length != other.length) return false
        forEachIndexed { i, c ->
            if (other[i] != c) return false
        }
        return true
    }

    fun ICharSequence.charSequenceHashCode(): Int {
        var hash = 0
        forEachIndexed { _, c ->
            hash = 31 * hash + c.hashCode()
        }
        return hash
    }

    fun ICharSequence.charSequenceToString(): String {
        val sb = StringBuilder()
        forEachIndexed { _, c -> sb.append(c) }
        return sb.toString()
    }

    abstract class Impl : ICharSequence {
        override fun equals(other: Any?): Boolean = charSequenceEquals(other)
        override fun hashCode(): Int = charSequenceHashCode()
        override fun toString(): String = charSequenceToString()
    }
}
