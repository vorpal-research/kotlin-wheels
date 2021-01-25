package ru.spbstu.wheels

enum class Platform { JVM, JS, NATIVE }

expect val currentPlatform: Platform
