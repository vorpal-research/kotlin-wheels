package ru.spbstu.wheels.collections

import ru.spbstu.wheels.asList
import ru.spbstu.wheels.nextElementOf
import ru.spbstu.wheels.times
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AdaptersTest {

    val alphabet = (('a'..'z') + ('A'..'Z') + ('0' .. '9'))

    @Test
    fun charSequenceAsList() {
        val random = Random(52)
        val string = StringBuilder()
        assertEquals(string.toList(), string.asList())
        assertEquals(string.asList(), string.toList())

        54 times {
            string.append(random.nextElementOf(alphabet))
        }

        assertEquals(string.toList(), string.asList())
        assertEquals(string.asList(), string.toList())

        val imm = string as CharSequence
        assertEquals(imm.toList(), imm.asList())
        assertEquals(imm.asList(), imm.toList())

        val ss = imm.toString()
        assertEquals(ss.toList(), ss.asList())
        assertEquals(ss.asList(), ss.toList())

        val sToList: MutableList<Char> = string.asList()

        54 times {
            val char = random.nextElementOf(alphabet)
            sToList += char
            assertEquals(char, sToList.last())
            assertEquals(char, string.last())
        }
        assertEquals(sToList, string.toList())

        sToList.add(25, '$')
        assertEquals('$', sToList[25])
        assertEquals('$', string[25])

        sToList.subList(0, 14).add('#')
        assertEquals('#', sToList[14])
        assertEquals('#', string[14])

    }

    @Test
    fun listAsCharSequence() {
        val cs = alphabet.asCharSequence()
        assertEquals(alphabet.joinToString(""), cs.toString())
        val mr = Regex("[a-zA-Z]*").find(cs)
        assertNotNull(mr)
        assertEquals(('a'..'z').joinToString("") +
                ('A'..'Z').joinToString(""), mr.value)
    }

}