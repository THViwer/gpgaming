package com.onepiece.treasure.games.live.dg

import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.model.token.DefaultClientToken
import java.math.BigDecimal

interface DGApi {

    /**
     * 注册用户
     */
    fun signup(token: DefaultClientToken, username: String, password: String): String


    /**
     * 启动游戏
     */
    fun login(token: DefaultClientToken, username: String, startPlatform: LaunchMethod): String


    /**
     * 启动试玩
     */
    fun loginFree(token: DefaultClientToken, startPlatform: LaunchMethod): String

    /**
     * 获得余额
     */
    fun getBalance(token: DefaultClientToken, username: String): BigDecimal


    /**
     * 会员转账
     */
    fun transfer(token: DefaultClientToken, username: String, orderId: String, amount: BigDecimal): String

    /**
     * 检查转账是否成功
     */
    fun checkTransfer(token: DefaultClientToken, orderId: String): Boolean

    /**
     * 获得会员订单
     */
    fun getReport(token: DefaultClientToken): String

}