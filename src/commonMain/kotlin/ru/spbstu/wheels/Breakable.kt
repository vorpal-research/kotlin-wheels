package ru.spbstu.wheels

object Break : NoStackThrowable("break")
object Continue : NoStackThrowable("continue")

object BreakableContext {
    val break_: Nothing get() = throw Break
    val continue_: Nothing get() = throw Continue

    inline fun <T> iteration(body: BreakableContext.() -> T): T? =
            try {
                this.body()
            } catch (_: Continue) {
                null
            }

    inline fun <T> loop(body: BreakableContext.() -> T): T? =
            try {
                this.body()
            } catch (_: Break) {
                null
            }
}

inline fun <T> Iterable<T>.forEachB(body: BreakableContext.(T) -> Unit) {
    BreakableContext.loop {
        forEach {
            iteration { body(it) }
        }
    }
}

inline fun <T> Sequence<T>.forEachB(body: BreakableContext.(T) -> Unit) {
    BreakableContext.loop {
        forEach {
            iteration { body(it) }
        }
    }
}

inline fun <T> Iterable<T>.forEachIndexedB(body: BreakableContext.(Int, T) -> Unit) {
    BreakableContext.loop {
        forEachIndexed { i, e ->
            iteration { body(i, e) }
        }
    }
}

inline fun <T> Sequence<T>.forEachIndexedB(body: BreakableContext.(Int, T) -> Unit) {
    BreakableContext.loop {
        forEachIndexed { i, e ->
            iteration { body(i, e) }
        }
    }
}

inline fun repeatB(times: Int, body: BreakableContext.(Int) -> Unit) {
    BreakableContext.loop {
        repeat(times) {
            iteration { body(it) }
        }
    }
}
