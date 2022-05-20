package ru.spbstu.wheels.collections

interface IAbstractCollection<out T>: Collection<T> {
    override fun contains(element: @UnsafeVariance T): Boolean =
        iteratorContains(iterator(), element)

    override fun containsAll(elements: Collection<@UnsafeVariance T>): Boolean =
        elements.all { contains(it) }

    override fun isEmpty(): Boolean = size == 0

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
    override fun toString(): String

    fun <T> IAbstractCollection<T>.collectionHashCode(): Int = ru.spbstu.wheels.orderedHashCode(this)

    fun <T> IAbstractCollection<T>.collectionToString(prefix: String = "[",
                                                      suffix: String = "]"): String {
        val sb = StringBuilder(prefix)
        forEachIndexed { i, t ->
            if (i != 0) sb.append(", ")
            sb.append(t)
        }
        sb.append(suffix)
        return sb.toString()
    }
}
