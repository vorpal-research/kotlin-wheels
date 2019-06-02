package ru.spbstu.wheels

import kotlin.reflect.KProperty

interface MutableRef<T> {
    var value: T
}

data class SimpleMutableRef<T>(override var value: T): MutableRef<T>

fun <T> MutableRef<T>.assign(value: T) {
    this.value = value
}

infix fun <T> MutableRef<T>.`=`(value: T) = assign(value)

fun <T> ref(value: T): MutableRef<T> = SimpleMutableRef(value)

operator fun <T> MutableRef<T>.getValue(thisRef: Any?, prop: KProperty<*>) = value
operator fun <T> MutableRef<T>.setValue(thisRef: Any?, prop: KProperty<*>, value: T) { this.value = value }

class OutRef<T>(): MutableRef<T> {
    private var option: Option<T> = Option.empty()
    override var value: T
        get() = option.getOrElse { throw IllegalStateException("Out value reference not set yet") }
        set(value) { option = Option.just(value) }

    override fun toString(): String = "OutRef($option)"
}

fun <T> out(): MutableRef<T> = OutRef()
