package com.onepiece.treasure.games.slot.joker

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.core.PlatformUsernameUtil
import com.onepiece.treasure.games.bet.CenterBetOrder
import jdk.nashorn.internal.ir.annotations.Ignore
import java.time.LocalDateTime

/**
 * joker的订单数据处理
 */
data class JokerBetOrder(
        val data: Data,
        val nextId: String

): CenterBetOrder {

    companion object {

        fun runTest(): List<BetOrderValue.BetOrderCo> {
            val json = """
                    {
                       "data":{
                          "Game":[
                             {
                                "OCode":"transocode",
                                "Username":"010002",
                                "GameCode":"gamecode",
                                "Description":"Main Scroll",
                                "Type":"Main",
                                "Amount":0.45,
                                "Result":2.75,
                                "Time":"2016-12-01T23:40:00.455827+08",
                                "AppID":"APP1"
                             }
                          ],
                          "Jackpot":[
                             {
                                "OCode":"transocode",
                                "Username":"user",
                                "GameCode":"Jackpot",
                                "Description":"Jackpot",
                                "Type":"Main",
                                "Amount":0,
                                "Result":2.75,
                                "Time":"2016-12-01T23:40:00.455827+08",
                                "AppID":"APP1"
                             }
                          ]
                       },
                       "nextId":"MDAwMDAwMDA="
                    }
                """.trimIndent()

            val objectMapper = jacksonObjectMapper()
            val jokerBetOrder = objectMapper.readValue<JokerBetOrder>(json)
            return jokerBetOrder.getBetOrders(objectMapper)
        }
    }

    data class Data(
            @JsonProperty("Game")
            val game: List<Game> = arrayListOf(),

            @JsonProperty("Jackpot")
            val jackpot: List<Jackpot> = arrayListOf()

    ) {
        data class Game(
                @Ignore
                @JsonAnySetter
                val map: Map<String, Any> = hashMapOf()
        )

        data class Jackpot(
                @Ignore
                @JsonAnySetter
                val map: Map<String, Any> = hashMapOf()
        )
    }

    override fun getBetOrders(objectMapper: ObjectMapper): List<BetOrderValue.BetOrderCo> {
        return this.data.game.filter { it.map["Username"]  == "A0011 s" } .map {
            val map = it.map


            val username = map["Username"]?.toString()?: error("无法查询到用户名")
            val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.Joker, platformUsername = username)

            val orderId = map["OCode"]?.toString()?: error("无法获得订单Id")
            val betAmount = map["Amount"]?.toString()?.toBigDecimal()?: error("无法获得下注金额")
            val winAmount = map["Result"]?.toString()?.toBigDecimal()?: error("无法获得最终结果输赢")

            val betTime = map["Time"]?.toString()?.substring(0, 19)?.let {
                LocalDateTime.parse(it)
            }?: error("无法获得下注时间")

            val originData = objectMapper.writeValueAsString(this)

            BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, platform = Platform.Joker, orderId = orderId, betAmount = betAmount,
                    winAmount = winAmount, betTime = betTime, settleTime = betTime, originData = originData)

        }
    }
}
