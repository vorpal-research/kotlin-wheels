package ru.spbstu.wheels

// == values.toSet().hashCode()
fun <T> setHashCode(values: Iterable<T>): Int = values.fold(0) { acc, c -> acc + c.hashCode() }

// == values.toList().hashCode()
fun <T> orderedHashCode(values: Iterable<T>): Int = values.fold(1) { acc, c -> 31 * acc + c.hashCode() }

fun <A, B> hashCombine(a: A, b: B): Int {
    var result = a?.hashCode() ?: 0
    result = 31 * result + (b?.hashCode() ?: 0)
    return result
}
fun <A, B, C> hashCombine(a: A, b: B, c: C): Int {
    var result = a?.hashCode() ?: 0
    result = 31 * result + (b?.hashCode() ?: 0)
    result = 31 * result + (c?.hashCode() ?: 0)
    return result
}
fun <A, B, C, D> hashCombine(a: A, b: B, c: C, d: D): Int {
    var result = a?.hashCode() ?: 0
    result = 31 * result + (b?.hashCode() ?: 0)
    result = 31 * result + (c?.hashCode() ?: 0)
    result = 31 * result + (d?.hashCode() ?: 0)
    return result
}
fun <T> hashCombine(vararg t: T): Int {
    var result = 1
    for(e in t) result = 31 * result + e.hashCode()
    return result
}
