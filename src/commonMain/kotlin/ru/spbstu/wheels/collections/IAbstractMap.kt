package ru.spbstu.wheels.collections

import ru.spbstu.wheels.SimpleEntry
import ru.spbstu.wheels.orderedHashCode

class AbstractKeys<K, out V> (val map: IAbstractMap<K, V>): IAbstractSet<K> {
    override val size: Int
        get() = map.size

    override fun iterator(): Iterator<K> = map.keyIterator()

    override fun contains(element: K): Boolean = map.containsKey(element)

    override fun equals(other: Any?): Boolean = setEquals(other)
    override fun hashCode(): Int = setHashCode()
    override fun toString(): String = setToString()
}

class AbstractValues<K, out V> (val map: IAbstractMap<K, V>): IAbstractCollection<V> {
    override val size: Int
        get() = map.size

    override fun iterator(): Iterator<V> = map.valueIterator()

    override fun equals(other: Any?): Boolean =
        other is AbstractValues<*, *> && other.map == map
    override fun hashCode(): Int = orderedHashCode(this)
    override fun toString(): String = collectionToString("[", "]")
}

class AbstractEntries<K, out V> (val map: IAbstractMap<K, V>): IAbstractSet<Map.Entry<K, V>> {
    override val size: Int
        get() = map.size

    override fun contains(element: Map.Entry<K, @UnsafeVariance V>): Boolean {
        val mapValue = map[element.key]
        if (mapValue != element.value) return false
        if (mapValue == null) return map.containsKey(element.key)
        return true
    }

    override fun iterator(): Iterator<Map.Entry<K, V>> = map.entryIterator()

    override fun equals(other: Any?): Boolean = setEquals(other)
    override fun hashCode(): Int = setHashCode()
    override fun toString(): String = setToString()
}

interface IAbstractMap<K, out V> : Map<K, V> {

    override val keys: Set<K>
        get() = AbstractKeys(this)
    override val values: Collection<V>
        get() = AbstractValues(this)
    override val entries: Set<Map.Entry<K, V>>
        get() = AbstractEntries(this)

    fun entryIterator(): Iterator<Map.Entry<K, V>>
    fun keyIterator(): Iterator<K> = mappingIterator(entryIterator()) { it.key }
    fun valueIterator(): Iterator<V> = mappingIterator(entryIterator()) { it.value }

    override fun containsValue(value: @UnsafeVariance V): Boolean =
        iteratorIndexOf(entryIterator()) { it.value == value } != -1

    override fun isEmpty(): Boolean = size == 0

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
    override fun toString(): String

    fun IAbstractMap<K, @UnsafeVariance V>.mapEquals(other: Any?): Boolean {
        if (other !is Map<*, *>) return false
        if (other.size != size) return false
        for ((k, theirs) in other.entries) {
            val ours = this[k]
            if (theirs != ours) return false
            if (ours == null && !containsKey(k)) return false
        }
        return true
    }

    fun IAbstractMap<K, @UnsafeVariance V>.mapHashcode(): Int = entries.hashCode()
    fun IAbstractMap<K, @UnsafeVariance V>.mapToString(): String {
        val sb = StringBuilder("{")
        var index = 0
        for ((k, v) in entryIterator()) {
            if (index++ != 0) sb.append(", ")
            sb.append(k).append("=").append(v)
        }
        sb.append("}")
        return sb.toString()
    }

    interface ByKeys<K, V>: IAbstractMap<K, V> {
        override fun keyIterator(): Iterator<K>
        override fun entryIterator(): Iterator<Map.Entry<K, V>> =
            mappingIterator(keyIterator()) { SimpleEntry(it, get(it)!!) }

        override fun equals(other: Any?): Boolean
        override fun hashCode(): Int
        override fun toString(): String
    }

    abstract class Impl<K, out V> : IAbstractMap<K, V> {
        override fun equals(other: Any?): Boolean = mapEquals(other)
        override fun hashCode(): Int = mapHashcode()
        override fun toString(): String = mapToString()
    }

}