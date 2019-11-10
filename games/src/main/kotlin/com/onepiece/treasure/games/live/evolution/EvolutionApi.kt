package com.onepiece.treasure.games.live.evolution

import com.onepiece.treasure.beans.model.token.DefaultClientToken
import java.math.BigDecimal

interface EvolutionApi {


    /**
     * 创建用户
     */
    fun createUser(token: DefaultClientToken, username: String): String

    /**
     * amount > 0 充值
     * amount < 0 取款
     */
    fun depositOrWithdraw(token: DefaultClientToken, username: String, orderId: String, amount: BigDecimal)


    /**
     * 获得余额
     */
    fun getBalance(token: DefaultClientToken, username: String): BigDecimal

    /**
     * 转账信息
     */
    fun transactionInfo(token: DefaultClientToken, orderId: String, username: String): Boolean


}