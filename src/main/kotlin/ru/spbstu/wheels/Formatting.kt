package ru.spbstu.wheels

@DslMarker
annotation class FormattingDSL

inline class AppendScope(val appendable: Appendable) {
    inline fun indent(indent: Int = 4, body: IndentScope.() -> Unit) {
        IndentScope(indent).body()
    }
}

inline fun AppendScope.appendln(value: CharSequence) = appendable.appendln(value)

inline class IndentScope(val indent: Int = 4) {
    inline fun AppendScope.appendln(value: CharSequence) = appendable.append(" ".repeat(indent)).appendln(value)

    inline fun indent(indent: Int = 4, body: IndentScope.() -> Unit) {
        IndentScope(this.indent + indent).body()
    }
}
