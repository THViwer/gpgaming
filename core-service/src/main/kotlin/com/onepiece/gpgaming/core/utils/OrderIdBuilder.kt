package com.onepiece.gpgaming.core.utils

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.token.TTGClientToken
import com.onepiece.gpgaming.core.ActiveConfig
import com.onepiece.gpgaming.core.service.PlatformBindService
import com.onepiece.gpgaming.utils.RedisService
import com.onepiece.gpgaming.utils.StringUtil
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class OrderIdBuilder(
        private val platformBindService: PlatformBindService,
        private val redisService: RedisService,
        private val activeConfig: ActiveConfig
) {

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
    private val dateTimeFormat2 = DateTimeFormatter.ofPattern("HHmmss")
    private val dateTimeFormat3 = DateTimeFormatter.ofPattern("yyMMddHHmmss")
    private val dateTimeFormat4 = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    private fun getCurrentTime(format: DateTimeFormatter = dateTimeFormat) = LocalDateTime.now().format(format)

    fun generatorDepositOrderId(): String {

        val profile = when {
            activeConfig.profile == "prod" -> ""
            activeConfig.profile == "prods2" -> ""
            activeConfig.profile.contains("prod") -> ""
            else -> "T"
        }

        return "${profile}BD${getCurrentTime()}${StringUtil.generateNumNonce(5)}"
    }

    fun generatorWithdrawOrderId(): String {
        return "BW${getCurrentTime()}${StringUtil.generateNumNonce(5)}"
    }

    fun generatorTransferOrderId(clientId: Int, platform: Platform, transfer: String, platformUsername: String): String {

        return when (platform) {
            //TODO SBO 订单生成
//            Platform.Sbo -> {
//                val clientToken = platformBindService.find(clientId = clientId, platform = platform).clientToken as DefaultClientToken
//                "${clientToken.appId}-${UUID.randomUUID().toString().replace("-", "").substring(0, 6)}"
//            }
            Platform.Mega -> "${getCurrentTime()}${StringUtil.generateNumNonce(2)}"
            Platform.GoldDeluxe -> {
                val l = if (transfer == "out") "D" else "W"
                "$l${getCurrentTime(dateTimeFormat3)}${StringUtil.generateNonce(5)}"
            }
            Platform.SaGaming,
            Platform.SimplePlay -> {
                val type = if (transfer == "out") "IN" else "OUT"
                return "$type${getCurrentTime(dateTimeFormat4)}${platformUsername}"

            }
//            Platform.AllBet -> "T${platform.name.substring(0, 1)}${getCurrentTime(dateTimeFormat2)}${StringUtil.generateNumNonce(5)}"
            Platform.AllBet -> "${getCurrentTime(dateTimeFormat2)}${StringUtil.generateNumNonce(7)}"

            Platform.TTG -> {
                val clientToken = platformBindService.find(clientId = clientId, platform = platform).clientToken as TTGClientToken
                "${clientToken.agentName}_${getCurrentTime(dateTimeFormat)}${StringUtil.generateNumNonce(2)}"
            }

            else -> "T${platform.name.substring(0, 1)}${getCurrentTime()}${StringUtil.generateNumNonce(2)}"
        }

    }

    fun generatorArtificialOrderId(): String {
        return "AB${getCurrentTime()}${StringUtil.generateNumNonce(2)}"
    }

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("MMdd")

    fun generatorPayOrderId(clientId: Int): String {
        val redisKey = "payId:${LocalDate.now()}:$clientId"
        val id = redisService.increase(key = redisKey, timeout = 30 * 3600)

        val now = LocalDateTime.now()

        val autoId = when ("$id".length) {
            1 -> "0000$id"
            2 -> "000$id"
            3 -> "00$id"
            4 -> "0$id"
            else -> "$id"
        }

        val autoClientId = when {
            clientId < 10 -> "0$clientId"
            else -> "$clientId"
        }

        val profile = when {
            activeConfig.profile == "prod" -> ""
            activeConfig.profile == "prods2" -> ""
            activeConfig.profile.contains("prod") -> ""
            else -> "T"
        }

        return "${profile}P${autoClientId}${now.format(dateTimeFormatter)}$autoId"
    }


}