package ru.spbstu.wheels

import kotlinx.warnings.Warnings

@DslMarker
annotation class FormattingDSL

fun <T> Appendable.appendLine(value: T?): Appendable =
        this.append("$value").appendLine()

inline class AppendScope(val appendable: Appendable) {
    inline fun indent(indent: Int = 4, body: IndentScope.() -> Unit) {
        IndentScope(indent).body()
    }
}

@Suppress(Warnings.NOTHING_TO_INLINE)
inline fun AppendScope.appendLine(value: CharSequence): Appendable =
        appendable.appendLine(value)

inline class IndentScope(val indent: Int = 4) {
    @Suppress(Warnings.NOTHING_TO_INLINE)
    inline fun AppendScope.appendLine(value: CharSequence) =
            appendable.append(" ".repeat(indent)).appendLine(value)

    inline fun indent(indent: Int = 4, body: IndentScope.() -> Unit) {
        IndentScope(this.indent + indent).body()
    }
}

@PublishedApi
internal fun <A: Appendable, T> A.appendElement(e: T) {
    when(e) {
        is CharSequence -> append(e)
        is Char -> append(e)
        else -> append(e.toString())
    }
}

inline fun <A: Appendable, T> A.appendJoined(
    iterator: Iterator<T>,
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    body: A.(T) -> Unit = { appendElement(it) }): A {
    append(prefix)
    var count = 0
    for (element in iterator) {
        if (++count > 1) append(separator)
        if (limit < 0 || count <= limit) {
            body(element)
        } else break
    }
    if (limit >= 0 && count > limit) append(truncated)
    append(postfix)
    return this
}

inline fun <A: Appendable, T> A.appendJoined(
    iterable: Iterable<T>,
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    body: A.(T) -> Unit = { appendElement(it) }
): A = appendJoined(iterable.iterator(), separator, prefix, postfix, limit, truncated, body)

inline fun <A: Appendable, T> A.appendJoined(
    sequence: Sequence<T>,
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    body: A.(T) -> Unit =  { appendElement(it) }
): A = appendJoined(sequence.iterator(), separator, prefix, postfix, limit, truncated, body)

inline fun <A: Appendable, K, V> A.appendJoined(
    map: Map<K, V>,
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    body: A.(K, V) -> Unit
): A = appendJoined(map.iterator(), separator, prefix, postfix, limit, truncated) { body(it.key, it.value) }

@Suppress(Warnings.NOTHING_TO_INLINE)
inline fun <A: Appendable, K, V> A.appendJoined(
    map: Map<K, V>,
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "..."
): A = appendJoined(map.iterator(), separator, prefix, postfix, limit, truncated) { appendElement(it) }

