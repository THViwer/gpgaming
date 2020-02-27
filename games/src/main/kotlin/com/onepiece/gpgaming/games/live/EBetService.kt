package com.onepiece.gpgaming.games.live

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.token.EBetClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.ActiveConfig
import com.onepiece.gpgaming.core.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap

@Service
class EBetService(
        private val activeConfig: ActiveConfig
) : PlatformService() {

    private val log = LoggerFactory.getLogger(EBetService::class.java)
    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    private fun doStartPost(data: HashMap<String, Any>, clientToken: EBetClientToken, path: String): EBetValue.Result {

        val username = data["username"]?.toString() ?: ""
        val timestamp = data["timestamp"]?.toString() ?: ""
        val signKey = "$username${timestamp}"
        val sign = EBetSignUtil.sign(signKey, clientToken.privateKey)
        data["signature"] = sign

        val url = if (activeConfig.profile == "dev") {
            "http://94.237.64.70:2011"
        } else {
            clientToken.apiUrl
        }
//        val url = clientToken.apiUrl
        val result = okHttpUtil.doPostJson(url = "$url$path", data = data, clz = EBetValue.Result::class.java)
        check(result.status == "200") {
            log.error("eBet request error: url = $url, request = $data, response: $result")
            OnePieceExceptionCode.PLATFORM_DATA_FAIL
        }
        return result
    }

    override fun register(registerReq: GameValue.RegisterReq): String {
        val clientToken = registerReq.token as EBetClientToken
        val data = hashMapOf(
                "channelId" to clientToken.channelId,
                "username" to registerReq.username,
                "subChannelId" to 0,
                "currency" to clientToken.currency
        )

        this.doStartPost(data = data, clientToken = clientToken, path = "/api/syncuser")

        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val clientToken = balanceReq.token as EBetClientToken
        val data: HashMap<String, Any> = hashMapOf(
                "channelId" to clientToken.channelId,
                "username" to balanceReq.username,
                "currency" to clientToken.currency
        )
        val result = this.doStartPost(data = data, clientToken = clientToken, path = "/api/getusermoney")
        return result.mapUtil.asList("results").first().asBigDecimal("money")
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameValue.TransferResp {

        val clientToken = transferReq.token as EBetClientToken

        val data = hashMapOf(
                "channelId" to clientToken.channelId,
                "username" to transferReq.username,
                "timestamp" to System.currentTimeMillis(),
                "money" to transferReq.amount,
                "rechargeReqId" to transferReq.orderId,
                "currency" to clientToken.currency,
                "typeId" to 0
        )

        val result = this.doStartPost(data = data, clientToken = clientToken, path = "/api/recharge")
        val successful = result.mapUtil.asBigDecimal("money").toDouble() > 0
        return GameValue.TransferResp.of(successful = successful)
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameValue.TransferResp {

        val clientToken = checkTransferReq.token as EBetClientToken
        val data = hashMapOf<String, Any>(
                "channelId" to clientToken.channelId,
                "rechargeReqId" to checkTransferReq.orderId,
                "currency" to clientToken.currency,
                // 为了签名
                "username" to checkTransferReq.orderId
        )

        val result = this.doStartPost(data = data, clientToken = clientToken, path = "/api/rechargestatus")
        val successful = result.mapUtil.asString("rechargeReqId") == checkTransferReq.orderId
        return GameValue.TransferResp.of(successful = successful)
    }

    override fun start(startReq: GameValue.StartReq): String {
        val clientToken = startReq.token as EBetClientToken
        val accessToken = DigestUtils.md5Hex("${startReq.username}:ebet:1:${UUID.randomUUID().toString().replace("-", "")}")
        return "${clientToken.gameUrl}&username=${startReq.username}&accessToken=$accessToken"
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {

        val clientToken = pullBetOrderReq.token as EBetClientToken

        val data = hashMapOf<String, Any>(
                "channelId" to clientToken.channelId,
                "timestamp" to System.currentTimeMillis(),
                "startTimeStr" to pullBetOrderReq.startTime.format(dateTimeFormat),
                "endTimeStr" to pullBetOrderReq.endTime.format(dateTimeFormat)
        )
        val result = this.doStartPost(data = data, clientToken = clientToken, path = "/api/userbethistory")

        return result.mapUtil.asList("betHistories").map { mapUtil ->
            val bet = mapUtil.asBigDecimal("bet")
            val win = mapUtil.asBigDecimal("payout")
            val orderId = mapUtil.asString("roundNo")
            val username = mapUtil.asString("username")

            val betTime = mapUtil.asLong("createTime").div(1000)
                    .let { Instant.ofEpochMilli(it) }
                    .atZone(ZoneId.of("Asia/Shanghai"))
                    .toLocalDateTime()

            val settleTime = mapUtil.asLong("createTime").div(1000)
                    .let { Instant.ofEpochMilli(it) }
                    .atZone(ZoneId.of("Asia/Shanghai"))
                    .toLocalDateTime()


            val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.EBet, platformUsername = username)

            val originData = objectMapper.writeValueAsString(mapUtil.data)
            BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, betAmount = bet, winAmount = win, orderId = orderId, betTime = betTime, settleTime = settleTime,
                    originData = originData, platform = Platform.EBet)
        }
    }

}