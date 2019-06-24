package ru.spbstu.wheels

@PublishedApi
internal fun <T> reconstructPath(value: T, paths: MutableMap<T, T>): List<T> {
    val res = mutableListOf(value)
    var current = value
    while (true) {
        val interm = paths.getOption(current)
        if (interm.isNotEmpty()) {
            current = interm.get()
            res += current
        } else break
    }
    return res
}

inline fun <T> bestFSearch(from: T,
                           comparator: Comparator<T>,
                           goal: (T) -> Boolean,
                           neighbours: (T) -> Sequence<T>): List<T>? {
    val closed = mutableSetOf<T>()
    val open = heap(comparator)
    val paths: MutableMap<T, T> = mutableMapOf()

    open += from

    while (!open.isEmpty()) {
        val peek = open.take()
        if (goal(peek)) return reconstructPath(peek, paths)

        closed += peek
        for (e in neighbours(peek)) if (e !in closed) {
            paths[e] = peek
            open += e
        }
    }
    return null
}

inline fun <T : Comparable<T>> bestFSearch(from: T,
                                           goal: (T) -> Boolean,
                                           neighbours: (T) -> Sequence<T>): List<T>? =
        bestFSearch(from, naturalOrder(), goal, neighbours)


inline fun <T> aStarSearch(from: T,
                           crossinline heur: (T) -> Double,
                           crossinline goal: (T) -> Boolean,
                           crossinline neighbours: (T) -> Sequence<T>): List<T>? {
    val closed = mutableSetOf<T>()
    val open: Heap<Pair<T, Int>> = heap(compareBy { (v, l) -> heur(v) + l })
    val paths: MutableMap<T, T> = mutableMapOf()

    open += (from to 0)

    closed += from

    while (!open.isEmpty()) {
        val (peek, len) = open.take()

        if (goal(peek)) return reconstructPath(peek, paths)

        for (e in neighbours(peek)) if (e !in closed) {
            paths[e] = peek
            open += (e to (len + 1))
            closed += e
        }
    }
    return null
}
