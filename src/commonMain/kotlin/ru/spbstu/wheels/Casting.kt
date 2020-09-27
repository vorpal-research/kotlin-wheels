package ru.spbstu.wheels

import kotlinx.warnings.Warnings

@Suppress(Warnings.UNCHECKED_CAST, Warnings.NOTHING_TO_INLINE)
inline fun <T> uncheckedCast(value: Any?): T = value as T

@Suppress(Warnings.UNCHECKED_CAST, Warnings.NOTHING_TO_INLINE)
inline fun <reified T> checkedCast(value: Any?): T? = value as? T
