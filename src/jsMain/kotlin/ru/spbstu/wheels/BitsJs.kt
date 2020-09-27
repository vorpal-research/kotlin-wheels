package ru.spbstu.wheels

private val reverseJs = js("""
    function(bits) {
        var x = new Uint32Array(1); x[0]=bits;
        x[0] = ((x[0] & 0x0000ffff) << 16) | ((x[0] & 0xffff0000) >>> 16);
        x[0] = ((x[0] & 0x55555555) << 1) | ((x[0] & 0xAAAAAAAA) >>> 1);
        x[0] = ((x[0] & 0x33333333) << 2) | ((x[0] & 0xCCCCCCCC) >>> 2);
        x[0] = ((x[0] & 0x0F0F0F0F) << 4) | ((x[0] & 0xF0F0F0F0) >>> 4);
        x[0] = ((x[0] & 0x00FF00FF) << 8) | ((x[0] & 0xFF00FF00) >>> 8);
        return x[0];
    }
    """)

@PublishedApi
internal actual fun Int.reverseBits(): Int = reverseJs(this) as Int
