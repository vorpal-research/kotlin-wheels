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

internal class ProductSequence<A, B, R> (private val left: () -> Iterator<A>,
                                         private val right: () -> Iterator<B>,
                                         private val transform: (A, B) -> R): Sequence<R> {
    inner class TheIterator : Iterator<R> {
        private val currentLeft = left()
        private var currentRight = right()
        private var currentLeftValue: Option<A> = Option.empty()

        private fun leftIsEmpty() = currentLeftValue.isEmpty() && !currentLeft.hasNext()
        private fun rightIsEmpty() = currentLeftValue.isEmpty() && !currentRight.hasNext()

        override fun hasNext(): Boolean =
                !leftIsEmpty() && !rightIsEmpty() && (currentRight.hasNext() || currentLeft.hasNext())

        override fun next(): R {
            if(currentLeftValue.isEmpty() || !currentRight.hasNext())
                currentLeftValue = currentLeft.nextOption()

            if(!currentRight.hasNext()) currentRight = right()

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
