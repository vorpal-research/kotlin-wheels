package ru.spbstu.wheels

import kotlin.random.Random

fun Random.ints(): Sequence<Int> = sequence { yield(nextInt()) }
fun Random.doubles(): Sequence<Double> = sequence { yield(nextDouble()) }
fun Random.longs(): Sequence<Long> = sequence { yield(nextLong()) }

fun Random.ints(size: Int): Sequence<Int> = sequence { yield(nextInt()) }.take(size)
fun Random.doubles(size: Int): Sequence<Double> = sequence { yield(nextDouble()) }.take(size)
fun Random.longs(size: Int): Sequence<Long> = sequence { yield(nextLong()) }.take(size)

fun Random.ints(from: Int, until: Int): Sequence<Int> = sequence { yield(nextInt(from, until)) }
fun Random.doubles(from: Double, until: Double): Sequence<Double> = sequence { yield(nextDouble(from, until)) }
fun Random.longs(from: Long, until: Long): Sequence<Long> = sequence { yield(nextLong(from, until)) }

fun Random.ints(size: Int, from: Int, until: Int): Sequence<Int> =
        sequence { yield(nextInt(from, until)) }.take(size)
fun Random.doubles(size: Int, from: Double, until: Double): Sequence<Double> =
        sequence { yield(nextDouble(from, until)) }.take(size)
fun Random.longs(size: Int, from: Long, until: Long): Sequence<Long> =
        sequence { yield(nextLong(from, until)) }.take(size)
