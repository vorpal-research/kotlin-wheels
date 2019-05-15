package ru.spbstu.wheels

internal class MemoizedSequence<T>(
        base: Sequence<T>,
        private val memoizeTo: MutableList<@UnsafeVariance T> = mutableListOf()) : Sequence<T> {
    private val baseIt = base.iterator()

    inner class TheIterator : Iterator<T> {
        private var index = 0

        private fun isInside() = index < memoizeTo.size

        override fun hasNext(): Boolean = isInside() || baseIt.hasNext()

        override fun next(): T = when {
            isInside() -> memoizeTo[index]
            else -> baseIt.next().also { memoizeTo.add(it) }
        }.also { ++index }
    }

    override fun iterator(): Iterator<T> = TheIterator()
}

fun <T> Sequence<T>.memoize(): Sequence<T> = when (this) {
    is MemoizedSequence -> this
    else -> MemoizedSequence(this)
}

fun <T> Sequence<T>.memoizeTo(storage: MutableList<T>): Sequence<T> = MemoizedSequence(this, storage)

internal class ProductSequence<A, B, R>(private val left: () -> Iterator<A>,
                                        private val right: () -> Iterator<B>,
                                        private val transform: (A, B) -> R) : Sequence<R> {
    inner class TheIterator : Iterator<R> {
        private val currentLeft = left()
        private var currentRight = right()
        private var currentLeftValue: Option<A> = Option.empty()

        private fun leftIsEmpty() = currentLeftValue.isEmpty() && !currentLeft.hasNext()
        private fun rightIsEmpty() = currentLeftValue.isEmpty() && !currentRight.hasNext()

        override fun hasNext(): Boolean =
                !leftIsEmpty() && !rightIsEmpty() && (currentRight.hasNext() || currentLeft.hasNext())

        override fun next(): R {
            if (currentLeftValue.isEmpty() || !currentRight.hasNext())
                currentLeftValue = currentLeft.nextOption()

            if (!currentRight.hasNext()) currentRight = right()

            val lefty = currentLeftValue.get()

            return transform(lefty, currentRight.next())
        }
    }

    override fun iterator(): Iterator<R> = TheIterator()
}

fun <A, B, R> Sequence<A>.product(that: Sequence<B>, body: (A, B) -> R): Sequence<R> =
        ProductSequence({ this.iterator() }, { that.iterator() }, body)

infix fun <A, B> Sequence<A>.product(that: Sequence<B>): Sequence<Pair<A, B>> =
        product(that, ::Pair)

fun <A, B, R> Sequence<A>.product(that: Iterable<B>, body: (A, B) -> R): Sequence<R> =
        ProductSequence({ this.iterator() }, { that.iterator() }, body)

infix fun <A, B> Sequence<A>.product(that: Iterable<B>): Sequence<Pair<A, B>> =
        product(that, ::Pair)

fun <T> Iterator<T>.peekSomeTo(howMany: Int, to: MutableCollection<T>): Iterator<T> {
    repeat(howMany) {
        if (hasNext()) to.add(next())
        else return this
    }
    return this
}

fun <T> Sequence<T>.peekSomeTo(howMany: Int, to: MutableCollection<T>): Sequence<T> =
        iterator().peekSomeTo(howMany, to).asSequence()

fun <T> Iterator<T>.peekSome(howMany: Int): Pair<List<T>, Iterator<T>> {
    val list = mutableListOf<T>()
    val rest = peekSomeTo(howMany, list)
    return list to rest
}

fun <T> Sequence<T>.peekSome(howMany: Int): Pair<List<T>, Sequence<T>> {
    val list = mutableListOf<T>()
    val rest = peekSomeTo(howMany, list)
    return list to rest
}

fun <T> Iterator<T>.peekFirst(): Pair<T, Iterator<T>> =
        if (hasNext()) next() to this
        else throw NoSuchElementException("peekFirst()")

fun <T> Sequence<T>.peekFirst(): Pair<T, Sequence<T>> {
    val iterator = iterator()
    if (iterator.hasNext()) return iterator.next() to iterator.asSequence()
    else throw NoSuchElementException("peekFirst()")
}

fun <T> Iterator<T>.peekFirstOrNull(): Pair<T?, Iterator<T>> =
        when {
            hasNext() -> next() to this
            else -> null to this
        }

fun <T> Sequence<T>.peekFirstOrNull(): Pair<T?, Sequence<T>> =
        iterator().run {
            when {
                hasNext() -> next() to asSequence()
                else -> null to this@peekFirstOrNull
            }
        }

private class PutBackIterator<T>(var first: Option<T>, val iterator: Iterator<T>): Iterator<T> {
    override fun hasNext(): Boolean = first.isNotEmpty() || iterator.hasNext()

    override fun next(): T = when {
        first.isNotEmpty() -> first.get().also { first = Option.empty() }
        else -> iterator.next()
    }
}

@PublishedApi
internal fun <T> Iterator<T>.putBack(value: T): Iterator<T> =
        PutBackIterator(Option.just(value), this)

inline fun <T> Iterator<T>.peekWhileTo(to: MutableCollection<T>,
                                       predicate: (T) -> Boolean): Iterator<T> {
    while (hasNext()) {
        val nxt = next()
        if (predicate(nxt)) to.add(nxt)
        else return this.putBack(nxt)
    }
    return this
}

inline fun <T> Sequence<T>.peekWhileTo(to: MutableCollection<T>,
                                       predicate: (T) -> Boolean): Sequence<T> =
        iterator().peekWhileTo(to, predicate).asSequence()

inline fun <T> Iterator<T>.peekWhile(predicate: (T) -> Boolean): Pair<List<T>, Iterator<T>> {
    val list = mutableListOf<T>()
    val rest = peekWhileTo(list, predicate)
    return list to rest
}

inline fun <T> Sequence<T>.peekWhile(predicate: (T) -> Boolean): Pair<List<T>, Sequence<T>> {
    val list = mutableListOf<T>()
    val rest = peekWhileTo(list, predicate)
    return list to rest
}

private class IntersperseSequence<T>(val seqs: Array<out Sequence<T>>): Sequence<T> {
    init {
        require(seqs.isNotEmpty())
    }

    private inner class TheIterator: Iterator<T> {
        val iters = seqs.map { it.iterator() }
        var currentIndex = 0

        inline val currentIterator get() = iters[currentIndex]

        override fun hasNext(): Boolean = currentIterator.hasNext()
        override fun next(): T {
            val res = currentIterator.next()
            if(currentIndex < iters.lastIndex) ++currentIndex
            else currentIndex = 0
            return res
        }
    }
    override fun iterator(): Iterator<T> = TheIterator()
}

fun <T> intersperse(vararg seqs: Sequence<T>): Sequence<T> = IntersperseSequence(seqs)
fun <T> Collection<Sequence<T>>.intersperse(): Sequence<T> = IntersperseSequence(toTypedArray())
