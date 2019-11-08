package com.onepiece.treasure.games.sport.sbo

import com.onepiece.treasure.beans.model.token.DefaultClientToken
import java.math.BigDecimal
import java.time.LocalDate

interface SboApi {

    /**
     * 注册账号
     */
    fun registerPlayer(token: DefaultClientToken, username: String): String

    /**
     * 启动游戏
     */
    fun login(token: DefaultClientToken, username: String): String

    /**
     * 转账:
     * amount > 0, 中心 -> sbo
     * amount < 0, sbo -> 中心
     */
    fun depositOrWithdraw(token: DefaultClientToken, username: String, orderId: String, amount: BigDecimal): String

    /**
     * 检查转账订单
     */
    fun checkTransferStatus(token: DefaultClientToken, orderId: String): Boolean

    /**
     * 获得用户余额
     */
    fun getPlayerBalance(token: DefaultClientToken, username: String): BigDecimal

    /**
     * 获得会员报表
     */
    fun getCustomerReport(token: DefaultClientToken, username: String, startDate: LocalDate, endDate: LocalDate): SboValue.PlayerRevenue

    /**
     * 获得会员下注记录
     */
    fun getCustomerBetList(token: DefaultClientToken, username: String, startDate: LocalDate, endDate: LocalDate): List<SboValue.PlayerBet>


//    /**
//     * 拉会员报表
//     */
//    fun pullCustomerReport()
//
//    /**
//     * 拉会员下注记录
//     */
//    fun pullCustomerBetList()

}