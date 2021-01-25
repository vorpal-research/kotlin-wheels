package ru.spbstu.wheels

import kotlinx.warnings.Warnings
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

interface Reification<T> {
    operator fun contains(value: Any?): Boolean
    fun cast(value: Any?): T
    fun arrayOf(vararg values: T): Array<T>

    val type: KType
}

fun <T> Reification<T>.safeCast(value: Any?): T? =
    if (contains(value)) cast(value) else null

interface ClassReification<T : Any> : Reification<T> {
    val kClass: KClass<T>
}

inline fun <reified T> Reification(@Suppress(Warnings.UNUSED_PARAMETER) vararg lowOverloadPriority: Empty) =
    object : Reification<T> {
        override fun contains(value: Any?): Boolean = value is T
        override fun cast(value: Any?): T = value as T
        override fun arrayOf(vararg values: T): Array<T> = arrayOf<T>(*values)

        override val type: KType
            get() = typeOf<T>()
    }

inline fun <reified T : Any> Reification() = object : ClassReification<T> {
    override fun contains(value: Any?): Boolean = value is T
    override fun cast(value: Any?): T = value as T
    override fun arrayOf(vararg values: T): Array<T> = arrayOf<T>(*values)

    override val type: KType
        get() = typeOf<T>()
    override val kClass: KClass<T>
        get() = T::class
}
