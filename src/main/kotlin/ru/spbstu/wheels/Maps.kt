package ru.spbstu.wheels

import kotlinx.warnings.Warnings

data class SimpleEntry<out K, out V>(override val key: K, override val value: V) : Map.Entry<K, V> {

    override fun equals(other: Any?): Boolean =
            other is Map.Entry<*, *> && key == other.key && value == other.value

    override fun hashCode(): Int = key.hashCode() xor value.hashCode()

    override fun toString(): String = "$key=$value"
}

@Suppress(Warnings.UNCHECKED_CAST)
fun <K, V> Map<K, V>.getEntry(key: K): Map.Entry<K, V>? = when (key) {
    in this -> SimpleEntry(key, get(key) as V) // not !! because V may be nullable
    else -> null
}

@Suppress(Warnings.UNCHECKED_CAST)
fun <K, V> Map<K, V>.getOption(key: K): Option<V> = when(key) {
    in this -> Option.just(get(key) as V) // not !! because V may be nullable
    else -> Option.empty()
}

fun <K, V, M: MutableMap<K, V>> Iterable<Map.Entry<K, V>>.toMap(m: M): M =
        m.apply { this@toMap.forEach { put(it.key, it.value) } }

fun <K, V> Iterable<Map.Entry<K, V>>.toMap(): Map<K, V> = when(this) {
    is Collection -> (this as Collection<Map.Entry<K, V>>).toMap()
    else -> toMap(mutableMapOf())
}

fun <K, V> Collection<Map.Entry<K, V>>.toMap(): Map<K, V> = when (size) {
    0 -> mapOf()
    1 -> first().let { mapOf(it.key to it.value) }
    else -> toMap(LinkedHashMap(size))
}

fun <K, V, M: MutableMap<K, V>> Sequence<Map.Entry<K, V>>.toMap(m: M): M =
        m.apply { this@toMap.forEach { put(it.key, it.value) } }

fun <K, V> Sequence<Map.Entry<K, V>>.toMap(): Map<K, V> = toMap(mutableMapOf())