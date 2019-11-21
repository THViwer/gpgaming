package com.onepiece.treasure.core

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.DefaultClientToken
import com.onepiece.treasure.core.service.PlatformBindService
import com.onepiece.treasure.utils.StringUtil
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class OrderIdBuilder(
        private val platformBindService: PlatformBindService
) {

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    private val dateTimeForma2 = DateTimeFormatter.ofPattern("HHmmss")
    private fun getCurrentTime(format: DateTimeFormatter = dateTimeFormat) = LocalDateTime.now().format(format)

    fun generatorDepositOrderId(): String {
        return "BD${getCurrentTime()}${StringUtil.generateNumNonce(5)}"
    }

    fun generatorWithdrawOrderId(): String {
        return "BW${getCurrentTime()}${StringUtil.generateNumNonce(5)}"
    }

    fun generatorTransferOrderId(clientId: Int, platform: Platform): String {

        return when (platform) {
            Platform.Sbo -> {
                val clientToken = platformBindService.find(clientId = clientId, platform = platform).clientToken as DefaultClientToken
                "${clientToken.appId}-${UUID.randomUUID().toString().replace("-", "").substring(0, 6)}"
            }
            Platform.AllBet -> "T${platform.name.substring(0, 1)}${getCurrentTime(dateTimeForma2)}${StringUtil.generateNumNonce(5)}"
            else -> "T${platform.name.substring(0, 1)}${getCurrentTime()}${StringUtil.generateNumNonce(5)}"
        }

    }

    fun generatorArtificialOrderId(): String {
        return UUID.randomUUID().toString()
    }




}