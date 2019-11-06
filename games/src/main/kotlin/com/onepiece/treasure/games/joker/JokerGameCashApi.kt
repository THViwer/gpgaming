package com.onepiece.treasure.games.joker

import com.onepiece.treasure.games.GameCashApi
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.games.joker.value.JokerBalanceResult
import com.onepiece.treasure.games.joker.value.JokerTransferResult
import com.onepiece.treasure.games.joker.value.JokerWalletResult
import com.onepiece.treasure.games.value.TransferResult
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class JokerGameCashApi(
        private val okHttpUtil: OkHttpUtil
) : GameCashApi() {

    override fun wallet(username: String): BigDecimal {
        val (url, formBody) = JokerParamBuilder.instance("GC")
                .set("Username", username)
                .build()

        val result =  okHttpUtil.doPostForm(url, formBody, JokerWalletResult::class.java)
        return result.credit
    }

    override fun clientBalance(): BigDecimal {
        val (url, formBody) = JokerParamBuilder.instance("JP").build()
        val result = okHttpUtil.doPostForm(url, formBody, JokerBalanceResult::class.java)
        return result.amount
    }

    override fun transfer(username: String, orderId: String, money: BigDecimal): TransferResult {
        val (url, formBody) = JokerParamBuilder.instance("TC")
                .set("Amount", money.toString())
                .set("RequestID", orderId)
                .set("Username", username)
                .build()

        val result = okHttpUtil.doPostForm(url, formBody, JokerTransferResult::class.java)
        return TransferResult(orderId = result.requestId, platformOrderId = result.requestId, balance = result.credit,
                afterBalance = result.beforeCredit)
    }

}