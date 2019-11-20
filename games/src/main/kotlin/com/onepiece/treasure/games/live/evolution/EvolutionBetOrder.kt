//package com.onepiece.treasure.games.live.evolution
//
//import com.fasterxml.jackson.annotation.JsonAnySetter
//import com.fasterxml.jackson.annotation.JsonIgnore
//import com.fasterxml.jackson.databind.ObjectMapper
//import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
//import com.onepiece.treasure.beans.enums.Platform
//import com.onepiece.treasure.beans.value.database.BetOrderValue
//import com.onepiece.treasure.core.PlatformUsernameUtil
//import com.onepiece.treasure.games.bet.CenterBetOrder
//import java.time.LocalDateTime
//
//data class EvolutionBetOrder(
//
//        val x: Int
//
//): CenterBetOrder {
//
//    data class Data(
//            @JsonIgnore
//            @JsonAnySetter
//            val map: Map<String, Any> = hashMapOf()
//    )
//
//
//    override fun getBetOrders(objectMapper: ObjectMapper): List<BetOrderValue.BetOrderCo> {
//
//
//        val json = "{\"id\": \"a1s2d3f4g5h6j7k8\", \"startedAt\": \"2017-01-03T10:02:59.117Z\", \"settledAt\": \"2017-01-03T10:03:40.246Z\", \"status\": \"Resolved\", \"gameType\": \"roulette\", \"table\": { \"id\": \"a1s2d3f4g5h6j7k8\",\n" +
//                "\"name\": \"Roulette VIP\" }, \"dealer\": { \"uid\": \"z1x2c3v4b5n6\", \"name\": \"Gnome Ann\" }, \"currency\": \"EUR\", \"wager\": 1750.00, \"payout\": 2000.00, \"participants\": [{ \"casinoId\": \"thebest00000000001\", \"playerId\": \"010002\", \"screenName\": \"John Doe\", \"sessionId\": \"3232jh5488fj88df87f8\", \"casinoSessionId\": \"3jh4jg43ghdf77f8\", \"currency\": \"EUR\", \"bets\": [{ \"code\": \"ROU_1Red\", \"stake\": 5, \"payout\": 0, \"placedOn\": \"2017-02-08T13:07:40.222Z\", \"description\": \"1 Red\", \"transactionId\": \"1234\" }, { \"code\": \"ROU_32Red\", \"stake\": 1, \"payout\": 36, \"placedOn\": \"2017- 02-08T13:07:44.223Z\", \"description\": \"32 Red\", \"transactionId\": \"1234\" }], \"configOverlays\": [\"virtual_table_id\"] }], \"result\": {\"outcomes\": [{ \"number\": \"32\", \"type\": \"Even\", \"color\": \"Red\" }]}}}"
//
//
//        val data = jacksonObjectMapper().readValue(json, EvolutionBetOrder.Data::class.java)
//
//        // 只接受结算订单
//        if (data.map["status"] != "Resolved") {
//            return emptyList()
//        }
//
//        val orderId = data.map["id"]?.toString()?: error("无法获得订单Id")
//        val wager = data.map["wager"]?.toString()?.toBigDecimal()?: error("无法获得订单金额")
//        val payout = data.map["payout"]?.toString()?.toBigDecimal()?: error("无法获得盈利金额")
//
//        val playerId = (data.map["participants"] as List<Map<String, Any>>).first()["playerId"]?.toString()?: error("无法获得用户名")
//        val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.Evolution, platformUsername = playerId)
//        val betTime = LocalDateTime.parse(data.map["startedAt"]?.toString()?.substring(0, 19)?: error("无法获得下注时间"))
//        val settledTime = LocalDateTime.parse(data.map["settledAt"]?.toString()?.substring(0, 19)?: error("无法获得结算时间"))
//
//        val originData = jacksonObjectMapper().writeValueAsString(data)
//
//        val order = BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, orderId = orderId, betAmount = wager, winAmount = payout, betTime = betTime,
//                settleTime = settledTime, platform = Platform.Evolution, originData = originData)
//        return listOf(order)
//    }
//}
//
