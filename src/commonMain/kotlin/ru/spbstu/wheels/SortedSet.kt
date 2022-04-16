package ru.spbstu.wheels

import kotlin.random.Random

interface SortedSet<E> : Set<E> {
    /**
     * Returns the comparator used to order the elements in this set,
     * or `null` if this set uses the [ natural ordering][Comparable] of its elements.
     *
     * @return the comparator used to order the elements in this set,
     * or `null` if this set uses the natural ordering
     * of its elements
     */
    fun comparator(): Comparator<in E>?

    /**
     * Returns the first (lowest) element currently in this set.
     *
     * @return the first (lowest) element currently in this set
     * @throws NoSuchElementException if this set is empty
     */
    fun first(): E

    /**
     * Returns the last (highest) element currently in this set.
     *
     * @return the last (highest) element currently in this set
     * @throws NoSuchElementException if this set is empty
     */
    fun last(): E

    /**
     * Returns the greatest element in this set strictly less than the
     * given element, or `null` if there is no such element.
     *
     * @param e the value to match
     * @return the greatest element less than `e`,
     * or `null` if there is no such element
     * @throws ClassCastException if the specified element cannot be
     * compared with the elements currently in the set
     * @throws NullPointerException if the specified element is null
     * and this set does not permit null elements
     */
    fun lower(e: E): E?

    /**
     * Returns the greatest element in this set less than or equal to
     * the given element, or `null` if there is no such element.
     *
     * @param e the value to match
     * @return the greatest element less than or equal to `e`,
     * or `null` if there is no such element
     * @throws ClassCastException if the specified element cannot be
     * compared with the elements currently in the set
     * @throws NullPointerException if the specified element is null
     * and this set does not permit null elements
     */
    fun floor(e: E): E?

    /**
     * Returns the least element in this set greater than or equal to
     * the given element, or `null` if there is no such element.
     *
     * @param e the value to match
     * @return the least element greater than or equal to `e`,
     * or `null` if there is no such element
     * @throws ClassCastException if the specified element cannot be
     * compared with the elements currently in the set
     * @throws NullPointerException if the specified element is null
     * and this set does not permit null elements
     */
    fun ceiling(e: E): E?

    /**
     * Returns the least element in this set strictly greater than the
     * given element, or `null` if there is no such element.
     *
     * @param e the value to match
     * @return the least element greater than `e`,
     * or `null` if there is no such element
     * @throws ClassCastException if the specified element cannot be
     * compared with the elements currently in the set
     * @throws NullPointerException if the specified element is null
     * and this set does not permit null elements
     */
    fun higher(e: E): E?
}

interface MutableSortedSet<T>: SortedSet<T>, MutableSet<T>

private data class TreapNode<T>(
    val key: T,
    val rank: Int = Random.nextInt(),
    val left: TreapNode<T>? = null,
    val right: TreapNode<T>? = null
) {
    constructor(key: T, generator: Random, left: TreapNode<T>? = null, right: TreapNode<T>? = null):
            this(key, generator.nextInt(), left, right)
}

private data class SplitResult<T>(var left: TreapNode<T>?, var key: T?, var right: TreapNode<T>?)
private data class DeltaCounter(var delta: Int = 0) {
    operator fun plusAssign(value: Int) { delta += value }
}

class TreapSet<T>(val comparator_: Comparator<T>?,
                  val generator: Random): MutableSortedSet<T>, AbstractMutableSet<T>() {

    private var root: TreapNode<T>? = null
    override var size: Int = 0
        private set

    private fun TreapNode(key: T, left: TreapNode<T>? = null, right: TreapNode<T>? = null) =
        TreapNode(key, generator, left, right)

    private fun TreapNode<T>?.detach() = when (this) {
        null -> null
        else -> copy(left = null, right = null)
    }

    private fun merge(left: TreapNode<T>?, right: TreapNode<T>?): TreapNode<T>? {
        left ?: return right
        right ?: return left
        return when {
            left.rank < right.rank -> right.copy(left = merge(left, right.left))
            else -> left.copy(right = merge(left.right, right))
        }
    }

    private fun merge(first: TreapNode<T>?, vararg rest: TreapNode<T>?): TreapNode<T>? =
        rest.fold(first, ::merge)

    private operator fun T.compareTo(that: T): Int = when(comparator_) {
        null -> uncheckedCast<Comparable<Any?>>(this).compareTo(that)
        else -> comparator_.compare(this, that)
    }

    private infix fun T.cmpEquals(that: T): Boolean = compareTo(that) == 0

    private fun TreapNode<T>?.split(on: T): SplitResult<T> {
        this ?: return SplitResult(null, null, null)
        return when {
            on cmpEquals key -> SplitResult(left, key, right)
            on < key -> {
                left ?: return SplitResult(null, null, this)
                val rec = left.split(on)
                rec.right = merge(rec.right, detach(), right)
                rec
            }
            else -> {
                right ?: return SplitResult(this, null, null)
                val rec = right.split(on)
                rec.left = merge(left, detach(), rec.left)
                rec
            }
        }
    }

    private fun TreapNode<T>?.contains(element: T): Boolean {
        this ?: return false

        return when {
            element cmpEquals key -> true
            element < key -> left.contains(element)
            else -> right.contains(element)
        }
    }

    override fun contains(element: T): Boolean = root.contains(element)

    private fun TreapNode<T>?.remove(element: T): TreapNode<T>? {
        this ?: return null

        val (l, k, r) = split(element)
        return when (k) {
            null -> null
            else -> merge(l, r)
        }
    }

    override fun remove(element: T): Boolean {
        root = root.remove(element) ?: return false
        size--
        return true
    }

    private fun TreapNode<T>?.add(element: T): TreapNode<T> {
        this ?: return TreapNode(element)

        val (l, k, r) = split(element)
        return when {
            k != null -> this
            else -> merge(l, TreapNode(element), r)!!
        }
    }

    override fun add(element: T): Boolean {
        val add = root.add(element)
        if (add === root) return false
        root = add
        size++
        return true
    }

    inner class TheIterator: MutableIterator<T> {
        private var backStack: Stack<TreapNode<T>> = stack()
        private var lastReturned: T? = null

        private fun reset() {
            backStack.clear()
            when(val root = root) {
                null -> {}
                else -> backStack.push(root)
            }
        }

        private fun goAllTheWayLeft() {
            while (true) {
                when (val left = backStack.top?.left) {
                    null -> break
                    else -> backStack.push(left)
                }
            }
        }

        init {
            reset()
            goAllTheWayLeft()
        }

        override fun hasNext(): Boolean = !backStack.isEmpty()

        override fun next(): T {
            if (backStack.isEmpty()) throw NoSuchElementException("Iterator.next")
            val current = backStack.pop()

            if (current.right != null) {
                backStack.push(current.right)
                goAllTheWayLeft()
            }
            return current.key.also { lastReturned = it }
        }

        private fun navigateTo(key: T) {
            while (true) {
                val current = backStack.top!!
                when {
                    key cmpEquals current.key -> break
                    key < current.key -> {
                        checkNotNull(current.left)
                        backStack.push(current.left)
                    }
                    else /* key > current.key */ -> {
                        checkNotNull(current.right)
                        backStack.pop()
                        backStack.push(current.right)
                    }
                }
            }
        }

        override fun remove() {
            val currentKey = backStack.top?.key
            remove(checkNotNull(lastReturned))
            if (currentKey != null) {
                reset()
                navigateTo(currentKey)
            }
        }
    }

    override fun iterator(): MutableIterator<T> = TheIterator()
    override fun clear() { root = null; size = 0 }
    override fun comparator(): Comparator<in T>? = comparator_

    private fun union(left: TreapNode<T>?, right: TreapNode<T>?, delta: MutableRef<Int>): TreapNode<T>? {
        left ?: return right
        right ?: return left
        if (left.rank < right.rank) return union(right, left, delta)

        val (rl, rk, rr) = right.split(left.key)
        if (rk != null) delta.value++
        val nleft = union(left.left, rl, delta)
        val nright = union(left.right, rr, delta)
        return left.copy(left = nleft, right = nright)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        if (elements is TreapSet) {
            val sizeRef = ref(0)
            root = union(root, elements.root, sizeRef)

            val oldSize = size
            size += elements.size - sizeRef.value
            return size > oldSize
        }
        return super.addAll(elements)
    }

    override fun removeAll(elements: Collection<T>): Boolean = super.removeAll(elements)

    private fun intersect(left: TreapNode<T>?, right: TreapNode<T>?, delta: MutableRef<Int>): TreapNode<T>? {
        left ?: return null
        right ?: return null
        if (left.rank < right.rank) return intersect(right, left, delta)

        val (rl, rk, rr) = right.split(left.key)
        if (rk != null) delta.value++
        val nleft = intersect(left.left, rl, delta)
        val nright = intersect(left.right, rr, delta)

        return if (rk == null) merge(nleft, nright)
        else left.copy(left = nleft, right = nright)
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        if (elements is TreapSet) {
            val sizeRef = ref(0)
            root = intersect(root, elements.root, sizeRef)
            val oldSize = size
            size = sizeRef.value
            return size < oldSize
        }
        return super.retainAll(elements)
    }

    private fun TreapNode<T>?.first(): T? =
        when(val left = this?.left) {
            null -> this?.key
            else -> left.first()
        }

    override fun first(): T = root.first() ?: throw NoSuchElementException("first")

    private fun TreapNode<T>?.last(): T? = when(val right = this?.right) {
        null -> this?.key
        else -> right.last()
    }
    override fun last(): T = root.last() ?: throw NoSuchElementException("last")

    override fun lower(e: T): T? {
        var current = root ?: return null
        var lastLowerElement: T? = null
        while (true) {
            when {
                e cmpEquals current.key -> {
                    when {
                        current.left != null -> return current.left.last()
                        else -> return lastLowerElement
                    }
                }
                e < current.key -> {
                    current = current.left ?: return lastLowerElement
                }
                else /* e > current.key */ -> {
                    lastLowerElement = current.key
                    current = current.right ?: return current.key
                }
            }
        }
    }

    override fun floor(e: T): T? {
        var current = root ?: return null
        var lastLowerElement: T? = null
        while (true) {
            when {
                e cmpEquals current.key -> return current.key
                e < current.key -> {
                    current = current.left ?: return lastLowerElement
                }
                else /* e > current.key */ -> {
                    lastLowerElement = current.key
                    current = current.right ?: return lastLowerElement
                }
            }
        }
    }

    override fun higher(e: T): T? {
        var current = root ?: return null
        var lastHigherElement: T? = null
        while (true) {
            when {
                e cmpEquals current.key -> {
                    when {
                        current.right != null -> return current.right.first()
                        else -> return lastHigherElement
                    }
                }
                e > current.key -> {
                    current = current.right ?: return lastHigherElement
                }
                else /* e < current.key */ -> {
                    lastHigherElement = current.key
                    current = current.left ?: return current.key
                }
            }
        }
    }

    override fun ceiling(e: T): T? {
        var current = root ?: return null
        var lastHigherElement: T? = null
        while (true) {
            when {
                e cmpEquals current.key -> return current.key
                e > current.key -> {
                    current = current.right ?: return lastHigherElement
                }
                else /* e < current.key */ -> {
                    lastHigherElement = current.key
                    current = current.left ?: return current.key
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is TreapSet<*>) {
            if (size != other.size) return false
            if (comparator_ != other.comparator_)
                return super.equals(other)
            // containsAll() implementation of equals is O(NlogN), while this is O(N)
            val it0 = iterator()
            val it1 = other.iterator()
            while (it0.hasNext()) {
                if (it0.next() != it1.next()) return false
            }
            return true
        } else return super.equals(other)
    }

    fun copy(): TreapSet<T> {
        val res = TreapSet(comparator_, generator)
        res.root = root
        res.size = size
        return res
    }
}

fun <T> mutableSortedSetOf(vararg elements: T, comparator: Comparator<T>): MutableSortedSet<T> {
    val treap = TreapSet(comparator, Random.Default)
    for (e in elements) treap.add(e)
    return treap
}

fun <T: Comparable<T>> mutableSortedSetOf(vararg elements: T): MutableSortedSet<T> {
    val treap = TreapSet<T>(null, Random.Default)
    for (e in elements) treap.add(e)
    return treap
}

fun <T> sortedSetOf(vararg elements: T, comparator: Comparator<T>): SortedSet<T> =
    mutableSortedSetOf(*elements, comparator = comparator)

fun <T: Comparable<T>> sortedSetOf(vararg elements: T): SortedSet<T> =
    mutableSortedSetOf(*elements)

fun <T: Comparable<T>> Iterable<T>.toMutableSortedSet(): MutableSortedSet<T> {
    val treap = TreapSet<T>(null, Random.Default)
    for (e in this) treap.add(e)
    return treap
}

fun <T: Comparable<T>> Iterable<T>.toSortedSet(): SortedSet<T> = toMutableSortedSet()

fun <T> Iterable<T>.toMutableSortedSetWith(comparator: Comparator<T>): MutableSortedSet<T> {
    val treap = TreapSet<T>(comparator, Random.Default)
    for (e in this) treap.add(e)
    return treap
}

fun <T> Iterable<T>.toSortedSetWith(comparator: Comparator<T>): SortedSet<T> =
    toMutableSortedSetWith(comparator)

fun <T: Comparable<T>> Sequence<T>.toMutableSortedSet(): MutableSortedSet<T> {
    val treap = TreapSet<T>(null, Random.Default)
    for (e in this) treap.add(e)
    return treap
}

fun <T: Comparable<T>> Sequence<T>.toSortedSet(): SortedSet<T> = toMutableSortedSet()

fun <T> Sequence<T>.toMutableSortedSetWith(comparator: Comparator<T>): MutableSortedSet<T> {
    val treap = TreapSet<T>(comparator, Random.Default)
    for (e in this) treap.add(e)
    return treap
}

fun <T> Sequence<T>.toSortedSetWith(comparator: Comparator<T>): SortedSet<T> =
    toMutableSortedSetWith(comparator)
