package com.onepiece.gpgaming.games.bet

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.utils.PlatformUsernameUtil
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class BetOrderUtil private constructor(
        private val mapUtil: MapUtil,
        private val platform: Platform
) {

    var clientId: Int = 0
    var memberId: Int = 0
    var orderId: String = ""
    var betAmount: BigDecimal = BigDecimal.ZERO
    var validAmount: BigDecimal = BigDecimal.ZERO
    var payout: BigDecimal = BigDecimal.ZERO
    var betTime: LocalDateTime = LocalDateTime.now()
    var settleTime: LocalDateTime = LocalDateTime.now()


    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    companion object {
        fun instance(platform: Platform, mapUtil: MapUtil): BetOrderUtil {
            return BetOrderUtil(platform = platform, mapUtil = mapUtil)
        }
    }

    fun setUsername(key: String): BetOrderUtil {
        val username  = mapUtil.asString(key)
        val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = platform, platformUsername = username)
        this.clientId = clientId
        this.memberId = memberId
        return this
    }

    fun setOrderId(key: String): BetOrderUtil {
        orderId = mapUtil.asString(key)
        return this
    }

    fun setBetAmount(key: String): BetOrderUtil {
        betAmount = mapUtil.asBigDecimal(key)
        return this
    }

    fun setWinAmount(key: String): BetOrderUtil {
        payout = mapUtil.asBigDecimal(key)
        return this
    }

    fun setBetTime(key: String, dateTimeFormatter: DateTimeFormatter = dateTimeFormat): BetOrderUtil {
        betTime = mapUtil.asLocalDateTime(key, dateTimeFormatter)
        return this
    }

    fun setBetTimeByCmdLong(key: String): BetOrderUtil {
        val time = mapUtil.asLong(key)
        betTime = LocalDateTime.ofInstant(Instant.ofEpochMilli((time-621355968000000000)/10000), ZoneId.of("Asia/Shanghai")).minusHours(8)
        return this
    }

    fun setSettleTime(key: String, dateTimeFormatter: DateTimeFormatter = dateTimeFormat): BetOrderUtil {
        settleTime = mapUtil.asLocalDateTime(key, dateTimeFormatter)
        return this
    }

    fun setSettleTimeByCmdLong(key: String): BetOrderUtil {
        val time = mapUtil.asLong(key)
        settleTime = LocalDateTime.ofInstant(Instant.ofEpochMilli((time-621355968000000000)/10000), ZoneId.of("Asia/Shanghai")).minusHours(8)
        return this
    }

    fun set(name: String, key: String, dateTimeFormatter: DateTimeFormatter = dateTimeFormat): BetOrderUtil {
        when (name) {
            "username" -> {
                val username = mapUtil.asString(key)
                val pair = PlatformUsernameUtil.prefixPlatformUsername(platform = platform, platformUsername = username)
                clientId = pair.first
                memberId = pair.second
            }
            "orderId" -> orderId = mapUtil.asString(key)
            "betAmount" -> betAmount = mapUtil.asBigDecimal(key)
            "validAmount" -> validAmount = mapUtil.asBigDecimal(key)
            "payout" -> payout = mapUtil.asBigDecimal(key)
            "betTime" -> betTime = mapUtil.asLocalDateTime(key, dateTimeFormatter)
            "settleTime" -> settleTime = mapUtil.asLocalDateTime(key, dateTimeFormatter)
        }

        return this
    }

    fun build(objectMapper: ObjectMapper): BetOrderValue.BetOrderCo {
        check(clientId > 0 && memberId > 0 && betAmount != BigDecimal.ZERO &&orderId != "") { OnePieceExceptionCode.PLATFORM_DATA_FAIL }

        val originData = objectMapper.writeValueAsString(mapUtil.data)
        return BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, platform = platform,  orderId = orderId, betAmount = betAmount,
                payout = payout, betTime = betTime, settleTime = settleTime, originData = originData, validAmount = validAmount)
    }





}