package ru.spbstu.wheels

import ru.spbstu.wheels.collections.IAbstractMutableSet
import kotlin.math.max
import kotlin.math.min

/*
* This implementation is a direct port of BitSet implementation from java EWAH: https://github.com/lemire/javaewah
* ported to Kotlin MPP and to 32-bit arrays
*
* Comments below are from original implementation
* */

/**
 *
 * This is an optimized version of Java's BitSet. In many cases, it can be used
 * as a drop-in replacement.
 *
 *
 * It differs from the basic Java BitSet class in the following ways:
 *
 *  * You can iterate over set bits using a simpler syntax `for(int bs: myBitset)`.
 *  * You can compute the cardinality of an intersection and union without writing it out
 * or modifying your BitSets (see methods such as andcardinality).
 *  * You can recover wasted memory with trim().
 *  * It does not implicitly expand: you have to explicitly call resize. This helps to keep memory usage in check.
 *  * It supports memory-file mapping (see the ImmutableBitSet class).
 *  * It supports faster and more efficient serialization functions (serialize and deserialize).
 *
 *
 * @author Daniel Lemire
 * @since 0.8.0
 */
class BitSet : Iterable<Int> {
    /**
     * Construct a bitset with the specified number of bits (initially all
     * false). The number of bits is rounded up to the nearest multiple of
     * 64.
     *
     * @param sizeInBits the size in bits
     */
    constructor(sizeInBits: Int) {
        data = IntArray((sizeInBits + WORD_SIZE_MINUS_ONE) / WORD_SIZE_BITS)
    }

    constructor() {
        data = IntArray(0)
    }

    private constructor(unsafeData: IntArray) {
        data = unsafeData
    }

    /**
     * Compute bitwise AND.
     *
     * @param bs other bitset
     */
    fun and(bs: BitSet) {
        for (k in 0 until min(getNumberOfWords(), bs.getNumberOfWords())) {
            data[k] = data[k] and bs.getWord(k)
        }
    }

    /**
     * Compute cardinality of bitwise AND.
     *
     * The current bitmap is modified. Consider calling trim()
     * to recover wasted memory afterward.
     *
     * @param bs other bitset
     * @return cardinality
     */
    fun andcardinality(bs: BitSet): Int {
        var sum = 0
        for (k in 0 until min(getNumberOfWords(), bs.getNumberOfWords())) {
            sum += (getWord(k) and bs.getWord(k)).countOneBits()
        }
        return sum
    }

    /**
     * Compute bitwise AND NOT.
     *
     * The current bitmap is modified. Consider calling trim()
     * to recover wasted memory afterward.
     *
     * @param bs other bitset
     */
    fun andNot(bs: BitSet) {
        for (k in 0 until min(getNumberOfWords(), bs.getNumberOfWords())) {
            data[k] = data[k] and bs.getWord(k).inv()
        }
    }

    /**
     * Compute cardinality of bitwise AND NOT.
     *
     * @param bs other bitset
     * @return cardinality
     */
    fun andNotcardinality(bs: BitSet): Int {
        var sum = 0
        for (k in 0 until min(getNumberOfWords(), bs.getNumberOfWords())) {
            sum += (getWord(k) and bs.getWord(k).inv()).countOneBits()
        }
        return sum
    }

    /**
     * Compute the number of bits set to 1
     *
     * @return the number of bits
     */
    fun cardinality(): Int {
        var sum = 0
        for (l in data) sum += (l).countOneBits()
        return sum
    }

    /**
     * Reset all bits to false. This might be wasteful: a better
     * approach is to create a new empty bitmap.
     */
    fun clear() {
        data.fill(0)
    }

    /**
     * Set the bit to false.
     * See [.unset]
     *
     * @param index location of the bit
     */
    fun clear(index: Int) {
        unset(index)
    }

    /**
     * Set the bits in the range of indexes to false.
     * This might throw an exception if size() is insufficient, consider calling resize().
     *
     * @param start location of the first bit to set to zero
     * @param end location of the last bit to set to zero (not included)
     */
    fun clear(start: Int, end: Int) {
        if (start == end) return
        val firstword = start / WORD_SIZE_BITS
        val endword = (end - 1) / WORD_SIZE_BITS
        if (firstword == endword) {
            data[firstword] = data[firstword] and (ONES shl start and (ONES ushr -end)).inv()
            return
        }
        data[firstword] = data[firstword] and (ONES shl start).inv()
        for (i in firstword + 1 until endword) data[i] = 0
        data[endword] = data[endword] and (ONES ushr -end).inv()
    }

    override fun equals(o: Any?): Boolean {
        if (o is BitSet) {
            val bs: BitSet = o
            for (k in 0 until min(
                getNumberOfWords(),
                bs.getNumberOfWords()
            )) {
                if (getWord(k) != bs.getWord(k)) return false
            }
            val longer: BitSet = if (bs.getNumberOfWords() < getNumberOfWords()) this else bs
            for (k in min(
                getNumberOfWords(),
                bs.getNumberOfWords()
            ) until max(
                getNumberOfWords(),
                bs.getNumberOfWords()
            )) {
                if (longer.getWord(k) != ZERO) {
                    return false
                }
            }
            return true
        }
        return false
    }

    /**
     * Check whether a bitset contains a set bit.
     *
     * @return true if no set bit is found
     */
    fun empty(): Boolean {
        for (l in data) if (l != ZERO) return false
        return true
    }

    /**
     * Flip the bit. This might throw an exception if size() is insufficient, consider calling resize().
     *
     * @param i index of the bit
     */
    fun flip(i: Int) {
        data[i / WORD_SIZE_BITS] = data[i / WORD_SIZE_BITS] xor (ONE shl (i % WORD_SIZE_BITS))
    }

    /**
     * Flip the bits in the range of indexes.
     * This might throw an exception if size() is insufficient, consider calling resize().
     *
     * @param start location of the first bit
     * @param end location of the last bit (not included)
     */
    fun flip(start: Int, end: Int) {
        if (start == end) return
        val firstword = start / WORD_SIZE_BITS
        val endword = (end - 1) / WORD_SIZE_BITS
        data[firstword] = data[firstword] xor (ONES shl start).inv()
        for (i in firstword until endword) data[i] = data[i].inv()
        data[endword] = data[endword] xor (ONES ushr -end)
    }

    /**
     * Get the value of the bit.  This might throw an exception if size() is insufficient, consider calling resize().
     * @param i index
     * @return value of the bit
     */
    operator fun get(i: Int): Boolean {
        return (data[i / WORD_SIZE_BITS] and (ONE shl (i % WORD_SIZE_BITS))) != ZERO
    }

    override fun hashCode(): Int {
        val b = 31
        var hash: Long = 0
        for (k in data.indices) {
            val aData = getWord(k)
            hash = hash * b + aData
        }
        return hash.toInt()
    }

    /**
     * Iterate over the set bits
     *
     * @return an iterator
     */

    fun intIterator(): IntIterator {
        return object : IntIterator() {
            override fun hasNext(): Boolean {
                return i >= 0
            }

            override fun nextInt(): Int {
                j = i
                i = nextSetBit(i + 1)
                return j
            }

            private var i = nextSetBit(0)
            private var j = 0
        }
    }


    override fun iterator(): MutableIterator<Int> {
        return object : MutableIterator<Int> {
            override fun hasNext(): Boolean {
                return i >= 0
            }


            override fun next(): Int {
                j = i
                i = nextSetBit(i + 1)
                return j
            }

            override fun remove() {
                unset(j)
            }

            private var i = nextSetBit(0)
            private var j = 0
        }
    }

    /**
     * Checks whether two bitsets intersect.
     *
     * @param bs other bitset
     * @return true if they have a non-empty intersection (result of AND)
     */
    fun intersects(bs: BitSet): Boolean {
        for (k in 0 until min(getNumberOfWords(), bs.getNumberOfWords())) {
            if (getWord(k) and bs.getWord(k) != ZERO) return true
        }
        return false
    }

    /**
     * Usage: for(int i=bs.nextSetBit(0); i&gt;=0; i=bs.nextSetBit(i+1)) {
     * operate on index i here }
     *
     * @param i current set bit
     * @return next set bit or -1
     */
    fun nextSetBit(i: Int): Int {
        var x = i / WORD_SIZE_BITS
        if (x >= getNumberOfWords()) return -1
        var w = data[x]
        w = w ushr i
        if (w != ZERO) {
            return i + w.countTrailingZeroBits()
        }
        ++x
        while (x < getNumberOfWords()) {
            if (data[x] != ZERO) {
                return (x
                        * WORD_SIZE_BITS
                        + data[x].countTrailingZeroBits())
            }
            ++x
        }
        return -1
    }

    /**
     * Usage: for(int i=bs.nextUnsetBit(0); i&gt;=0; i=bs.nextUnsetBit(i+1))
     * { operate on index i here }
     *
     * @param i current unset bit
     * @return next unset bit or -1
     */
    fun nextUnsetBit(i: Int): Int {
        var x = i / WORD_SIZE_BITS
        if (x >= getNumberOfWords()) return -1
        var w = data[x].inv()
        w = w ushr i
        if (w != ZERO) {
            return i + w.countTrailingZeroBits()
        }
        ++x
        while (x < getNumberOfWords()) {
            if (data[x] != ONES) {
                return (x
                        * WORD_SIZE_BITS
                        + data[x].inv().countTrailingZeroBits())
            }
            ++x
        }
        return -1
    }

    /**
     * Compute bitwise OR.
     *
     * The current bitmap is modified. Consider calling trim()
     * to recover wasted memory afterward.
     *
     * @param bs other bitset
     */
    fun or(bs: BitSet) {
        if (getNumberOfWords() < bs.getNumberOfWords()) resize(bs.getNumberOfWords() * WORD_SIZE_BITS)
        for (k in 0 until getNumberOfWords()) {
            data[k] = data[k] or bs.getWord(k)
        }
    }

    /**
     * Compute cardinality of bitwise OR.
     *
     * BitSets are not modified.
     *
     * @param bs other bitset
     * @return cardinality
     */
    fun orcardinality(bs: BitSet): Int {
        var sum = 0
        for (k in 0 until min(getNumberOfWords(), bs.getNumberOfWords())) {
            sum += (getWord(k) or bs.getWord(k)).countOneBits()
        }
        val longer: BitSet = if (bs.getNumberOfWords() < getNumberOfWords()) this else bs
        for (k in min(getNumberOfWords(), bs.getNumberOfWords()) until max(
            getNumberOfWords(),
            bs.getNumberOfWords()
        )) {
            sum += (longer.getWord(k)).countOneBits()
        }
        return sum
    }

    /**
     * Remove a word.
     *
     *
     * @param i index of the word to be removed.
     */
    fun removeWord(i: Int) {
        val newdata = IntArray(data.size - 1)
        if (i == 0) {
            data.copyInto(
                newdata,
                destinationOffset = 0,
                startIndex = 1
            )
        }
        data.copyInto(
            newdata,
            destinationOffset = 0,
            startIndex = 0,
            endIndex = i
        )
        data.copyInto(
            newdata,
            destinationOffset = i - 1,
            startIndex = i
        )
        data = newdata
    }

    /**
     * Resize the bitset
     *
     * @param sizeInBits new number of bits
     */
    fun resize(sizeInBits: Int) {
        data = data.copyOf((sizeInBits + WORD_SIZE_MINUS_ONE) / WORD_SIZE_BITS)
    }

    /**
     * Set to true. This might throw an exception if size() is insufficient, consider calling resize().
     *
     * @param i index of the bit
     */
    fun set(i: Int) {
        data[i / WORD_SIZE_BITS] = data[i / WORD_SIZE_BITS] or (ONE shl (i % WORD_SIZE_BITS))
    }

    /**
     * Set to some value. This might throw an exception if size() is insufficient, consider calling resize().
     *
     * @param i index
     * @param b value of the bit
     */
    operator fun set(i: Int, b: Boolean) {
        if (b) set(i) else unset(i)
    }

    /**
     * Set the bits in the range of indexes true.
     * This might throw an exception if size() is insufficient, consider calling resize().
     *
     * @param start location of the first bit
     * @param end location of the last bit (not included)
     */
    fun set(start: Int, end: Int) {
        if (start == end) return
        val firstword = start / WORD_SIZE_BITS
        val endword = (end - 1) / WORD_SIZE_BITS
        if (firstword == endword) {
            data[firstword] = data[firstword] or (ONES shl start and (ONES ushr -end))
            return
        }
        data[firstword] = data[firstword] or (ONES shl start)
        for (i in firstword + 1 until endword) data[i] = 0.inv()
        data[endword] = data[endword] or (ONES ushr -end)
    }

    /**
     * Set the bits in the range of indexes to the specified Boolean value.
     * This might throw an exception if size() is insufficient, consider calling resize().
     *
     * @param start location of the first bit
     * @param end location of the last bit (not included)
     * @param v Boolean value
     */
    operator fun set(start: Int, end: Int, v: Boolean) {
        if (v) set(start, end) else clear(start, end)
    }

    /**
     * Query the size
     *
     * @return the size in bits.
     */
    fun size(): Int {
        return getNumberOfWords() * WORD_SIZE_BITS
    }

    /**
     * Recovers wasted memory
     */
    fun trim() {
        for (k in getNumberOfWords() - 1 downTo 0) if (getWord(k) != ZERO) {
            if (k + 1 < getNumberOfWords()) data = data.copyOf(k + 1)
            return
        }
        data = IntArray(0)
    }

    /**
     * Set to false
     *
     * @param i index of the bit
     */
    fun unset(i: Int) {
        data[i / WORD_SIZE_BITS] = data[i / WORD_SIZE_BITS] and (ONE shl i % WORD_SIZE_BITS).inv()
    }

    /**
     * Iterate over the unset bits
     *
     * @return an iterator
     */

    fun unsetIntIterator(): IntIterator {
        return object : IntIterator() {
            override fun hasNext(): Boolean {
                return i >= 0
            }

            override fun nextInt(): Int {
                j = i
                i = nextUnsetBit(i + 1)
                return j
            }

            private var i = nextUnsetBit(0)
            private var j = 0
        }
    }

    /**
     * Compute bitwise XOR.
     *
     * The current bitmap is modified. Consider calling trim()
     * to recover wasted memory afterward.
     *
     * @param bs other bitset
     */
    fun xor(bs: BitSet) {
        if (getNumberOfWords() < bs.getNumberOfWords()) resize(bs.getNumberOfWords() * WORD_SIZE_BITS)
        for (k in 0 until getNumberOfWords()) {
            data[k] = data[k] xor bs.getWord(k)
        }
    }

    /**
     * Compute cardinality of bitwise XOR.
     *
     * BitSets are not modified.
     *
     * @param bs other bitset
     * @return cardinality
     */
    fun xorcardinality(bs: BitSet): Int {
        var sum = 0
        for (k in 0 until min(getNumberOfWords(), bs.getNumberOfWords())) {
            sum += (getWord(k) xor bs.getWord(k)).countOneBits()
        }
        val longer: BitSet = if (bs.getNumberOfWords() < getNumberOfWords()) this else bs
        val start: Int = min(getNumberOfWords(), bs.getNumberOfWords())
        val end: Int = max(getNumberOfWords(), bs.getNumberOfWords())
        for (k in start until end) {
            sum += (longer.getWord(k)).countOneBits()
        }
        return sum
    }

    fun getNumberOfWords(): Int {
        return data.size
    }

    fun getWord(index: Int): Int {
        return data[index]
    }


    override fun toString(): String {
        val answer = StringBuilder()
        val i = intIterator()
        answer.append("{")
        if (i.hasNext()) answer.append(i.nextInt())
        while (i.hasNext()) {
            answer.append(",")
            answer.append(i.nextInt())
        }
        answer.append("}")
        return answer.toString()
    }

    fun toBitSet(): BitSet = BitSet(data.copyOf())

    private var data: IntArray

    companion object {
        private const val WORD_SIZE_BITS = Int.SIZE_BITS
        private const val WORD_SIZE_MINUS_ONE = WORD_SIZE_BITS - 1
        private const val ZERO = 0
        private const val ONE = ZERO + 1
        private const val ONES = ZERO.inv()

        /**
         * Return a bitmap with the bit set to true at the given positions.
         *
         * (This is a convenience method.)
         *
         * @param setBits list of set bit positions
         * @return the bitmap
         */

        fun bitmapOf(vararg setBits: Int): BitSet {
            var maxv = 0
            for (k in setBits) if (maxv < k) maxv = k
            val a = BitSet(maxv + 1)
            for (k in setBits) a.set(k)
            return a
        }
    }
}

/**
 * Adhere to kotlin `in` operator convention
 *
 * @param element index to check
 * @see BitSet.get
 **/
operator fun BitSet.contains(element: Int): Boolean = element < size() && get(element)

/**
 * Check whether `this` bitset has all `other` bitset bits also set
 **/
fun BitSet.containsAll(other: BitSet) = other.andNotcardinality(this) == 0

/**
 * MutableSet wrapper for BitSet
 *
 * @see MutableSet
 */
private class BitSetAsSet(val inner: BitSet): IAbstractMutableSet.Impl<Int>() {
    /**
     * size is handled and stored separately from the bitset because calculating it is not free
     */
    override var size: Int = inner.cardinality()
        private set

    /**
     * recalculate size based on `inner` contents and assign it
     */
    private fun recalculateSize() {
        size = inner.cardinality()
    }

    override fun iterator(): MutableIterator<Int> {
        val innerIterator = inner.iterator()
        return object : MutableIterator<Int> {
            override fun hasNext(): Boolean = innerIterator.hasNext()
            override fun next(): Int = innerIterator.next()
            override fun remove() {
                /* we need to provide our own remove() because of size */
                innerIterator.remove()
                recalculateSize()
            }
        }
    }

    override fun add(element: Int): Boolean {
        if (element > inner.size()) {
            val newSize = minOf((inner.size() * growFactor).toInt(), element + 1)
            inner.resize(newSize)
        } else {
            if (inner[element]) return false
        }
        inner.set(element)
        size++
        return true
    }

    override fun remove(element: Int): Boolean {
        if (!inner.contains(element)) return false
        inner.unset(element)
        size--
        return true
    }

    /*
    * Optimized set union: if `elements` is also a bitset, use bitset or operation
    * */
    override fun addAll(elements: Collection<Int>): Boolean {
        when (elements) {
            is BitSetAsSet -> inner.or(elements.inner)
            else -> super.addAll(elements)
        }
        val oldSize = size
        recalculateSize()
        return oldSize != size
    }

    /*
    * Optimized set inclusion: if `elements` is also a bitset, use bitset containsAll operation
    * */
    override fun containsAll(elements: Collection<Int>): Boolean {
        return when (elements) {
            is BitSetAsSet -> inner.containsAll(elements.inner)
            else -> super.containsAll(elements)
        }
    }

    /*
    * Optimized set difference: if `elements` is also a bitset, use bitset andNot operation
    * */
    override fun removeAll(elements: Collection<Int>): Boolean {
        when (elements) {
            is BitSetAsSet -> inner.andNot(elements.inner)
            else -> super.removeAll(elements)
        }
        val oldSize = size
        recalculateSize()
        return oldSize != size
    }

    /*
    * Optimized set intersection: if `elements` is also a bitset, use bitset and operation
    * */
    override fun retainAll(elements: Collection<Int>): Boolean {
        when (elements) {
            is BitSetAsSet -> inner.and(elements.inner)
            else -> super.retainAll(elements)
        }
        val oldSize = size
        recalculateSize()
        return oldSize != size
    }

    override fun contains(element: Int): Boolean = element in inner

    override fun clear() {
        inner.clear()
    }

    companion object {
        const val growFactor = 1.5
    }
}

/**
 * Wrap this BitSet as a MutableSet
 *
 * Any changes to the resulting set will change the contents of BitSet
 *
 * It is unsafe to change the contents of this BitSet after it has been wrapped
 */
fun BitSet.asMutableSet(): MutableSet<Int> = BitSetAsSet(this)

/**
 * Wrap this BitSet as a Set
 *
 * It is unsafe to change the contents of this BitSet after it has been wrapped
 */
fun BitSet.asSet(): Set<Int> = asMutableSet()

