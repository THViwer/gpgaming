package com.onepiece.treasure.games.sport.lbc

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.core.PlatformUsernameUtil
import com.onepiece.treasure.games.bet.CenterBetOrder
import java.time.LocalDateTime

data class LbcBetOrder(

        @JsonProperty("LastVersionKey")
        val lastVersionKey: Int,

        @JsonProperty("TotalRecord")
        val totalRecord: Int,

        @JsonProperty("Data")
        val data: List<Bet> = arrayListOf()

): CenterBetOrder {

    override fun getBetOrders(objectMapper: ObjectMapper): List<BetOrderValue.BetOrderCo> {

        if (this.data.isEmpty()) return emptyList()

        return data.filter { it.map["TicketStatus"] != "running" }.map {

            val orderId = it.map["TransId"]?.toString()?: error("无法获得订单数据")
            val username = it.map["PlayerName"]?.toString()?: error("无法获得用户名")
            val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.Lbc, platformUsername = username)

            val betTime = it.map["TransactionTime"]?.toString()?.let { LocalDateTime.parse(it) }?: error("无法获得下注时间")
            val settleTime = it.map["settlement_time"]?.toString()?.let { LocalDateTime.parse(it) }?: error("无法获得结算时间")

            val bet = it.map["stake"]?.toString()?.toBigDecimal()?: error("无法获得下注金额")
            val winLoseAmount = it.map["WinLoseAmount"]?.toString()?.toBigDecimal() ?: error("无法获得赢钱")

            val originData = objectMapper.writeValueAsString(it.map)

            BetOrderValue.BetOrderCo(orderId = orderId, clientId = clientId, memberId = memberId, betTime = betTime, settleTime = settleTime,
                    betAmount = bet, winAmount = winLoseAmount, originData = originData, platform = Platform.Lbc)
        }
    }

    data class Bet(

            @JsonIgnore
            @JsonAnySetter
            val map: Map<String, Any> = hashMapOf()

    )
}