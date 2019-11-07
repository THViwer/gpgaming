package com.onepiece.treasure.utils

object StringUtil {

    fun generatePassword(): String {

        return "${generateNonce(3)}${generateNumNonce(3)}".capitalize()
    }

    fun generateNonce(size: Int): String {
        val nonceScope = "abcdefghijklmnopqrstuvwxyz"
        val scopeSize = nonceScope.length
        val nonceItem: (Int) -> Char = { nonceScope[(scopeSize * Math.random()).toInt()] }
        return Array(size, nonceItem).joinToString("")
    }

    fun generateNumNonce(size: Int): String {
        val nonceScope = "1234567890"
        val scopeSize = nonceScope.length
        val nonceItem: (Int) -> Char = { nonceScope[(scopeSize * Math.random()).toInt()] }
        return Array(size, nonceItem).joinToString("")
    }

}