package com.onepiece.treasure.core

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.utils.StringUtil

object PlatformUsernameUtil  {

    fun generatorPlatformUsername(clientId: Int, memberId: Int, platform: Platform): Pair<String, String> {

        val username =  when (platform) {
//            Platform.Joker, Platform.Evolution, Platform.Lbc, Platform.Sbo, Platform.SexyGaming,
//            Platform.Fgg, Platform.Bcs, Plat -> {
//                "${autoCompletion(clientId, 2)}${autoCompletion(memberId, 6)}"
//            }
            Platform.Kiss918, Platform.Pussy888, Platform.Mega  -> ""
            Platform.GoldDeluxe -> "A${autoCompletion(clientId, 2)}${autoCompletion(memberId, 6)}"
            else -> "${autoCompletion(clientId, 2)}${autoCompletion(memberId, 6)}${StringUtil.generateNonce(2)}"
        }

        val password = when (platform) {
            // slot
            Platform.Joker,
            Platform.Kiss918,
            Platform.MicroGaming,
            Platform.Pussy888,
            Platform.PlaytechSlot,

                // live
            Platform.AllBet,
            Platform.DreamGaming -> StringUtil.generatePassword()
            else -> "-"
        }

        return username to password
    }

    fun prefixPlatformUsername(platform: Platform, platformUsername: String): Pair<Int, Int> {

        return when (platform) {

            Platform.GoldDeluxe -> {
                val clientId = platformUsername.substring(1, 3).toInt()
                val memberId = platformUsername.substring(3, platformUsername.length).toInt()

                clientId to memberId
            }
            else -> {
                val clientId = platformUsername.substring(0, 2).toInt()
                val memberId = platformUsername.substring(2, platformUsername.length-2).toInt()

                clientId to memberId
            }
        }

    }

    private fun autoCompletion(number: Int, size: Int): String {
        val completion = size - "$number".length
        if (completion <= 0) return "$number"

        return (0 until completion).joinToString(separator = "") { "0" }.plus(number)
    }

}