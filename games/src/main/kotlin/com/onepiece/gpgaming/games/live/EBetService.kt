package com.onepiece.gpgaming.games.live

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.U9RequestStatus
import com.onepiece.gpgaming.beans.model.token.EBetClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.utils.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.http.GameResponse
import com.onepiece.gpgaming.games.http.OKParam
import com.onepiece.gpgaming.games.http.OKResponse
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class EBetService : PlatformService() {

    private val log = LoggerFactory.getLogger(EBetService::class.java)
    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    private fun doPost(data: HashMap<String, Any>, clientToken: EBetClientToken, path: String): OKResponse {

        val username = data["username"]?.toString() ?: ""
        val timestamp = data["timestamp"]?.toString() ?: ""
        val signKey = "$username${timestamp}"
        val sign = EBetSignUtil.sign(signKey, clientToken.privateKey)
        data["signature"] = sign

        val url = "${clientToken.apiPath}$path"

        val param = objectMapper.writeValueAsString(data)
        val okParam = OKParam.ofPost(url = url, param = param)

        val okResponse = u9HttpRequest.startRequest(okParam)
        if (!okResponse.ok) return okResponse

        val status = try {
            when (okResponse.asString("status")) {
                "200" -> U9RequestStatus.OK
                else -> U9RequestStatus.Fail
            }
        } catch (e: Exception) {
            U9RequestStatus.Fail
        }
        return okResponse.copy(status = status)
    }

    override fun register(registerReq: GameValue.RegisterReq): GameResponse<String> {
        val clientToken = registerReq.token as EBetClientToken
        val data = hashMapOf<String, Any>(
                "channelId" to clientToken.channelId,
                "username" to registerReq.username,
                "subChannelId" to 0,
                "currency" to clientToken.currency
        )

        val okResponse = this.doPost(data = data, clientToken = clientToken, path = "/api/syncuser")
        return this.bindGameResponse(okResponse = okResponse) {
            registerReq.username
        }
    }

    override fun balance(balanceReq: GameValue.BalanceReq): GameResponse<BigDecimal> {
        val clientToken = balanceReq.token as EBetClientToken
        val data: HashMap<String, Any> = hashMapOf(
                "channelId" to clientToken.channelId,
                "username" to balanceReq.username,
                "currency" to clientToken.currency
        )
        val okResponse = this.doPost(data = data, clientToken = clientToken, path = "/api/getusermoney")
        return this.bindGameResponse(okResponse) {
            it.asList("results").first().asBigDecimal("money")
        }
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameResponse<GameValue.TransferResp> {

        val clientToken = transferReq.token as EBetClientToken

        val data = hashMapOf<String, Any>(
                "channelId" to clientToken.channelId,
                "username" to transferReq.username,
                "timestamp" to System.currentTimeMillis(),
                "money" to transferReq.amount,
                "rechargeReqId" to transferReq.orderId,
                "currency" to clientToken.currency,
                "typeId" to 0
        )

        val okResponse = this.doPost(data = data, clientToken = clientToken, path = "/api/recharge")
        return this.bindGameResponse(okResponse = okResponse) {
            val successful = it.asBigDecimal("money").toDouble() > 0
            GameValue.TransferResp.of(successful = successful)
        }
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameResponse<GameValue.TransferResp> {

        val clientToken = checkTransferReq.token as EBetClientToken
        val data = hashMapOf<String, Any>(
                "channelId" to clientToken.channelId,
                "rechargeReqId" to checkTransferReq.orderId,
                "currency" to clientToken.currency,
                // 为了签名
                "username" to checkTransferReq.orderId
        )

        val okResponse = this.doPost(data = data, clientToken = clientToken, path = "/api/rechargestatus")
        return this.bindGameResponse(okResponse = okResponse) {
            val successful = it.asString("rechargeReqId") == checkTransferReq.orderId
            GameValue.TransferResp.of(successful = successful)
        }
    }

    override fun start(startReq: GameValue.StartReq): GameResponse<String> {
        val clientToken = startReq.token as EBetClientToken

        val language = when (startReq.language) {
            Language.CN -> "zh_cn"
            Language.MY -> "ms_my"
            Language.ID -> "in_id"
            Language.TH -> "th_th"
            Language.VI -> "vi_vn"
            else -> "en_us"
        }

//        val orientation = if (startReq.launch == LaunchMethod.Wap) "&orientation=1" else ""

        val accessToken = DigestUtils.md5Hex("${startReq.username}:ebet:1:${UUID.randomUUID().toString().replace("-", "")}")
        val path = "${clientToken.gameUrl}&username=${startReq.username}&accessToken=$accessToken&language=$language&gameType=0,1,2,7"
        return GameResponse.of(data = path)
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): GameResponse<List<BetOrderValue.BetOrderCo>> {

        val clientToken = pullBetOrderReq.token as EBetClientToken

        val data = hashMapOf<String, Any>(
                "channelId" to clientToken.channelId,
                "timestamp" to System.currentTimeMillis(),
                "startTimeStr" to pullBetOrderReq.startTime.format(dateTimeFormat),
                "endTimeStr" to pullBetOrderReq.endTime.format(dateTimeFormat)
        )
        val okResponse = this.doPost(data = data, clientToken = clientToken, path = "/api/userbethistory")
        return this.bindGameResponse(okResponse = okResponse) {
            it.asList("betHistories").map { mapUtil ->
                val bet = mapUtil.asBigDecimal("bet")
                val validBet = mapUtil.asBigDecimal("validBet")
                val payout = mapUtil.asBigDecimal("payout")
                val orderId = mapUtil.asString("roundNo")
                val username = mapUtil.asString("username")

                val betTime = (mapUtil.asLong("createTime") * 1000)
                        .let { Instant.ofEpochMilli(it) }
                        .atZone(ZoneId.of("Asia/Shanghai"))
                        .toLocalDateTime()

                val settleTime = betTime


                val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.EBet, platformUsername = username)

                val originData = objectMapper.writeValueAsString(mapUtil.data)
                BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, betAmount = bet, payout = payout, orderId = orderId, betTime = betTime, settleTime = settleTime,
                        originData = originData, platform = Platform.EBet, validAmount = validBet)
            }
        }


    }

}

fun main() {

    val t = 1599635054L * 1000
    val d = t.let { b -> Instant.ofEpochMilli(b) }
            .atZone(ZoneId.of("Asia/Shanghai"))
            .toLocalDateTime()

    println(d)

}