package ru.spbstu.wheels.collections

import ru.spbstu.wheels.SimpleEntry
import ru.spbstu.wheels.SimpleMutableEntry
import ru.spbstu.wheels.orderedHashCode

class AbstractMutableKeys<K, V>(val map: IAbstractMutableMap<K, V>): IAbstractMutableSet<K> {
    override val size: Int
        get() = map.size

    override fun clear() {
        map.clear()
    }

    override fun iterator(): MutableIterator<K> = map.keyIterator()

    override fun contains(element: K): Boolean = map.containsKey(element)

    override fun add(element: K): Boolean = throw UnsupportedOperationException("add")

    override fun remove(element: K): Boolean {
        val size = map.size
        map.remove(element)
        return map.size != size
    }
    override fun equals(other: Any?): Boolean = setEquals(other)
    override fun hashCode(): Int = setHashCode()
    override fun toString(): String = setToString()
}

class AbstractMutableValues<K, V>(val map: IAbstractMutableMap<K, V>): IAbstractMutableCollection<V> {
    override val size: Int
        get() = map.size

    override fun clear() {
        map.clear()
    }

    override fun iterator(): MutableIterator<V> = map.valueIterator()

    override fun add(element: V): Boolean = throw UnsupportedOperationException("add")

    override fun remove(element: V): Boolean {
        val size = map.size

        val it = map.entryIterator()
        for ((_, v) in it) {
            if (v == element) {
                it.remove()
                break
            }
        }
        return map.size != size
    }

    override fun equals(other: Any?): Boolean =
        other is AbstractValues<*, *> && other.map == map
    override fun hashCode(): Int = orderedHashCode(this)
    override fun toString(): String = collectionToString("[", "]")
}

class AbstractMutableEntries<K, V>(val map: IAbstractMutableMap<K, V>): IAbstractMutableSet<MutableMap.MutableEntry<K, V>> {
    override val size: Int
        get() = map.size

    override fun clear() {
        map.clear()
    }

    override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> = map.entryIterator()

    override fun add(element: MutableMap.MutableEntry<K, V>): Boolean {
        val size = map.size
        map.put(element.key, element.value)
        return map.size != size
    }

    override fun remove(element: MutableMap.MutableEntry<K, V>): Boolean {
        val size = map.size

        if (contains(element)) map.remove(element.key)

        return map.size != size
    }

    override fun contains(element: MutableMap.MutableEntry<K, V>): Boolean {
        val existing = map[element.key]
        if (existing != element.value) return false
        if (element.value == null) return map.containsKey(element.key)
        return true
    }

    override fun equals(other: Any?): Boolean = setEquals(other)
    override fun hashCode(): Int = setHashCode()
    override fun toString(): String = setToString()

}

interface IAbstractMutableMap<K, V>: MutableMap<K, V>, IAbstractMap<K, V> {
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = AbstractMutableEntries(this)
    override val keys: MutableSet<K>
        get() = AbstractMutableKeys(this)
    override val values: MutableCollection<V>
        get() = AbstractMutableValues(this)

    override fun entryIterator(): MutableIterator<MutableMap.MutableEntry<K, V>>
    override fun keyIterator(): MutableIterator<K> = mappingIterator(entryIterator()) { it.key }
    override fun valueIterator(): MutableIterator<V> = mappingIterator(entryIterator()) { it.value }

    override fun putAll(from: Map<out K, V>) {
        for ((k, v) in from) put(k, v)
    }
    override fun clear() {
        for ((k, _) in entryIterator()) remove(k)
    }

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
    override fun toString(): String

    interface ByKeys<K, V> : IAbstractMutableMap<K, V> {
        override fun keyIterator(): MutableIterator<K>
        override fun entryIterator(): MutableIterator<MutableMap.MutableEntry<K, V>> =
            mappingIterator(keyIterator()) { key ->
                val value = get(key)!!
                SimpleMutableEntry(key, value) {
                    newValue -> put(key, newValue)!!
                }
            }

        abstract class Impl<K, V> : ByKeys<K, V> {
            override fun equals(other: Any?): Boolean = mapEquals(other)
            override fun hashCode(): Int = mapHashcode()
            override fun toString(): String = mapToString()
        }
    }

    abstract class Impl<K, V> : IAbstractMutableMap<K, V> {
        override fun equals(other: Any?): Boolean = mapEquals(other)
        override fun hashCode(): Int = mapHashcode()
        override fun toString(): String = mapToString()
    }

}