//package com.onepiece.treasure.games.slot.kiss918
//
//import com.onepiece.treasure.beans.model.token.Kiss918ClientToken
//import com.onepiece.treasure.games.value.ReportVo
//import java.math.BigDecimal
//import java.time.LocalDate
//import java.time.LocalDateTime
//
//interface Kiss918Api {
//
//
//    /**
//     * 添加用户
//     */
//    fun addUser(token: Kiss918ClientToken, password: String): String
//
//    /**
//     * 查询用户信息(balance)
//     */
//    fun userinfo(token: Kiss918ClientToken, username: String): BigDecimal
//
//
//    /**
//     * 上分
//     */
//    fun setScore(token: Kiss918ClientToken, orderId: String, username: String, amount: BigDecimal): String
//
//    /**
//     * 游戏日志
//     */
//    fun gameLog(token: Kiss918ClientToken, username: String, startTime: LocalDateTime, endTime: LocalDateTime): Any
//
//    /**
//     * 会员报表
//     */
//    fun accountReport(token: Kiss918ClientToken, username: String, startDate: LocalDate, endDate: LocalDate): List<ReportVo>
//
//    /**
//     * 代理报表
//     */
//    fun agentMoneyLog(token: Kiss918ClientToken, startDate: LocalDate, endDate: LocalDate): List<ReportVo>
//
//}