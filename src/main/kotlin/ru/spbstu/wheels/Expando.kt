package ru.spbstu.wheels

import kotlinx.warnings.Warnings
import kotlin.reflect.KProperty

@Suppress(Warnings.NOTHING_TO_INLINE)
open class Expando {
    companion object {
        inline operator fun <Self: Expando, T: Any> getValue(thisRef: Self, prop: KProperty<*>): T =
                uncheckedCast<T>(thisRef.expansion.map[prop.name]
                        ?: throw IllegalStateException("Property $prop not initialized yet"))
        inline operator fun <Self: Expando, T: Any> setValue(thisRef: Self, prop: KProperty<*>, newValue: T) {
            thisRef.expansion.map[prop.name] = newValue
        }

        class Lazy<T>(val body: () -> T) {
            inline operator fun <Self: Expando> getValue(thisRef: Self, prop: KProperty<*>): T =
                    uncheckedCast<T>(thisRef.expansion.map.getOrPut(prop.name, body))
            inline operator fun <Self: Expando, T: Any> setValue(thisRef: Self, prop: KProperty<*>, newValue: T) {
                thisRef.expansion.map[prop.name] = newValue
            }
        }

        fun <T> lazy(body: () -> T) = Lazy(body)
    }

    val expansion: Expansion = Expansion()
}


inline class Expansion(val map: MutableMap<String, Any?> = mutableMapOf()) {
    override inline fun toString(): String = map.entries.joinToString(", ") { (k, v) -> "$k=$v" }
}
