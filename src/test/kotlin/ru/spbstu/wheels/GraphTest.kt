package ru.spbstu.wheels

import kotlin.math.sqrt
import kotlin.test.Test

class GraphTest {
    data class Cell(val x: Int, val y: Int)

    fun sqr(x: Double) = x * x

    operator fun Cell.minus(rhv: Cell) = let { lhv ->
        sqrt(sqr(lhv.x.toDouble() - rhv.x) + sqr(lhv.y.toDouble() - rhv.y))
    }

    @Test
    fun testAStar() {

        val p = aStarSearch(
                Cell(0, 0),
                heur = { it - Cell(20, 20) },
                goal = { it == Cell(20, 20) },
                neighbours = {
                    sequenceOf(
                            Cell(it.x + 1, it.y + 2),
                            Cell(it.x + 1, it.y - 2),
                            Cell(it.x - 1, it.y + 2),
                            Cell(it.x - 1, it.y - 2),
                            Cell(it.x + 2, it.y + 1),
                            Cell(it.x + 2, it.y - 1),
                            Cell(it.x - 2, it.y + 1),
                            Cell(it.x - 2, it.y - 1)
                    )
                }
        )?.toSet()

        if(p != null) {
            for(y in 0..30) {
                for(x in 0..30) {
                    if(Cell(x, y) !in p) print("@")
                    else print("*")
                }
                println()
            }
        }

    }
}