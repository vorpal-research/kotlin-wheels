package ru.spbstu.wheels

import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SortedSetTest {
    private enum class OP { ADD, REMOVE }
    private enum class PICK { RANDOM, EXISTING }

    private val randomIterations = if (currentPlatform == Platform.JS) 5 else 100

    @Test
    fun basic() {
        randomIterations times { i ->
            val random = Random(42 * i)
            val st = TreapSet<Int>(comparator_ = null, generator = random)
            val data = random.ints(1000, 0, 10000).toSet()
            st.addAll(data)

            assertEquals(data, st)
            assertTrue(st == data)
            assertEquals(data.minOrNull(), st.first())
            assertEquals(data.maxOrNull(), st.last())

            val sortedData = data.sorted()

            fun testAdjacent(value: Int) {
                assertEquals(sortedData.lastOrNull { it < value }, st.lower(value))
                assertEquals(sortedData.lastOrNull { it <= value }, st.floor(value))

                assertEquals(sortedData.firstOrNull { it > value }, st.higher(value))
                assertEquals(sortedData.firstOrNull { it >= value }, st.ceiling(value))
            }

            testAdjacent(5000)

            val median = sortedData[sortedData.size / 2]

            testAdjacent(median)
            testAdjacent(0)
            testAdjacent(10000)
            testAdjacent(-1)
            testAdjacent(10001)
            for (i in 1..9) {
                val element = sortedData[sortedData.size.times(i.toDouble() / 10).roundToInt()]
                testAdjacent(element)
                testAdjacent(element + 1)
                testAdjacent(element - 1)
            }

            st.clear()
            assertEquals(setOf(), st)
            assertTrue(st == setOf<Int>())

        }
    }


    @Test
    fun addRemove() {
        randomIterations times { i ->
            val random = Random(100 * i)
            val data = random.ints(1000, 0, 10000).toList()

            val ts = TreapSet<Int>(comparator_ = null, generator = random)
            ts.addAll(data)

            val setData = data.toMutableSet()
            assertEquals(setData, ts)

            for (i in 0 .. random.nextInt(1, 100)) {
                val op = random.nextEnum<OP>()
                val pick = random.nextEnum<PICK>()

                val element = when(pick) {
                    PICK.RANDOM -> random.nextInt()
                    PICK.EXISTING -> setData.random(random)
                }

                when(op) {
                    OP.ADD -> {
                        ts.add(element)
                        setData.add(element)
                    }
                    OP.REMOVE -> {
                        ts.remove(element)
                        setData.remove(element)
                    }
                }

                assertEquals(setData, ts)
            }
        }
    }

    @Test
    fun equality() {
        randomIterations times { i ->
            val random = Random(100 * i)
            val data = random.ints(1000, 0, 1000).toList()

            val ts = TreapSet<Int>(null, random)
            val ts2 = TreapSet<Int>(null, random)
            assertEquals(setOf(), ts)
            assertEquals(ts, ts2)
            ts.addAll(data)
            ts2.addAll(data)
            assertEquals(ts, ts2)
            ts2.add(10000)
            assertNotEquals(ts, ts2)
            ts.add(10000)
            assertEquals(ts, ts2)
        }
    }

    @Test
    fun bulkOps() {
        randomIterations times { i ->
            val random = Random(100 * i)
            val data = random.ints(1000, 0, 1000).toList()

            val ts = TreapSet<Int>(null, random)
            ts.addAll(data)
            val setData = data.toMutableSet()

            assertEquals(setData, ts.apply { addAll(ts) })
            assertEquals(setData, ts.apply { retainAll(ts) })

            run {
                val data1 = random.ints(1000, 0, 1000).toList()

                val ts1 = TreapSet<Int>(null, random)
                ts1.addAll(data1)
                val setData1 = data1.toSet()

                ts.addAll(ts1)
                setData.addAll(setData1)
                assertEquals(setData, ts)
            }

        }
    }

}