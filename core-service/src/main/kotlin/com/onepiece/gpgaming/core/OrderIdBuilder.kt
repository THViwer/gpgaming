package com.onepiece.gpgaming.core

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.token.ClientToken
import com.onepiece.gpgaming.beans.model.token.TTGClientToken
import com.onepiece.gpgaming.core.service.PlatformBindService
import com.onepiece.gpgaming.utils.StringUtil
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class OrderIdBuilder(
        private val platformBindService: PlatformBindService
) {

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
    private val dateTimeFormat2 = DateTimeFormatter.ofPattern("HHmmss")
    private val dateTimeFormat3 = DateTimeFormatter.ofPattern("yyMMddHHmmss")
    private val dateTimeFormat4 = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    private fun getCurrentTime(format: DateTimeFormatter = dateTimeFormat) = LocalDateTime.now().format(format)

    fun generatorDepositOrderId(): String {
        return "BD${getCurrentTime()}${StringUtil.generateNumNonce(5)}"
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
            Platform.AllBet -> "T${platform.name.substring(0, 1)}${getCurrentTime(dateTimeFormat2)}${StringUtil.generateNumNonce(5)}"

            Platform.TTG -> {
                val clientToken = platformBindService.find(clientId = clientId, platform = platform).clientToken as TTGClientToken
                "${clientToken.agentName}_${getCurrentTime(dateTimeFormat2)}${StringUtil.generateNumNonce(2)}"
            }

            else -> "T${platform.name.substring(0, 1)}${getCurrentTime()}${StringUtil.generateNumNonce(2)}"
        }

    }

    fun generatorArtificialOrderId(): String {
        return "AB${getCurrentTime()}${StringUtil.generateNumNonce(2)}"
    }




}