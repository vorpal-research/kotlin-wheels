package benchmarks

import kotlinx.benchmark.*
import ru.spbstu.wheels.forEachB

@State(Scope.Benchmark)
@Warmup(10)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
open class TestBenchmark {

    lateinit var lst: List<Int>

    @Setup
    fun prepare() {
        lst = (0..20000).toList()
    }

    @Benchmark
    fun beNormal() {
        for (e in lst) {
            if (e % 3 == 0) continue
            if (e - 2 > 10000) break
        }
    }

    @Benchmark
    fun beBreakable() {
        lst.forEachB { e ->
            if (e % 3 == 0) continue_
            if (e - 2 > 10000) break_
        }
    }
}