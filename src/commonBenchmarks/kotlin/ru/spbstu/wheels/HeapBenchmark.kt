package ru.spbstu.wheels

import kotlinx.benchmark.*
import kotlin.random.Random

@State(Scope.Benchmark)
open class HeapBenchmark {
    lateinit var random: Random
    lateinit var data: List<Int>

    @Setup
    fun setup() {
        random = Random(32)
        data = random.ints(3000).toList()
    }

    @Benchmark
    fun tryNormalHeap(bh: Blackhole) {
        val heap = heap(naturalOrder<Int>().reversed())
        for (e in data) heap.put(e)

        while(heap.isNotEmpty()) {
            bh.consume(heap.take())
        }
    }

    @Benchmark
    fun tryInlinedHeap(bh: Blackhole) {
        val heap = object : AbstractBinaryHeap<Int>() {
            override fun compare(lhv: Int, rhv: Int): Int = -lhv.compareTo(rhv)
        }
        for (e in data) heap.put(e)

        while(heap.isNotEmpty()) {
            bh.consume(heap.take())
        }
    }

}