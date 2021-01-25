package ru.spbstu.wheels

import kotlin.test.Test
import kotlin.test.assertEquals

class MutableRefTest {
    @Test
    fun smokeTest() {

        fun performStuff(sRef: MutableRef<String>, iRef: MutableRef<Int>) {
            var s by sRef
            var i by iRef

            i = i + s.length
            if(s.isEmpty()) s = "Hello world"
        }

        val sRef = ref("Hello")
        val iRef = ref(2)

        assertEquals("Hello", sRef.value)
        assertEquals(2, iRef.value)

        performStuff(sRef, iRef)
        assertEquals("Hello", sRef.value)
        assertEquals(7, iRef.value)

        sRef `=` ""

        performStuff(sRef, iRef)
        assertEquals("Hello world", sRef.value)
        assertEquals(7, iRef.value)

        iRef `=` 0
        performStuff(sRef, iRef)
        assertEquals("Hello world", sRef.value)
        assertEquals(11, iRef.value)
    }
}