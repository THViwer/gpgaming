package com.onepiece.gpgaming.games.live

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.token.DreamGamingClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.bet.BetOrderUtil
import com.onepiece.gpgaming.games.bet.MapUtil
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class   DreamGamingService : PlatformService() {

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val log = LoggerFactory.getLogger(DreamGamingService::class.java)

    fun doStartPostJson(clientToken: DreamGamingClientToken, method: String, data: String): MapUtil {

        val url = "${clientToken.apiPath}$method"
        val result = okHttpUtil.doPostJson(platform = Platform.DreamGaming, url = url, data = data, clz = DreamGamingValue.Result::class.java)

        check(result.codeId == 0) {
            log.error("dreamGaming network error: codeId = ${result.codeId}")
            OnePieceExceptionCode.PLATFORM_DATA_FAIL
        }
        return result.mapUtil
    }

    private fun getToken(clientToken: DreamGamingClientToken): Pair<String, String> {
        val random = UUID.randomUUID().toString()
        val sign = DigestUtils.md5Hex("${clientToken.agentName}${clientToken.key}$random")
        return random to sign
    }


    override fun register(registerReq: GameValue.RegisterReq): String {
        val clientToken = registerReq.token as DreamGamingClientToken

        val md5Password = DigestUtils.md5Hex(registerReq.password)
        val (random, sign) = this.getToken(clientToken)
        val data = """
            {
                "token":"$sign",
                "random":"$random",
                "data":"${clientToken.data}",
                "member":{
                    "username":"${registerReq.username}",
                    "password":"$md5Password",
                    "currencyName":"${clientToken.currency}",
                    "winLimit":0
                }
            }
        """.trimIndent()

        this.doStartPostJson(clientToken = clientToken, method = "/user/signup/${clientToken.agentName}", data = data)
        return registerReq.username
    }

    override fun updatePassword(updatePasswordReq: GameValue.UpdatePasswordReq) {
        val clientToken = updatePasswordReq.token as DreamGamingClientToken

        val md5Password = DigestUtils.md5Hex(updatePasswordReq.password)
        val (random, sign) = this.getToken(clientToken)
        val data = """
            {
                "token":"$sign",
                "random":"$random",
                "member":{
                    "username":"${updatePasswordReq.username}",
                    "password":"$md5Password",
                    "status": 1,
                    "winLimit":0
                }
            }
        """.trimIndent()

        this.doStartPostJson(clientToken = clientToken, method = "/user/update/${clientToken.agentName}", data = data)
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val clientToken = balanceReq.token as DreamGamingClientToken
        val (random, sign) = this.getToken(clientToken)

        val data = """
            {
                "token":"$sign",
                "random":"$random",
                "member":{"username":"${balanceReq.username}"}
            } 
        """.trimIndent()

        val mapUtil = this.doStartPostJson(clientToken = clientToken,  method = "/user/getBalance/${clientToken.agentName}", data = data)

        return mapUtil.asMap("member").asBigDecimal("balance")
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameValue.TransferResp {
        val clientToken = transferReq.token as DreamGamingClientToken
        val (random, sign) = this.getToken(clientToken)

        val data = """
            {
                "token":"$sign",
                "random":"$random",
                "data":"${transferReq.orderId}",
                "member":{
                    "username":"${transferReq.username}",
                    "amount":${transferReq.amount}
                }
            } 
        """.trimIndent()

        val mapUtil = this.doStartPostJson(clientToken = clientToken, method = "/account/transfer/${clientToken.agentName}", data = data)
        val platformOrderId = mapUtil.asString("data")
        val balance = mapUtil.asMap("member").asBigDecimal("balance")
        return GameValue.TransferResp.successful(balance = balance, platformOrderId = platformOrderId)
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameValue.TransferResp {
        val clientToken = checkTransferReq.token as DreamGamingClientToken
        val (random, sign) = this.getToken(clientToken)

        val data = """
            {
                "token":"$sign",
                "random":"$random",
                "data":"${checkTransferReq.orderId}"
            } 
        """.trimIndent()


        val url = "${clientToken.apiPath}/account/checkTransfer/${clientToken.agentName}"
        val result = okHttpUtil.doPostJson(platform = Platform.DreamGaming, url = url, data = data, clz = DreamGamingValue.Result::class.java)
        val successful = result.codeId == 0
        return GameValue.TransferResp.of(successful)
    }


    override fun start(startReq: GameValue.StartReq): String {
        val clientToken = startReq.token as DreamGamingClientToken

        val lang = when (startReq.language) {
            Language.EN -> "en"
            Language.CN -> "cn"
            Language.TH -> "th"
            else -> "en"
        }

        val (random, sign) = this.getToken(clientToken)
        val data = """
            {
                "token":"$sign",
                "random":"$random",
                "lang":"$lang",
                "member":{
                    "username":"${startReq.username}"
                }
            }
        """.trimIndent()
        val mapUtil = this.doStartPostJson(clientToken = clientToken, method = "/user/login/${clientToken.agentName}", data = data)
        val list = mapUtil.data["list"] as List<String>

        val token = mapUtil.asString("token")
        return when (startReq.launch) {
            LaunchMethod.Web -> list[0]
            LaunchMethod.Wap -> list[1]
            else -> list[2]
        }.plus(token)
    }

//
//    override fun startSlotDemo(token: ClientToken, startPlatform: LaunchMethod): String {
//        val param = DGBuild.instance(token, "/user/free")
//        val data = """
//            {
//                "token":"${param.token}",
//                "random":"${param.random}",
//                "lang":"$lang",
//                "device": 1
//            }
//        """.trimIndent()
//
//        val result = okHttpUtil.doPostJson(param.url, data, DGValue.LoginResult::class.java)
//        checkCode(result.codeId)
//
//        return when (startPlatform) {
//            LaunchMethod.Web -> result.list[0]
//            LaunchMethod.Wap -> result.list[1]
//            else -> result.list[2]
//        }.plus(result.token)
//    }


    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {
        val clientToken = pullBetOrderReq.token as DreamGamingClientToken
        val (random, sign) = this.getToken(clientToken)

        val data = """
            {
                "token":"$sign",
                "random":"$random"
            } 
        """.trimIndent()

        val mapUtil = this.doStartPostJson(clientToken = clientToken, method = "/game/getReport/${clientToken.agentName}", data = data)
        val orders = mapUtil.asList("list").map { bet ->
            BetOrderUtil.instance(platform = Platform.DreamGaming, mapUtil = bet)
                    .set("orderId", "id")
                    .set("username", "userName")
                    .set("betAmount", "betPoints")
                    .set("availableBet", "availableBet")
                    .set("winAmount", "winOrLoss")
                    .set("betTime", "betTime", dateTimeFormat)
                    .set("settleTime", "calTime", dateTimeFormat)
                    .build(objectMapper)
        }

        val ids = orders.map { it.orderId.toLong() }
        this.mark(clientToken = clientToken, ids = ids)

        return orders
    }

    private fun mark(clientToken: DreamGamingClientToken, ids: List<Long>) {
        if (ids.isEmpty()) return

        val (random, sign) = this.getToken(clientToken)

        val data = """
            {
                "token":"$sign",
                "random":"$random",
                "list":[${ids.joinToString(",")}]
            } 
        """.trimIndent()

        this.doStartPostJson(clientToken = clientToken, method = "/game/markReport/${clientToken.agentName}", data = data)
    }

}