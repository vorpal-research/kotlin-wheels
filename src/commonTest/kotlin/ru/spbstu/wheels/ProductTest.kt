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

    @Test
    fun visitorCheck() {
        val bb = BinaryExpr(BinaryExpr(Locus, Variable("x", 2), 4), Variable("x", 8), 5)

        val varSet = mutableSetOf<Variable>()
        bb.acceptProductVisitor { v: Variable -> varSet += v }
        assertEquals(setOf(Variable("x", 2), Variable("x", 8)), varSet)

        val binExpLoci = mutableListOf<Int>()
        bb.acceptProductVisitor { b: BinaryExpr ->
            binExpLoci += b.location
            visitProduct(b)
        }
        assertEquals(listOf(5, 4), binExpLoci)

        val numbers = mutableListOf<Int>()
        bb.acceptProductVisitor(object: ProductVisitor() {
            override fun default(any: Any?) {
                if (any is Int) numbers.add(any)
            }
        })

        assertEquals(listOf(32, 2, 4, 8, 5), numbers)
    }
}