package ru.spbstu.wheels

import ru.spbstu.wheels.Product.Companion.LastElement
import ru.spbstu.wheels.Product.Companion.MAX_SIZE
import ru.spbstu.wheels.Product.Companion.requireNotLast

interface Product {
    companion object {
        val LastElement = Any()
        val MAX_SIZE = 10

        internal fun Any?.requireNotLast(message: String): Any? =
            if (this === LastElement) throw NoSuchElementException(message) else this
    }

    operator fun component1(): Any? = LastElement
    operator fun component2(): Any? = LastElement
    operator fun component3(): Any? = LastElement
    operator fun component4(): Any? = LastElement
    operator fun component5(): Any? = LastElement
    operator fun component6(): Any? = LastElement
    operator fun component7(): Any? = LastElement
    operator fun component8(): Any? = LastElement
    operator fun component9(): Any? = LastElement
    operator fun component10(): Any? = LastElement
    operator fun component11(): Nothing = throw IllegalStateException("Product max size exceeded")
}

private fun Product.componentAt(index: Int): Any? = when(index) {
    0 -> component1()
    1 -> component2()
    2 -> component3()
    3 -> component4()
    4 -> component5()
    5 -> component6()
    6 -> component7()
    7 -> component8()
    8 -> component9()
    9 -> component10()
    else -> LastElement
}

fun Product.productElementAt(index: Int): Any? =
    componentAt(index).requireNotLast("Product.productElementAt($index)")

fun Product.productIterator() = object: Iterator<Any?> {
    var index = 0

    override fun hasNext(): Boolean =
        index < MAX_SIZE && componentAt(index) !== LastElement

    override fun next(): Any? = componentAt(index++).requireNotLast("ProductIterator.next()")
}
