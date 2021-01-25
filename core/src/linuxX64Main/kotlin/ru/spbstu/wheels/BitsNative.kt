package ru.spbstu.wheels

@PublishedApi
internal actual fun Int.reverseBits(): Int {
    var i = this
    // HD, Figure 7-1
    i = (i and 0x55555555) shl 1 or ((i ushr 1) and 0x55555555)
    i = (i and 0x33333333) shl 2 or ((i ushr 2) and 0x33333333)
    i = (i and 0x0f0f0f0f) shl 4 or ((i ushr 4) and 0x0f0f0f0f)
    i = (i shl 24) or ((i and 0xff00) shl 8) or
            ((i ushr 8) and 0xff00) or (i ushr 24)
    return i
}
