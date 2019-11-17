//package com.onepiece.treasure.games.slot.joker
//
//import com.onepiece.treasure.beans.enums.LaunchMethod
//import com.onepiece.treasure.beans.model.token.DefaultClientToken
//import com.onepiece.treasure.beans.value.internet.web.SlotGame
//import java.math.BigDecimal
//import java.time.LocalDate
//import java.time.LocalDateTime
//
//interface JokerApi {
//
//    /**
//     * 游戏列表
//     */
//    fun slotGames(token: DefaultClientToken, launch: LaunchMethod): List<SlotGame>
//
//    /**
//     * 注册账号
//     */
//    fun register(token: DefaultClientToken, username: String, password: String): String
//
//    /**
//     * 获得余额
//     */
//    fun getCredit(token: DefaultClientToken, username: String): BigDecimal
//
//    /**
//     * 转账
//     */
//    fun transferCredit(token: DefaultClientToken, orderId: String, username: String, amount: BigDecimal): String
//
//
//    /**
//     * 开始游戏
//     */
//    fun start(token: DefaultClientToken, username: String, gameId: String): String
//
//
//    /**
//     * 获得订单数据
//     */
//    fun retrieveTransactions(token: DefaultClientToken, startTime: LocalDateTime, endTime: LocalDateTime): String
//
//
//    /**
//     * 获得用户报表
//     */
//    fun getMembersWinLoss(token: DefaultClientToken, username: String?, startDate: LocalDate, endDate: LocalDate): JokerValue.GetMembersWinLoss
//
//
//
//
//}