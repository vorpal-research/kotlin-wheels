package ru.spbstu.wheels

import kotlin.random.Random

fun Random.ints(): Sequence<Int> = generateSequence { nextInt() }
fun Random.doubles(): Sequence<Double> = generateSequence { nextDouble() }
fun Random.longs(): Sequence<Long> = generateSequence { nextLong() }

fun Random.ints(size: Int): Sequence<Int> = ints().take(size)
fun Random.doubles(size: Int): Sequence<Double> = doubles().take(size)
fun Random.longs(size: Int): Sequence<Long> = longs().take(size)

fun Random.ints(from: Int, until: Int): Sequence<Int> = generateSequence { nextInt(from, until) }
fun Random.doubles(from: Double, until: Double): Sequence<Double> = generateSequence { nextDouble(from, until) }
fun Random.longs(from: Long, until: Long): Sequence<Long> = generateSequence { nextLong(from, until) }

fun Random.ints(size: Int, from: Int, until: Int): Sequence<Int> = ints(from, until).take(size)
fun Random.doubles(size: Int, from: Double, until: Double): Sequence<Double> = doubles(from, until).take(size)
fun Random.longs(size: Int, from: Long, until: Long): Sequence<Long> = longs(from, until).take(size)
