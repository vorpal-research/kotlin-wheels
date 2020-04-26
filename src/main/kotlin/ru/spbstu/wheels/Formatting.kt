package ru.spbstu.wheels

import kotlinx.warnings.Warnings

@DslMarker
annotation class FormattingDSL

inline class AppendScope(val appendable: Appendable) {
    inline fun indent(indent: Int = 4, body: IndentScope.() -> Unit) {
        IndentScope(indent).body()
    }
}

@Suppress(Warnings.NOTHING_TO_INLINE)
inline fun AppendScope.appendln(value: CharSequence) = appendable.appendln(value)

inline class IndentScope(val indent: Int = 4) {
    @Suppress(Warnings.NOTHING_TO_INLINE)
    inline fun AppendScope.appendln(value: CharSequence) = appendable.append(" ".repeat(indent)).appendln(value)

    inline fun indent(indent: Int = 4, body: IndentScope.() -> Unit) {
        IndentScope(this.indent + indent).body()
    }
}
