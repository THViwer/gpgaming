package com.onepiece.treasure.core

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode

object PlatformUsernameUtil  {

    fun generatorPlatformUsername(clientId: Int, memberId: Int, platform: Platform): String {

        return when (platform) {
            Platform.Joker, Platform.CT, Platform.DG, Platform.Evolution, Platform.Lbc, Platform.Sbo, Platform.SexyGaming -> {
                "${autoCompletion(clientId, 2)}${autoCompletion(memberId, 6)}"
            }
            Platform.Kiss918, Platform.Pussy888, Platform.Mega  -> ""
            Platform.GoldDeluxe -> "A${autoCompletion(clientId, 2)}${autoCompletion(memberId, 6)}"
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    fun prefixPlatformUsername(platform: Platform, platformUsername: String): Pair<Int, Int> {

        return when (platform) {
            Platform.Joker, Platform.CT, Platform.DG, Platform.Evolution, Platform.Lbc -> {
                val clientId = platformUsername.substring(0, 2).toInt()
                val memberId = platformUsername.substring(2, platformUsername.length).toInt()

                clientId to memberId
            }
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }

    }

    private fun autoCompletion(number: Int, size: Int): String {
        val completion = size - "$number".length
        if (completion <= 0) return "$number"

        return (0 until completion).joinToString(separator = "") { "0" }.plus(number)
    }

}