package com.onepiece.treasure.games.cta666

import com.onepiece.treasure.games.GameCashApi
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.games.value.TransferResult
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class Cta666GameCashApi(
        private val okHttpUtil: OkHttpUtil
) : GameCashApi {

    override fun wallet(username: String): BigDecimal {

        val param = Cat666ParamBuilder.instance("getBalance")
        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}",
                "member":{"username":"$username"}
            } 
        """.trimIndent()
        val result = okHttpUtil.doPostJson(param.url, data, Cta666Result.Balance::class.java)
        return result.member.balance
    }

    override fun clientBalance(): BigDecimal {
        return BigDecimal.valueOf(-1)
    }

    override fun transfer(username: String, orderId: String, money: BigDecimal): TransferResult {


        val param = Cat666ParamBuilder.instance("transfer")

        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}",
                "data":"$orderId",
                "member":{
                    "username":"$username",
                    "amount":${money}
                }
            } 
        """.trimIndent()

        val result = okHttpUtil.doPostJson(param.url, data, Cta666Result.Transfer::class.java)

        return TransferResult(orderId = orderId, afterBalance = result.member.balance, platformOrderId = result.data,
                balance = result.member.balance.subtract(money))

    }
}