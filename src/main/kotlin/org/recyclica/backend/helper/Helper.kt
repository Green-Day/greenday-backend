package org.recyclica.backend.helper

import java.util.*

private const val STRING_ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

fun Random.nextString(size: Int) = StringBuilder().apply {
    repeat(size) {
        append(STRING_ALPHABET[nextInt(STRING_ALPHABET.length)])
    }
}.toString()