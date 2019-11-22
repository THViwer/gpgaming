package com.onepiece.treasure.games.slot.mega

import com.onepiece.treasure.beans.model.token.MegaClientToken
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformService
import com.onepiece.treasure.games.slot.mega.MegaValue.RegisterResult
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

@Service
class MegaService: PlatformService() {

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun register(registerReq: GameValue.RegisterReq): String {

        val (url, param) = MegaBuild.instance()
                .set("nickname", registerReq.name)
                .set("agentLoginId", (registerReq.token as MegaClientToken).agentId)
                .build(registerReq.token, "open.mega.user.create")

        val result = okHttpUtil.doPostJson(url = url, data = param, clz = RegisterResult::class.java)
        return result.result!!.loginId
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val (url, param) = MegaBuild.instance()
                .set("loginId", balanceReq.username)
                .build(token = balanceReq.token as MegaClientToken, method = "open.mega.balance.get")

        val result = okHttpUtil.doPostJson(url = url, data = param, clz = MegaValue.BalanceResult::class.java)
        return result.result

    }

    override fun transfer(transferReq: GameValue.TransferReq): String {

        val (url, param) = MegaBuild.instance()
                .set("loginId", transferReq.username)
                .set("amount", transferReq.amount)
                .build(token = transferReq.token as MegaClientToken, method = "open.mega.balance.transfer")


        val result = okHttpUtil.doPostJson(url = url, data = param, clz = MegaValue.BalanceResult::class.java)
        return result.id
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): Boolean {

        val (url, param) = MegaBuild.instance()
                .set("agentLoginId", (checkTransferReq.token as MegaClientToken).agentId)
                .set("loginId", checkTransferReq.username)
                .set("bizd", checkTransferReq.orderId)
                .build(token = checkTransferReq.token, method = "open.mega.balance.transfer.query")

        val str = okHttpUtil.doPostJson(url = url, data = param, clz = String::class.java)
        return true
    }

    fun downApp(token: MegaClientToken): String {

        val (url, param) = MegaBuild.instance()
                .set("agentLoginId", token.agentId)
                .build(token = token, method = "open.mega.app.url.download")


        val result = okHttpUtil.doPostJson(url = url, data = param, clz = MegaValue.DownAppResult::class.java)
        return result.result
    }


    override fun queryBetOrder(betOrderReq: GameValue.BetOrderReq): Any {

        val (url, param) = MegaBuild.instance()
                .set("loginId", betOrderReq.username)
                .set("startTime", betOrderReq.startTime.format(dateTimeFormat))
                .set("endTime", betOrderReq.startTime.format(dateTimeFormat))
                .build(token = betOrderReq.token as MegaClientToken, method = "open.mega.player.game.log.url.get")

        val result = okHttpUtil.doPostJson(url = url, data = param, clz = MegaValue.BetQueryResult::class.java)
        return result.result
    }

}