package com.onepiece.treasure.games.joker

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
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
        private val okHttpUtil: OkHttpUtil,
        private val objectMapper: ObjectMapper
) : GameCashApi {

    override fun wallet(username: String): BigDecimal {
        val urlParam = JokerParamBuilder.instance("GC")
                .set("Username", username)
                .build()

        val result =  okHttpUtil.doPost(JokerConstant.url, urlParam, JokerWalletResult::class.java)
        return result.credit
    }

    override fun clientBalance(): BigDecimal {
        val urlParam = JokerParamBuilder.instance("JP").build()
        val result = okHttpUtil.doGet(JokerConstant.url, urlParam, JokerBalanceResult::class.java)
        return result.amount
    }

    override fun transfer(username: String, orderId: String, money: BigDecimal): TransferResult {
        val urlParam = JokerParamBuilder.instance("TC")
                .set("RequestID", orderId)
                .set("Username", username)
                .build()

        val result = okHttpUtil.doPost(JokerConstant.url, urlParam, JokerTransferResult::class.java) { code ->
            when (code) {
                400 -> OnePieceExceptionCode.PLATFORM_TRANSFER_ORDERID_EXIST
                else -> OnePieceExceptionCode.PLATFORM_METHOD_FAIL
            }
        }
        return TransferResult(orderId = result.requestId, platformOrderId = result.requestId, balance = result.beforeCredit,
                afterBalance = result.beforeCredit)
    }

}