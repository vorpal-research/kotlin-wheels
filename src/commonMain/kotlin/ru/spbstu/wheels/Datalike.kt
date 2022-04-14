package ru.spbstu.wheels

import kotlinx.warnings.Warnings
import kotlin.reflect.KProperty1

interface DatalikeToString {
    abstract override fun toString(): String
}

interface DatalikeEquals {
    abstract override fun hashCode(): Int
    abstract override fun equals(other: Any?): Boolean
}

interface Datalike : DatalikeToString, DatalikeEquals

@PublishedApi
@Suppress(Warnings.NOTHING_TO_INLINE)
internal inline fun <T: DatalikeToString, U> T.kvToString(prop: KProperty1<T, U>) =
        prop.name + "=" + prop(this)

inline fun <reified T: DatalikeToString> T.toRecordString(): String =
        T::class.simpleName ?: "Anonymous"
inline fun <reified T: DatalikeToString> T.toRecordString(
        prop1: KProperty1<T, *>,
        prefix: String = "(",
        postfix: String = ")",
        @Suppress(Warnings.UNUSED_PARAMETER)
        separator: String = ", "
): String =
        toRecordString() +
                prefix +
                kvToString(prop1) +
                postfix

inline fun <reified T: DatalikeToString> T.toRecordString(
        prop1: KProperty1<T, *>,
        prop2: KProperty1<T, *>,
        prefix: String = "(",
        postfix: String = ")",
        separator: String = ", "
): String =
        toRecordString() +
                prefix +
                kvToString(prop1) + separator +
                kvToString(prop2) +
                postfix

inline fun <reified T: DatalikeToString> T.toRecordString(
        prop1: KProperty1<T, *>,
        prop2: KProperty1<T, *>,
        prop3: KProperty1<T, *>,
        prefix: String = "(",
        postfix: String = ")",
        separator: String = ", "
): String =
        toRecordString() +
                prefix +
                kvToString(prop1) + separator +
                kvToString(prop2) + separator +
                kvToString(prop3) +
                postfix

inline fun <reified T: DatalikeToString> T.toRecordString(
        prop1: KProperty1<T, *>,
        prop2: KProperty1<T, *>,
        prop3: KProperty1<T, *>,
        prop4: KProperty1<T, *>,
        prefix: String = "(",
        postfix: String = ")",
        separator: String = ", "
): String =
        toRecordString() +
                prefix +
                kvToString(prop1) + separator +
                kvToString(prop2) + separator +
                kvToString(prop3) + separator +
                kvToString(prop4) +
                postfix

inline fun <reified T: DatalikeToString> T.toRecordString(
        prop1: KProperty1<T, *>,
        prop2: KProperty1<T, *>,
        prop3: KProperty1<T, *>,
        prop4: KProperty1<T, *>,
        prop5: KProperty1<T, *>,
        prefix: String = "(",
        postfix: String = ")",
        separator: String = ", "
): String =
        toRecordString() +
                prefix +
                kvToString(prop1) + separator +
                kvToString(prop2) + separator +
                kvToString(prop3) + separator +
                kvToString(prop4) + separator +
                kvToString(prop5) +
                postfix

inline fun <reified T: DatalikeToString> T.toRecordString(
        vararg props: KProperty1<T, *>,
        prefix: String = "(",
        postfix: String = ")",
        separator: String = ", "
): String =
        toRecordString() + props.joinToString(prefix = prefix, postfix = postfix, separator = separator) { kvToString(it) }

inline fun <reified T: DatalikeToString> T.toTupleString(
        prop1: (T) -> Any?,
        prefix: String = "(",
        postfix: String = ")",
        @Suppress(Warnings.UNUSED_PARAMETER)
        separator: String = ", "
): String =
        prefix +
                prop1(this) +
                postfix

inline fun <reified T: DatalikeToString> T.toTupleString(
        prop1: (T) -> Any?,
        prop2: (T) -> Any?,
        prefix: String = "(",
        postfix: String = ")",
        separator: String = ", "
): String =
        prefix +
                prop1(this) + separator +
                prop2(this) +
                postfix

inline fun <reified T: DatalikeToString> T.toTupleString(
        prop1: (T) -> Any?,
        prop2: (T) -> Any?,
        prop3: (T) -> Any?,
        prefix: String = "(",
        postfix: String = ")",
        separator: String = ", "
): String =
        prefix +
                prop1(this) + separator +
                prop2(this) + separator +
                prop3(this) +
                postfix

inline fun <reified T: DatalikeToString> T.toTupleString(
        prop1: (T) -> Any?,
        prop2: (T) -> Any?,
        prop3: (T) -> Any?,
        prop4: (T) -> Any?,
        prefix: String = "(",
        postfix: String = ")",
        separator: String = ", "
): String =
        prefix +
                prop1(this) + separator +
                prop2(this) + separator +
                prop3(this) + separator +
                prop4(this) +
                postfix

inline fun <reified T: DatalikeToString> T.toTupleString(
        prop1: (T) -> Any?,
        prop2: (T) -> Any?,
        prop3: (T) -> Any?,
        prop4: (T) -> Any?,
        prop5: (T) -> Any?,
        prefix: String = "(",
        postfix: String = ")",
        separator: String = ", "
): String =
        prefix +
                prop1(this) + separator +
                prop2(this) + separator +
                prop3(this) + separator +
                prop4(this) + separator +
                prop5(this) +
                postfix

inline fun <reified T: DatalikeToString> T.toTupleString(
        vararg props: (T) -> Any?,
        prefix: String = "(",
        postfix: String = ")",
        separator: String = ", "
): String =
        props.joinToString(prefix = prefix, postfix = postfix, separator = separator) { it(this).toString() }

inline fun <reified T: DatalikeEquals> T.defaultEquals(
        that: Any?,
        prop1: (T) -> Any?
) =
        that is T &&
                prop1(this) == prop1(that)

inline fun <reified T: DatalikeEquals> T.defaultEquals(
        that: Any?,
        prop1: (T) -> Any?,
        prop2: (T) -> Any?
): Boolean =
        that is T &&
                prop1(this) == prop1(that) &&
                prop2(this) == prop2(that)

inline fun <reified T: DatalikeEquals> T.defaultEquals(
        that: Any?,
        prop1: (T) -> Any?,
        prop2: (T) -> Any?,
        prop3: (T) -> Any?
): Boolean =
        that is T &&
                prop1(this) == prop1(that) &&
                prop2(this) == prop2(that) &&
                prop3(this) == prop3(that)

inline fun <reified T: DatalikeEquals> T.defaultEquals(
        that: Any?,
        prop1: (T) -> Any?,
        prop2: (T) -> Any?,
        prop3: (T) -> Any?,
        prop4: (T) -> Any?
): Boolean =
        that is T &&
                prop1(this) == prop1(that) &&
                prop2(this) == prop2(that) &&
                prop3(this) == prop3(that) &&
                prop4(this) == prop4(that)

inline fun <reified T: DatalikeEquals> T.defaultEquals(
        that: Any?,
        prop1: (T) -> Any?,
        prop2: (T) -> Any?,
        prop3: (T) -> Any?,
        prop4: (T) -> Any?,
        prop5: (T) -> Any?
): Boolean =
        that is T &&
                prop1(this) == prop1(that) &&
                prop2(this) == prop2(that) &&
                prop3(this) == prop3(that) &&
                prop4(this) == prop4(that) &&
                prop5(this) == prop5(that)

inline fun <reified T: DatalikeEquals> T.defaultEquals(that: Any?, vararg props: (T) -> Any?): Boolean =
        that is T && props.all { it(this) == it(that) }

inline fun <reified T: DatalikeEquals> T.defaultHashCode(
        prop1: (T) -> Any?
): Int =
        prop1(this).hashCode()

inline fun <reified T: DatalikeEquals> T.defaultHashCode(
        prop1: (T) -> Any?,
        prop2: (T) -> Any?
): Int = hashCombine(prop1(this), prop2(this))

inline fun <reified T: DatalikeEquals> T.defaultHashCode(
        prop1: (T) -> Any?,
        prop2: (T) -> Any?,
        prop3: (T) -> Any?
): Int = hashCombine(prop1(this), prop2(this), prop3(this))

inline fun <reified T> T.defaultHashCode(
        prop1: (T) -> Any?,
        prop2: (T) -> Any?,
        prop3: (T) -> Any?,
        prop4: (T) -> Any?
): Int = hashCombine(prop1(this), prop2(this), prop3(this), prop4(this))

inline fun <reified T: DatalikeEquals> T.defaultHashCode(
        prop1: (T) -> Any?,
        prop2: (T) -> Any?,
        prop3: (T) -> Any?,
        prop4: (T) -> Any?,
        prop5: (T) -> Any?
): Int = hashCombine(prop1(this), prop2(this), prop3(this), prop4(this), prop5(this))

inline fun <reified T: DatalikeEquals> T.defaultHashCode(vararg props: (T) -> Any?): Int {
    if (props.isEmpty()) return 0
    var result = props[0](this).hashCode()
    for(i in 1..props.lastIndex) result = 31 * result + props[i](this).hashCode()
    return result
}

inline fun <reified T: DatalikeEquals> T.defaultHashCode(
        prop1: () -> Any?
): Int =
        prop1().hashCode()

inline fun <reified T: DatalikeEquals> T.defaultHashCode(
        prop1: () -> Any?,
        prop2: () -> Any?
): Int = hashCombine(prop1(), prop2())

inline fun <reified T: DatalikeEquals> T.defaultHashCode(
        prop1: () -> Any?,
        prop2: () -> Any?,
        prop3: () -> Any?
): Int = hashCombine(prop1(), prop2(), prop3())

inline fun <reified T: DatalikeEquals> T.defaultHashCode(
        prop1: () -> Any?,
        prop2: () -> Any?,
        prop3: () -> Any?,
        prop4: () -> Any?
): Int = hashCombine(prop1(), prop2(), prop3(), prop4())

inline fun <reified T: DatalikeEquals> T.defaultHashCode(
        prop1: () -> Any?,
        prop2: () -> Any?,
        prop3: () -> Any?,
        prop4: () -> Any?,
        prop5: () -> Any?
): Int = hashCombine(prop1(), prop2(), prop3(), prop4(), prop5())

inline fun <reified T: DatalikeEquals> T.defaultHashCode(vararg props: () -> Any?): Int {
    if (props.isEmpty()) return 0
    var result = props[0]().hashCode()
    for(i in 1..props.lastIndex) result = 31 * result + props[i]().hashCode()
    return result
}

inline fun <reified T: Comparable<T>, A1 : Comparable<A1>> T.defaultCompareTo(that: T, prop1: (T) -> A1): Int {
    return prop1(this).compareTo(prop1(that))
}

inline fun <reified T: Comparable<T>,
        A1 : Comparable<A1>,
        A2 : Comparable<A2>
        > T.defaultCompareTo(
        that: T,
        prop1: (T) -> A1,
        prop2: (T) -> A2
): Int {
    val r1 = prop1(this).compareTo(prop1(that))
    if (r1 != 0) return r1
    return prop2(this).compareTo(prop2(that))
}

inline fun <reified T: Comparable<T>,
        A1 : Comparable<A1>,
        A2 : Comparable<A2>,
        A3 : Comparable<A3>
        > T.defaultCompareTo(
        that: T,
        prop1: (T) -> A1,
        prop2: (T) -> A2,
        prop3: (T) -> A3
): Int {
    val r1 = prop1(this).compareTo(prop1(that))
    if (r1 != 0) return r1
    val r2 = prop2(this).compareTo(prop2(that))
    if (r2 != 0) return r2
    return prop3(this).compareTo(prop3(that))
}

inline fun <reified T: Comparable<T>,
        A1 : Comparable<A1>,
        A2 : Comparable<A2>,
        A3 : Comparable<A3>,
        A4 : Comparable<A4>
        > T.defaultCompareTo(
        that: T,
        prop1: (T) -> A1,
        prop2: (T) -> A2,
        prop3: (T) -> A3,
        prop4: (T) -> A4
): Int {
    val r1 = prop1(this).compareTo(prop1(that))
    if (r1 != 0) return r1
    val r2 = prop2(this).compareTo(prop2(that))
    if (r2 != 0) return r2
    val r3 = prop3(this).compareTo(prop3(that))
    if (r3 != 0) return r3
    return prop4(this).compareTo(prop4(that))
}

inline fun <reified T: Comparable<T>,
        A1 : Comparable<A1>,
        A2 : Comparable<A2>,
        A3 : Comparable<A3>,
        A4 : Comparable<A4>,
        A5 : Comparable<A5>
        > T.defaultCompareTo(
        that: T,
        prop1: (T) -> A1,
        prop2: (T) -> A2,
        prop3: (T) -> A3,
        prop4: (T) -> A4,
        prop5: (T) -> A5
): Int {
    val r1 = prop1(this).compareTo(prop1(that))
    if (r1 != 0) return r1
    val r2 = prop2(this).compareTo(prop2(that))
    if (r2 != 0) return r2
    val r3 = prop3(this).compareTo(prop3(that))
    if (r3 != 0) return r3
    val r4 = prop4(this).compareTo(prop4(that))
    if (r4 != 0) return r4
    return prop5(this).compareTo(prop5(that))
}

inline fun <reified T: Comparable<T>> T.defaultCompareTo(that: T, vararg props: (T) -> Comparable<*>): Int {
    for (prop in props) {
        @Suppress(Warnings.UNCHECKED_CAST)
        val prop0 = prop as (T) -> Comparable<Any?>
        val res = prop0(this).compareTo(prop0(that))
        if (res != 0) return res
    }
    return 0
}

interface Copyable<T : Copyable<T>> {
    fun copy(): T
}

inline fun <T: Copyable<T>, A1> T.defaultCopy(
        constructor: (A1) -> T,
        prop1: (T) -> A1
): T = constructor(prop1(this))

inline fun <T: Copyable<T>, A1, A2> T.defaultCopy(
        constructor: (A1, A2) -> T,
        prop1: (T) -> A1,
        prop2: (T) -> A2
): T = constructor(prop1(this), prop2(this))

inline fun <T: Copyable<T>, A1, A2, A3> T.defaultCopy(
        constructor: (A1, A2, A3) -> T,
        prop1: (T) -> A1,
        prop2: (T) -> A2,
        prop3: (T) -> A3
): T = constructor(prop1(this), prop2(this), prop3(this))

inline fun <T: Copyable<T>, A1, A2, A3, A4> T.defaultCopy(
        constructor: (A1, A2, A3, A4) -> T,
        prop1: (T) -> A1,
        prop2: (T) -> A2,
        prop3: (T) -> A3,
        prop4: (T) -> A4
): T = constructor(prop1(this), prop2(this), prop3(this), prop4(this))

inline fun <T: Copyable<T>, A1, A2, A3, A4, A5> T.defaultCopy(
        constructor: (A1, A2, A3, A4, A5) -> T,
        prop1: (T) -> A1,
        prop2: (T) -> A2,
        prop3: (T) -> A3,
        prop4: (T) -> A4,
        prop5: (T) -> A5
): T = constructor(prop1(this), prop2(this), prop3(this), prop4(this), prop5(this))


inline fun <T: Copyable<T>, A1> T.defaultCopy(
        constructor: (A1) -> T,
        prop1: () -> A1
): T = constructor(prop1())

inline fun <T: Copyable<T>, A1, A2> T.defaultCopy(
        constructor: (A1, A2) -> T,
        prop1: () -> A1,
        prop2: () -> A2
): T = constructor(prop1(), prop2())

inline fun <T: Copyable<T>, A1, A2, A3> T.defaultCopy(
        constructor: (A1, A2, A3) -> T,
        prop1: () -> A1,
        prop2: () -> A2,
        prop3: () -> A3
): T = constructor(prop1(), prop2(), prop3())

inline fun <T: Copyable<T>, A1, A2, A3, A4> T.defaultCopy(
        constructor: (A1, A2, A3, A4) -> T,
        prop1: () -> A1,
        prop2: () -> A2,
        prop3: () -> A3,
        prop4: () -> A4
): T = constructor(prop1(), prop2(), prop3(), prop4())

inline fun <T: Copyable<T>, A1, A2, A3, A4, A5> T.defaultCopy(
        constructor: (A1, A2, A3, A4, A5) -> T,
        prop1: () -> A1,
        prop2: () -> A2,
        prop3: () -> A3,
        prop4: () -> A4,
        prop5: () -> A5
): T = constructor(prop1(), prop2(), prop3(), prop4(), prop5())

