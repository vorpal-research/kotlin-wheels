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

private inline fun <A, B, R> product(crossinline left: () -> Iterator<A>,
                                     crossinline right: () -> Iterator<B>,
                                     crossinline body: (A, B) -> R): Sequence<R> = sequence {
    for(a in left()) {
        for(b in right()) {
            yield(body(a, b))
        }
    }
}

fun <A, B, R> Sequence<A>.product(that: Sequence<B>, body: (A, B) -> R): Sequence<R> =
        product({ this.iterator() }, { that.iterator() }, body)

infix fun <A, B> Sequence<A>.product(that: Sequence<B>): Sequence<Pair<A, B>> =
        product(that, ::Pair)

fun <A, B, R> Sequence<A>.product(that: Iterable<B>, body: (A, B) -> R): Sequence<R> =
        product({ this.iterator() }, { that.iterator() }, body)

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

@PublishedApi
internal fun <T> Iterator<T>.putBack(value: T): Iterator<T> = iterator {
    yield(value)
    yieldAll(this@putBack)
}

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

fun <T> intersperse(vararg seqs: Sequence<T>): Sequence<T> = run {
    require(seqs.isNotEmpty())
    sequence {
        val iterators = seqs.map { it.iterator() }
        while (true) {
            for (it in iterators) {
                if (it.hasNext()) yield(it.next())
                else return@sequence
            }
        }
    }
}

fun <T> Collection<Sequence<T>>.intersperse(): Sequence<T> = intersperse(*toTypedArray())
