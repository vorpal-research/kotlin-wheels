package ru.spbstu.wheels

import kotlin.test.Test
import kotlin.test.assertEquals

class ProductTest {
    interface Expr: Product {
        val location: Int
    }
    object Null: Expr {
        override val location: Int
            get() = 0
    }
    data class Variable(val name: String, override val location: Int): Expr
    data class BinaryExpr(val lhv: Expr, val rhv: Expr, override val location: Int): Expr

    object Locus: Expr {
        override val location: Int
            get() = 32

        override fun component1() = location
    }

    @Test
    fun simpleCheck() {
        assertEquals(listOf(), Null.productSequence().toList())
        assertEquals(
            listOf(Null, Null, 0),
            BinaryExpr(Null, Null, 0).productSequence().toList()
        )
        assertEquals(
            listOf("Hello", 0),
            Variable("Hello", 0).productSequence().toList()
        )
        assertEquals(
            listOf(32),
            Locus.productSequence().toList()
        )
    }
}