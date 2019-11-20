package com.onepiece.treasure.games.bet

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.core.PlatformUsernameUtil
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class BetOrderUtil private constructor(
        private val data: Map<String, Any>,
        private val platform: Platform
) {

    var clientId: Int = 0
    var memberId: Int = 0
    var orderId: String = ""
    var betAmount: BigDecimal = BigDecimal.ZERO
    var winAmount: BigDecimal = BigDecimal.ZERO
    var betTime: LocalDateTime = LocalDateTime.now()
    var settleTime: LocalDateTime = LocalDateTime.now()

    companion object {
        fun instance(platform: Platform, data: Map<String, Any>): BetOrderUtil {
            return BetOrderUtil(platform = platform, data = data)
        }
    }

    fun set(name: String, key: String, dateTimeFormatter: DateTimeFormatter? = null): BetOrderUtil {

        val d = data[key]?.toString()?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
        when (name) {
            "username" -> {
                val pair = PlatformUsernameUtil.prefixPlatformUsername(platform = platform, platformUsername = d)
                clientId = pair.first
                memberId = pair.second
            }
            "orderId" -> {
                orderId = d
            }
            "betAmount" -> {
                betAmount = d.toBigDecimal()
            }
            "winAmount" -> winAmount = d.toBigDecimal()
            "betTime" -> betTime = LocalDateTime.parse(d, dateTimeFormatter)
            "settleTime" -> settleTime = LocalDateTime.parse(d, dateTimeFormatter)
        }

        return this
    }

    fun build(objectMapper: ObjectMapper): BetOrderValue.BetOrderCo {
        val originData = objectMapper.writeValueAsString(data)
        return BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, platform = platform,  orderId = orderId, betAmount = betAmount,
                winAmount = winAmount, betTime = betTime, settleTime = settleTime, originData = originData)
    }





}