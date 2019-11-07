package com.onepiece.treasure.games.kiss918

import com.onepiece.treasure.games.GameCashApi
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.games.value.ClientAuthVo
import com.onepiece.treasure.games.value.TransferResult
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class Kiss918GameCashApi(
        private val okHttpUtil: OkHttpUtil
) : GameCashApi() {

    override fun wallet(clientAuthVo: ClientAuthVo?, username: String): BigDecimal {

        val url = Kiss918Builder.instance(path = "/ashx/account/account.ashx")
                .set("action", "getUserInfo")
                .set("userName", username)
                .build(username = username)

        val userinfo = okHttpUtil.doGet(url, Kiss918Value.Userinfo::class.java)
        return userinfo.moneyNumber
    }

    override fun transfer(clientAuthVo: ClientAuthVo?, username: String, orderId: String, money: BigDecimal): TransferResult {
        val url = Kiss918Builder.instance(path = "/ashx/account/setScore.ashx")
                .set("action", "setServerScore")
                .set("orderid", orderId)
                .set("scoreNum", "$money")
                .set("userName", username)
                .set("ActionUser", "system")
                .set("ActionIp", "12.213.1.24")
                .build(username = username)
        val result = okHttpUtil.doGet(url, Kiss918Value.TransferResult::class.java)

        return TransferResult(orderId = orderId, afterBalance = result.money, balance = result.money.subtract(money), platformOrderId = orderId)
    }
}