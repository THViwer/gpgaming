package com.onepiece.treasure.games.sport.sbo

import com.onepiece.treasure.beans.model.token.DefaultClientToken
import java.math.BigDecimal
import java.time.LocalDate

interface SboApi {

    /**
     * 注册账号
     */
    fun registerPlayer(clientToken: DefaultClientToken, username: String): String

    /**
     * 启动游戏
     */
    fun login(clientToken: DefaultClientToken, username: String): String

    /**
     * 转账:
     * amount > 0, 中心 -> sbo
     * amount < 0, sbo -> 中心
     */
    fun depositOrWithdraw(clientToken: DefaultClientToken, username: String, orderId: String, amount: BigDecimal): String

    /**
     * 检查转账订单
     */
    fun checkTransferStatus(clientToken: DefaultClientToken, orderId: String): Boolean

    /**
     * 获得用户余额
     */
    fun getPlayerBalance(clientToken: DefaultClientToken, username: String): BigDecimal

    /**
     * 获得会员报表
     */
    fun getCustomerReport(clientToken: DefaultClientToken, username: String, startDate: LocalDate, endDate: LocalDate): SboValue.PlayerRevenue

    /**
     * 获得会员下注记录
     */
    fun getCustomerBetList(clientToken: DefaultClientToken, username: String, startDate: LocalDate, endDate: LocalDate): List<SboValue.PlayerBet>


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