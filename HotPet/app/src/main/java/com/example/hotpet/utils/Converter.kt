package com.example.hotpet.utils

object Converter {
    private const val CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private fun convertChar(c: Char): Int {
        val ret = CHARS.indexOf(c)
        require(ret != -1) { "Invalid character encountered: $c" }
        return ret
    }

    fun convert(s: String): Long {
        require(s.length == 10) { "String length must be 10, was " + s.length }
        var ret: Long = 0
        for (element in s) {
            ret = (ret shl 6) + convertChar(element)
        }
        return ret
    }
}