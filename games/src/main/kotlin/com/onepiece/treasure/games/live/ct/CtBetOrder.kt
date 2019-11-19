package com.onepiece.treasure.games.live.ct

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.core.PlatformUsernameUtil
import com.onepiece.treasure.games.bet.CenterBetOrder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

data class CtBetOrder(

        val codeId: Int,

        val token: String,

        val random: String,

        val list: List<Data> = arrayListOf()

): CenterBetOrder {

    data class Data(
            @JsonIgnore
            @JsonAnySetter
            val map: Map<String, Any> = hashMapOf()
    )

    override fun getBetOrders(objectMapper: ObjectMapper): List<BetOrderValue.BetOrderCo> {

        if (list.isEmpty()) return emptyList()

        // 过滤已结算的订单
        return list.filter { it.map["userName"] != "A0011" } .filter { it.map["isRevocation"] == 1 }.map { data ->
            val map = data.map

            val username = map["userName"]?.toString()?: error("无法获得用户名")
            val orderId = map["id"]?.toString()?: error("无法获得订单Id")
            val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.CT, platformUsername = username)
            val betTime = map["betTime"]?.toString()?.let { LocalDateTime.parse(it, dateTimeFormatter) } ?: error("无法获得下注时间")
            val calTime = map["calTime"]?.toString()?.let { LocalDateTime.parse(it, dateTimeFormatter) }?: error("无法获得结算时间")
            val betPoints = map["betPoints"]?.toString()?.toBigDecimal()?: error("无法获得下注金额")
            val winOrLoss = map["winOrLoss"]?.toString()?.toBigDecimal()?: error("无法获得盈利金额")

            val originData = objectMapper.writeValueAsString(data.map)

            BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, orderId = orderId, betAmount = betPoints, winAmount = winOrLoss,
                    betTime = betTime, settleTime = calTime, platform = Platform.CT, originData = originData)
        }
    }
}