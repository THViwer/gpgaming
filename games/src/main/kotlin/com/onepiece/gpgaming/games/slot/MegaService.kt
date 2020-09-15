package com.onepiece.gpgaming.games.slot

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.token.MegaClientToken
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.http.GameResponse
import com.onepiece.gpgaming.games.http.OKParam
import com.onepiece.gpgaming.games.http.OKResponse
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.Exception
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class MegaService : PlatformService() {

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val log = LoggerFactory.getLogger(MegaService::class.java)

    private fun sign(random: String, loginId: String = "", amount: String = "", clientToken: MegaClientToken): String {
        // 签名
        val signParam = "$random${clientToken.appId}${loginId}${amount}${clientToken.key}"
        return DigestUtils.md5Hex(signParam)
    }


    fun doPost(method: String, data: Map<String, Any>, clientToken: MegaClientToken): OKResponse {
        val param = hashMapOf(
                "id" to "${UUID.randomUUID()}",
                "sn" to clientToken.appId,
                "method" to method,
                "jsonrpc" to "2.0",
                "params" to data
        )

        val url = "${clientToken.apiPath}/mega-cloud/api/"

        val okParam = OKParam.ofPost(url = url, param = objectMapper.writeValueAsString(param))
        val okResponse = u9HttpRequest.startRequest(okParam)
        if (!okResponse.ok) return okResponse

        val ok = try {
            val error = okResponse.mapUtil.data["error"]?.toString()
            error.isNullOrBlank()
        } catch (e: Exception) {
            false
        }

        return okResponse.copy(ok = ok)
    }


    override fun register(registerReq: GameValue.RegisterReq): GameResponse<String> {
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
        val okResponse = this.doPost(method = "open.mega.user.create", data = data, clientToken = clientToken)
        return this.bindGameResponse(okResponse = okResponse) {
            it.asMap("result").asString("loginId")
        }
    }

    override fun balance(balanceReq: GameValue.BalanceReq): GameResponse<BigDecimal> {
        val clientToken = balanceReq.token as MegaClientToken
        val random = UUID.randomUUID().toString()
        val digest = this.sign(random = random, loginId = balanceReq.username, clientToken = clientToken)
        val data = mapOf(
                "loginId" to balanceReq.username,
                "random" to random,
                "sn" to clientToken.appId,
                "digest" to digest
        )
        val okResponse = this.doPost(method = "open.mega.balance.get", data = data, clientToken = clientToken)
        return this.bindGameResponse(okResponse = okResponse) {
            it.asBigDecimal("result")
        }
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameResponse<GameValue.TransferResp> {
        val clientToken = transferReq.token as MegaClientToken
        val random = UUID.randomUUID().toString()

        val digest = this.sign(random = random, loginId = transferReq.username, amount = transferReq.amount.stripTrailingZeros().toPlainString(), clientToken = clientToken)
        val data = mapOf(
                "loginId" to transferReq.username,
                "amount" to transferReq.amount.stripTrailingZeros().toPlainString(),
                "bizId" to transferReq.orderId,
                "random" to random,
                "sn" to clientToken.appId,
                "digest" to digest
        )
        val okResponse = this.doPost(method = "open.mega.balance.transfer", data = data, clientToken = clientToken)
        return this.bindGameResponse(okResponse = okResponse) {
            val balance = it.asBigDecimal("result")
            GameValue.TransferResp.successful(balance = balance)
        }
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameResponse<GameValue.TransferResp> {
        val clientToken = checkTransferReq.token as MegaClientToken
        val random = UUID.randomUUID().toString()
        val digest = this.sign(random = random, clientToken = clientToken)

        val data = mapOf(
                "agentLoginId" to clientToken.agentId,
                "loginId" to checkTransferReq.username,
                "bizId" to checkTransferReq.orderId,
                "random" to random,
                "sn" to clientToken.appId,
                "digest" to digest
        )
        val okResponse = this.doPost(method = "open.mega.balance.transfer.query", data = data, clientToken = clientToken)
        //TODO 判断是否转账成功
        return this.bindGameResponse(okResponse = okResponse) {
            val successful = it.asMap("result").asInt("total") > 0
            GameValue.TransferResp.of(successful)
        }
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
        val okResponse = this.doPost(method = "open.mega.app.url.download", data = data, clientToken = clientToken)
        return okResponse.asString("result")
    }

    open fun getBetOrderHtml(betOrderReq: GameValue.BetOrderReq): GameResponse<String> {
        val clientToken = betOrderReq.token as MegaClientToken
        val random = UUID.randomUUID().toString()
        val digest = this.sign(random = random, loginId = betOrderReq.username, clientToken = clientToken)

        val data = mapOf(
                "loginId" to betOrderReq.username,
                "startTime" to betOrderReq.startTime.format(dateTimeFormat),
                "endTime" to betOrderReq.endTime.format(dateTimeFormat),
                "random" to random,
                "sn" to clientToken.appId,
                "digest" to digest
        )
        val okResponse = this.doPost(method = "open.mega.player.game.log.url.get", data = data, clientToken = clientToken)
        return this.bindGameResponse(okResponse = okResponse) {
            it.asString("result")
        }
    }

    override fun queryReport(reportQueryReq: GameValue.ReportQueryReq): GameResponse<List<GameValue.PlatformReportData>> {

        val clientToken = reportQueryReq.token as MegaClientToken

        val random = UUID.randomUUID().toString()
        val digest = this.sign(random = random, loginId = clientToken.agentId, clientToken = clientToken)


        val startTime = reportQueryReq.startDate.atStartOfDay()
        val endTime = startTime.plusDays(1)
        val data = mapOf(
                "agentLoginId" to clientToken.agentId,
                "startTime" to startTime.format(dateTimeFormat),
                "endTime" to endTime.format(dateTimeFormat),
                "random" to random,
                "type" to "1",
                "sn" to clientToken.appId,
                "digest" to digest
        )
        val okResponse = this.doPost(method = "open.mega.player.total.report", data = data, clientToken = clientToken)

        return this.bindGameResponse(okResponse = okResponse) {
            it.asList("result").map {
                val bet = BigDecimal.ZERO
                val win = it.asBigDecimal("win").negate()
                val username = it.asString("loginId")
                val originData = objectMapper.writeValueAsString(it.data)
                GameValue.PlatformReportData(username = username, platform = Platform.Mega, bet = bet, win = win, originData = originData)
            }
        }
    }


//    fun queryBetReport(token: ClientToken, username: String, startTime: LocalDateTime): BigDecimal {
//
//        val clientToken = token as MegaClientToken
//
//        val random = UUID.randomUUID().toString()
//        val digest = this.sign(random = random, loginId = username, clientToken = clientToken)
//
//        val endTime = LocalDateTime.now()
//        val data = mapOf(
//                "loginId" to username,
//                "startTime" to startTime.format(dateTimeFormat),
//                "endTime" to endTime.format(dateTimeFormat),
//                "random" to random,
//                "sn" to clientToken.appId,
//                "digest" to digest
//        )
//        val mapUtil = this.startPostJson(method = "open.mega.player.total.report", data = data, clientToken = clientToken)
//        return BigDecimal.ZERO
//
//
//    }

}
