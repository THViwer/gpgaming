package com.onepiece.treasure.games.live.sexy

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.SexyClientToken
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformApi
import com.onepiece.treasure.games.http.OkHttpUtil
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.net.URLEncoder

@Service
class SexyService(
        private val okHttpUtil: OkHttpUtil
) : PlatformApi() {

    fun checkCode(code: Int) {
        check(code == 1) { OnePieceExceptionCode.DATA_FAIL }
    }

    override fun register(registerReq: GameValue.RegisterReq): String {
        val url = SexyBuild.instance("/getKey")
                .set("cert", (registerReq.token as SexyClientToken).key)
                .set("user", registerReq.username)
                .set("betLimitIds", URLEncoder.encode("[29,30,31,32,33,34,35]", "utf-8"))
                .build(token = registerReq.token)

        val result = okHttpUtil.doGet(url = url, clz = SexyValue.Login::class.java)
        this.checkCode(result.status)

        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {

        val url = SexyBuild.instance("/getBalance")
                .set("cert", (balanceReq.token as SexyClientToken).key)
                .set("alluser", 0)
                .set("users", balanceReq.username)
                .build(token = balanceReq.token)

        val result = okHttpUtil.doGet(url = url, clz = SexyValue.Balance::class.java)
        this.checkCode(result.status)

        return result.results.first().balance
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {
        when (transferReq.amount.toDouble() > 0) {
            true -> {
                val url = SexyBuild.instance("/deposit")
                        .set("cert", (transferReq.token as SexyClientToken).key)
                        .set("user", transferReq.username)
                        .set("balance", transferReq.amount)
                        .set("ts_code", transferReq.orderId)
                        .set("extension1", transferReq.token.agentId)
                        .build(token = transferReq.token)

                val deposit = okHttpUtil.doGet(url = url, clz = SexyValue.Deposit::class.java)
                this.checkCode(deposit.status)
            }
            false -> {
                val url = SexyBuild.instance("/withdraw")
                        .set("cert", (transferReq.token as SexyClientToken).key)
                        .set("user", transferReq.username)
                        .set("withdrawtype", 0)
                        .set("balance", transferReq.amount)
                        .set("ts_code", transferReq.orderId)
                        .set("extension1", transferReq.token.agentId)
                        .build(token = transferReq.token)

                val withdraw = okHttpUtil.doGet(url = url, clz = SexyValue.Withdraw::class.java)
                this.checkCode(withdraw.status)
            }
        }

        return transferReq.orderId
    }

    override fun start(startReq: GameValue.StartReq): String {
        val url = SexyBuild.instance("/getKey")
                .set("cert", (startReq.token as SexyClientToken).key)
                .set("user", startReq.username)
                .build(token = startReq.token)

        val result = okHttpUtil.doGet(url = url, clz = SexyValue.Login::class.java)

        return "${result.url}${result.key}"
    }

    override fun queryBetOrder(betOrderReq: GameValue.BetOrderReq): Any {
        val url = SexyBuild.instance("/getPlayerReportByRoundStartTime")
                .set("cert", (betOrderReq.token as SexyClientToken).key)
                .set("user", betOrderReq.username)
                .set("st", betOrderReq.startTime.toLocalDate())
                .set("et", betOrderReq.endTime.toLocalDate())
                .build(token = betOrderReq.token)
        val result = okHttpUtil.doGet(url = url, clz = SexyValue.BetOrder::class.java)
        this.checkCode(result.status)
        return result
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {

        val url = SexyBuild.instance("/getSettledTransactionsByRoundStartTime")
                .set("st", pullBetOrderReq.startTime.toLocalDate())
                .set("et", pullBetOrderReq.endTime.toLocalDate())

        return emptyList()
    }



}