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

@PublishedApi
internal data class PathCell<T>(
    val value: T,
    val len: Int,
    val prev: PathCell<T>?
) {
    constructor(value: T): this(value, 1, null)
    constructor(value: T, prev: PathCell<T>): this(
        value = value,
        prev = prev,
        len = prev.len + 1
    )

    fun toValueList(): List<T> = buildList(len) {
        var current: PathCell<T>? = this@PathCell
        while (current != null) {
            add(current.value)
            current = current.prev
        }
        reverse()
    }
}

inline fun <T> aStarSearch(from: T,
                           crossinline heur: (T) -> Double,
                           crossinline goal: (T) -> Boolean,
                           crossinline neighbours: (T) -> Sequence<T>): List<T>? {
    val closed = mutableSetOf<T>()
    val open: Heap<PathCell<T>> = heap(compareBy { (v, l, _) -> heur(v) + l })

    open += PathCell(from)

    closed += from

    while (!open.isEmpty()) {
        val peek = open.take()

        if (goal(peek.value)) return peek.toValueList()

        for (e in neighbours(peek.value)) if (e !in closed) {
            open += PathCell(e, peek)
            closed += e
        }
    }
    return null
}
