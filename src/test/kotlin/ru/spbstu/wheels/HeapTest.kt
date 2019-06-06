package ru.spbstu.wheels

import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertEquals

class HeapTest {

    @Test
    fun heapSortTest() {

        val random = Random(42)

        run {
            val data = (0..500).toMutableList()
            data.shuffle(random)

            val heap: Heap<Int> = BinaryHeap()
            heap += data

            val heapSorted = generateSequence { if(heap.isEmpty()) null else heap.take() }.toList()

            assertEquals(data.sorted(), heapSorted)
        }

        run {
            val cmp = compareBy { it: Int -> "$it$it"  }
            val data = (0..500).toMutableList()
            data.shuffle(random)

            val heap: Heap<Int> = BinaryHeap(cmp)
            heap += data

            assertEquals(cmp, heap.comparator)

            val heapSorted = generateSequence { if(heap.isEmpty()) null else heap.take() }.toList()

            assertEquals(data.sortedWith(cmp), heapSorted)
        }

    }

}
