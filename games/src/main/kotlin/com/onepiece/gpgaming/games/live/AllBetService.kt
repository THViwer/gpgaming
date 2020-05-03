package com.onepiece.gpgaming.games.live

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.token.AllBetClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.bet.DesUtil
import com.onepiece.gpgaming.games.bet.MapUtil
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.net.URLEncoder
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class AllBetService : PlatformService() {

    private val log = LoggerFactory.getLogger(AllBetService::class.java)
    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")


    fun startDoGet(method: String, urlParam: String, allBetClientToken: AllBetClientToken): MapUtil {

        val desData =  DesUtil.encrypt(urlParam, allBetClientToken.desKey, null)
        val md5Data = Base64.encodeBase64String(DigestUtils.md5("$desData${allBetClientToken.md5Key}"))
        val param = "propertyId=${allBetClientToken.propertyId}&data=${URLEncoder.encode(desData, "UTF-8")}&sign=${URLEncoder.encode(md5Data, "UTF-8")}&${urlParam}"

        val result = okHttpUtil.doGet(platform = Platform.AllBet, url = "http://193.200.134.198:1001${method}?$param", clz = AllBetValue.Result::class.java)
//        val result = okHttpUtil.doGet(platform = Platform.AllBet, url = "${allBetClientToken.apiPath}${method}?$param", clz = AllBetValue.Result::class.java)
        check(result.errorCode == "OK") {
            log.error("allBet network error: ${result.errorCode}, ${result.message}")
            OnePieceExceptionCode.PLATFORM_DATA_FAIL
        }
        return result.mapUtil
    }

//    private fun queryHandicap(allBetClientToken: AllBetClientToken): Pair<String, String> {
//
//        val urlParam = "agent=${allBetClientToken.agentName}&random=${UUID.randomUUID()}"
//        val mapUtil = this.startDoGet(method = "/query_handicap", urlParam = urlParam, allBetClientToken = allBetClientToken)
//        val handicaps = mapUtil.asList("handicaps")
//
//        var orHandicapNames = ""
//        var vipHandicapNames = ""
//        handicaps.forEach {
//
//            when (it.asInt("handicapType")) {
//                0 -> orHandicapNames = it.asString("name")
//                1 -> vipHandicapNames = it.asString("name")
//            }
//        }
//        return orHandicapNames to vipHandicapNames
//
//    }

    override fun register(registerReq: GameValue.RegisterReq): String {
        val allBetClientToken = registerReq.token as AllBetClientToken

        // 查询盘口
//        val (orHallRebate, vipHandicapNames) = this.queryHandicap(allBetClientToken)

        val data = listOf(
                "agent=${allBetClientToken.agentName}",
                "client=${registerReq.username}",
                "password=${registerReq.password}",
                "orHandicapNames=${allBetClientToken.orHandicapNames}",
                "vipHandicapNames=${allBetClientToken.vipHandicapNames}",
                "orHallRebate=0",
//                "vipHandicaps=12",
//                "orHandicaps=1",
//                "orHallRebate=0.5",
                "random=${UUID.randomUUID()}"
        )
        val urlParam = data.joinToString(separator = "&")

        val mapUtil = this.startDoGet(method = "/check_or_create", urlParam = urlParam, allBetClientToken = allBetClientToken )
        return mapUtil.asString("client")
    }

    override fun updatePassword(updatePasswordReq: GameValue.UpdatePasswordReq) {

        val allBetClientToken = updatePasswordReq.token as AllBetClientToken

        val data = listOf(
                "client=${updatePasswordReq.username}",
                "newPassword=${updatePasswordReq.password}",
                "random=${UUID.randomUUID()}"
        )
        val urlParam = data.joinToString(separator = "&")

        this.startDoGet(method = "/setup_client_password", urlParam = urlParam, allBetClientToken = allBetClientToken )

    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val allBetClientToken = balanceReq.token as AllBetClientToken

        val data = listOf(
                "client=${balanceReq.username}",
                "password=${balanceReq.password}",
                "random=${UUID.randomUUID()}"
        )
        val urlParam = data.joinToString(separator = "&")
        val mapUtil = this.startDoGet(method = "/get_balance", urlParam = urlParam, allBetClientToken = allBetClientToken)
        return mapUtil.asBigDecimal("balance")
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameValue.TransferResp {
        val allBetClientToken = transferReq.token as AllBetClientToken

        val operFlag = if (transferReq.amount.toDouble() > 0) 1 else 0

        val allBetOrderId = "${allBetClientToken.propertyId}${transferReq.orderId}"
        val data = listOf(
                "agent=${allBetClientToken.agentName}",
                "sn=${allBetOrderId}",
                "client=${transferReq.username}",
                "operFlag=${operFlag}",
                "credit=${transferReq.amount.abs()}",
                "random=${UUID.randomUUID()}"
        )
        val urlParam = data.joinToString("&")
        this.startDoGet(method = "/agent_client_transfer", urlParam = urlParam, allBetClientToken = allBetClientToken)
        return GameValue.TransferResp.successful(platformOrderId = allBetOrderId)
    }


    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameValue.TransferResp {
        val allBetClientToken = checkTransferReq.token as AllBetClientToken

        // query_transfer_state
        val data = listOf(
                "sn=${checkTransferReq.platformOrderId}",
                "random=${UUID.randomUUID()}"
        )
        val urlParam = data.joinToString(separator = "&")
        val mapUtil = this.startDoGet(method = "/query_transfer_state", urlParam = urlParam, allBetClientToken = allBetClientToken)

        // 0 创建状态 1 成功 2 失败
        val successful = mapUtil.asInt("transferState") == 1
        return GameValue.TransferResp.of(successful)
    }

    override fun start(startReq: GameValue.StartReq): String {

        /**
         * 中文简体	zh_CN
         * 中文繁体	zh_TW
         * 英文	en
         * 韩语	ko
         * 泰文	th
         * 马来文	ms	H5不支持
         * 越南文	vi	H5不支持
         * 西班牙语	es-es	H5不支持
         * 日语	ja
         * 俄罗斯语	ru	H5不支持
         * 印度尼西亚语	id	H5不支持
         */
        val language = when(startReq.language) {
            Language.TH -> "th"
            Language.VI -> "vi"
            Language.CN -> "zh_CN"
            Language.EN -> "en"
            Language.MY -> "ms"
            Language.ID -> "id"
            else -> "en"
        }


        val allBetClientToken = startReq.token as AllBetClientToken
        val data = listOf(
                "client=${startReq.username}",
                "password=${startReq.password}",
                "language=$language",
                "random=${UUID.randomUUID()}"
        )
        val urlParam = data.joinToString(separator = "&")
        val mapUtil = this.startDoGet(method = "/forward_game", urlParam = urlParam, allBetClientToken = allBetClientToken)

        return mapUtil.asString("gameLoginUrl")
    }

//    override fun queryBetOrder(betOrderReq: GameValue.BetOrderReq): Any {
//        val allBetClientToken = betOrderReq.token as AllBetClientToken
//        val data = listOf(
//                "client=${betOrderReq.username}",
//                "startTime=${betOrderReq.startTime.format(dateTimeFormat)}",
//                "endTime=${betOrderReq.endTime.format(dateTimeFormat)}",
//                "pageIndex=1",
//                "pageSize=100",
//                "random=${UUID.randomUUID()}"
//        )
//        val urlParam = data.joinToString(separator = "&")
//        val mapUtil = this.startDoGet(method = "/client_betlog_query", urlParam = urlParam, allBetClientToken = allBetClientToken)
//
//        return mapUtil.asMap("page").asList("datas").map { it.data }
//    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {

        val allBetClientToken = pullBetOrderReq.token as AllBetClientToken
        val data = listOf(
                "client=${allBetClientToken.agentName}",
//                "egameType=af",
                "startTime=${pullBetOrderReq.startTime.format(dateTimeFormat)}",
                "endTime=${pullBetOrderReq.endTime.format(dateTimeFormat)}",
//                "pageIndex=1",
//                "pageSize=1000",
                "random=${UUID.randomUUID()}"
        )
        val urlParam = data.joinToString(separator = "&")
        val mapUtil = this.startDoGet(method = "/betlog_pieceof_histories_in30days", urlParam = urlParam, allBetClientToken = allBetClientToken)

        val page = mapUtil.asList("histories")
        return page.map { bet ->
            val orderId = bet.asString("betNum")
            val username = bet.asString("client")
            val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.AllBet, platformUsername = username)
            val betAmount = bet.asBigDecimal("betAmount")
            val winOrLoss = bet.asBigDecimal("winOrLoss")
            val winAmount = betAmount.plus(winOrLoss)
            val betTime = bet.asLocalDateTime("gameRoundStartTime", dateTimeFormat)
            val settleTime = bet.asLocalDateTime("gameRoundEndTime", dateTimeFormat)

            val originData = objectMapper.writeValueAsString(bet.data)
            BetOrderValue.BetOrderCo(orderId = orderId, clientId = clientId, memberId = memberId, betAmount = betAmount, winAmount = winAmount, betTime = betTime,
                    settleTime = settleTime, platform = Platform.AllBet, originData = originData)
        }
    }

}