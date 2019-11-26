package com.onepiece.treasure.games.slot

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.MegaClientToken
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformService
import com.onepiece.treasure.games.bet.MapUtil
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class MegaService : PlatformService() {

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    private fun sign(random: String, loginId: String = "", amount: String = "", clientToken: MegaClientToken): String {
        // 签名
        val signParam = "$random${clientToken.appId}${loginId}${amount}${clientToken.key}"
        return DigestUtils.md5Hex(signParam)
    }


    fun startPostJson(method: String, data: Map<String, Any>, clientToken: MegaClientToken): MapUtil {


        /**
         *         map["id"] = random
        map["sn"] = token.appId
        map["params"] = param
        map["method"] = method
        map["jsonrpc"] = "2.0"
         */

        //
        //        param["random"] = random
        //        param["sn"] = token.appId
        //        param["digest"] = sign
        //

        val param = hashMapOf(
                "id" to "${UUID.randomUUID()}",
                "sn" to clientToken.appId,
                "method" to method,
                "jsonrpc" to "2.0"
        )

        val url = "${gameConstant.getDomain(Platform.Mega)}/mega-cloud/api/"
        val result = okHttpUtil.doPostJson(url = url, data = param, clz = MegaValue.Result::class.java)
        check(result.error.isNullOrBlank()) { OnePieceExceptionCode.PLATFORM_DATA_FAIL }

        return result.mapUtil
    }


    override fun register(registerReq: GameValue.RegisterReq): String {
        val clientToken = registerReq.token as MegaClientToken

        val random = UUID.randomUUID().toString()
        val digest = this.sign(random = random, clientToken = clientToken)
        val data = mapOf(
                "nickname" to registerReq.name,
                "agentLoginId" to clientToken.agentId,
                "random" to random,
                "sn" to clientToken.appId,
                "digest" to digest
        )
        val mapUtil = this.startPostJson(method = "open.mega.user.create", data = data, clientToken = clientToken)
        return mapUtil.asMap("result").asString("loginId")
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val clientToken = balanceReq.token as MegaClientToken
        val random = UUID.randomUUID().toString()
        val digest = this.sign(random = random, loginId = balanceReq.username, clientToken = clientToken)
        val data = mapOf(
                "loginId" to balanceReq.username,
                "random" to random,
                "sn" to clientToken.appId,
                "digest" to digest
        )
        val mapUtil = this.startPostJson(method = "open.mega.balance.get", data = data, clientToken = clientToken)
        return mapUtil.asBigDecimal("result")
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {
        val clientToken = transferReq.token as MegaClientToken
        val random = UUID.randomUUID().toString()
        val digest = this.sign(random = random, loginId = transferReq.username, amount = "${transferReq.amount}", clientToken = clientToken)
        val data = mapOf(
                "loginId" to transferReq.username,
                "amount" to transferReq.amount,
                "random" to random,
                "sn" to clientToken.appId,
                "digest" to digest
        )
        val mapUtil = this.startPostJson(method = "open.mega.balance.transfer", data = data, clientToken = clientToken)
        return mapUtil.asString("id")
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): Boolean {
        val clientToken = checkTransferReq.token as MegaClientToken
        val random = UUID.randomUUID().toString()
        val digest = this.sign(random = random, loginId = checkTransferReq.username, clientToken = clientToken)

        val data = mapOf(
                "agentLoginId" to clientToken.agentId,
                "loginId" to checkTransferReq.username,
                "bizd" to checkTransferReq.orderId,
                "random" to random,
                "sn" to clientToken.appId,
                "digest" to digest
        )
        val mapUtil = this.startPostJson(method = "open.mega.balance.transfer.query", data = data, clientToken = clientToken)
        //TODO 判断是否转账成功
        return true
    }

    fun downApp(clientToken: MegaClientToken): String {
        val random = UUID.randomUUID().toString()
        val digest = this.sign(random = random, clientToken = clientToken)

        val data = mapOf(
                "agentLoginId" to clientToken.agentId,
                "random" to random,
                "sn" to clientToken.appId,
                "digest" to digest
        )
        val mapUtil = this.startPostJson(method = "open.mega.app.url.download", data = data, clientToken = clientToken)
        return mapUtil.asString("result")
    }

    override fun queryBetOrder(betOrderReq: GameValue.BetOrderReq): Any {
        val clientToken = betOrderReq.token as MegaClientToken
        val random = UUID.randomUUID().toString()
        val digest = this.sign(random = random, loginId = betOrderReq.username, clientToken = clientToken)

        val data = mapOf(
                "loginId" to betOrderReq.username,
                "startTime" to betOrderReq.startTime.format(dateTimeFormat),
                "endTime" to betOrderReq.startTime.format(dateTimeFormat),
                "random" to random,
                "sn" to clientToken.appId,
                "digest" to digest
        )
        val mapUtil = this.startPostJson(method = "open.mega.player.game.log.url.get", data = data, clientToken = clientToken)
        return mapUtil.asString("result")
    }

}