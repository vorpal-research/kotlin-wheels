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

inline fun Product.forEachProductComponent(body: (Any?) -> Unit) {
    component1().takeUnless { it == LastElement }?.let(body) ?: return
    component2().takeUnless { it == LastElement }?.let(body) ?: return
    component3().takeUnless { it == LastElement }?.let(body) ?: return
    component4().takeUnless { it == LastElement }?.let(body) ?: return
    component5().takeUnless { it == LastElement }?.let(body) ?: return
    component6().takeUnless { it == LastElement }?.let(body) ?: return
    component7().takeUnless { it == LastElement }?.let(body) ?: return
    component8().takeUnless { it == LastElement }?.let(body) ?: return
    component9().takeUnless { it == LastElement }?.let(body) ?: return
    component10().takeUnless { it == LastElement }?.let(body) ?: return
}

fun Product.productElementAt(index: Int): Any? =
    componentAt(index).requireNotLast("Product.productElementAt($index)")

fun Product.productIterator() = object: Iterator<Any?> {
    var index = 0

    override fun hasNext(): Boolean =
        index < MAX_SIZE && componentAt(index) !== LastElement

    override fun next(): Any? = componentAt(index++).requireNotLast("ProductIterator.next()")
}

fun Product.productSequence(): Sequence<Any?> = Sequence { productIterator() }

open class ProductVisitor {
    fun visitAny(any: Any?) {
        when (any) {
            is Product -> visitProduct(any)
            is Iterable<*> -> any.forEach { visitAny(it) }
            is Array<*> -> any.forEach { visitAny(it) }
            is Map<*, *> -> any.forEach { visitAny(it.key); visitAny(it.value) }
            is Pair<*, *> -> { visitAny(any.first); visitAny(any.second) }
            is Triple<*, *, *> -> { visitAny(any.first); visitAny(any.second); visitAny(any.third) }
            else -> default(any)
        }
    }

    open fun default(any: Any?) {}
    open fun visitProduct(p: Product) {
        p.forEachProductComponent { visitAny(it) }
    }
}

fun Product.acceptProductVisitor(visitor: ProductVisitor) {
    visitor.visitProduct(this)
}

@PublishedApi
internal abstract class AutoProductVisitor: ProductVisitor() {
    private inner class SuperProxy: ProductVisitor() {
        override fun visitProduct(p: Product) = superVisitProduct(p)
    }
    private fun superVisitProduct(p: Product) = super.visitProduct(p)

    protected val superProxy: ProductVisitor = SuperProxy()
}

inline fun <reified T> Product.acceptProductVisitor(crossinline visitorBody: ProductVisitor.(T) -> Unit) {
    val visitor = object: AutoProductVisitor() {
        override fun default(any: Any?) {
            if (any is T) superProxy.visitorBody(any)
            else super.default(any)
        }

        override fun visitProduct(p: Product) {
            if (p is T) superProxy.visitorBody(p)
            else super.visitProduct(p)
        }
    }

    visitor.visitProduct(this)
}
