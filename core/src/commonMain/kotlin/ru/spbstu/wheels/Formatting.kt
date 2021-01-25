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
