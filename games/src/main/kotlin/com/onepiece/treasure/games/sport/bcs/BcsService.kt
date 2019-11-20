package com.onepiece.treasure.games.sport.bcs

import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.DefaultClientToken
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.core.PlatformUsernameUtil
import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformApi
import com.onepiece.treasure.games.bet.DEFAULT_DATETIMEFORMATTER
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 货币：
 * MYR Malysia
 * USD United States
 * RMB China
 * HKD HongKong
 * THB Thailand
 * JPY Japan
 * KHR Korea(1:1000)
 * VND Vietnam(1:1000)
 * IDR Indonisia(1:1000)
 * SGD Singapore
 *
 * 语言：
 * CH 简体中文
 * EN 英文
 * JP 日文
 * KO 韩文
 * TH 泰文
 * TW 繁体中文
 * VN 越南文
 */
@Service
class BcsService : PlatformApi() {


    override fun getRequestUrl(path: String, data: Map<String, Any>): String {
        val urlParam = data.map { "${it.key}=${it.value}" }.joinToString(separator = "&")
        return "${GameConstant.BCS_API_URL}${path}?$urlParam"
    }

    override fun register(registerReq: GameValue.RegisterReq): String {

        val token = registerReq.token as DefaultClientToken
        val param = mapOf(
                "APIPassword" to token.key,
                "MemberAccount" to registerReq.username,
                "NickName" to registerReq.name,
                "Currency" to "MRY"
        )


        val url = this.getRequestUrl("/ThirdApi.asmx/ThirdApi.asmx/Register", param)

        val result = okHttpUtil.doGetXml(url = url, clz = BcsResult::class.java)
        //TODO check


        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {

        val token = balanceReq.token as DefaultClientToken
        val param = mapOf(
                "APIPassword" to token.key,
                "MemberAccount" to balanceReq.username
        )

        val url = this.getRequestUrl(path = "/ThirdApi.asmx/ThirdApi.asmx/GetBalance", data = param)
        val result = okHttpUtil.doGetXml(url = url, clz = BcsResult::class.java)
        //TODO check

        val map = result.data["result"] as Map<String, Any>
        return map["Balance"]?.toString()?.toBigDecimal()?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {

        val token = transferReq.token as DefaultClientToken
        val transferType = if (transferReq.amount.toDouble() > 0) 0 else 1

        // MD5(APIPassword+MemberAccount+Amount)取

        val sign = DigestUtils.md5Hex("${token.key}${transferReq.username}${transferReq.amount.setScale(2,4)}")
        val signLast6 = sign.substring(sign.length - 6, sign.length)

        val param = mapOf(
                "APIPassword" to token.key,
                "MemberAccount" to transferReq.username,
                "SerialNumber" to transferReq.orderId,
                "Amount" to transferReq.amount.abs(),
                "TransferType" to transferType,
                "Key" to signLast6
        )

        val url = this.getRequestUrl("/ThirdApi.asmx/ThirdApi.asmx/Transfer", param)

        val result = okHttpUtil.doGet(url = url, clz = BcsResult::class.java)
        //TODO check

        val map = result.data["result"] as Map<String, Any>
        return map["SerialNumber"]?.toString()?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): Boolean {

        val token = checkTransferReq.token as DefaultClientToken

        val param = mapOf(
                "APIPassword" to token.key,
                "SerialNumber" to checkTransferReq.orderId
        )

        val url = this.getRequestUrl("/ThirdApi.asmx/ThirdApi.asmx/CheckTransfer", param)

        val result = okHttpUtil.doGetXml(url = url, clz = BcsResult::class.java)
        //TODO check

        val state = result.data["result"]


        error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)

    }

    override fun start(startReq: GameValue.StartReq): String {

        val webType = when (startReq.startPlatform) {
            LaunchMethod.Web -> "PC"
            LaunchMethod.Wap -> "Wap"
            else -> "Smart"
        }

        val lang = when (startReq.language) {
            Language.EN -> "EN"
            Language.CN -> "CH"
            Language.VI -> "VN"
            Language.TH -> "TH"
            else -> "EN"
        }

        val token = startReq.token as DefaultClientToken
        val param = mapOf(
                "APIPassword" to token.key,
                "MemberAccount" to startReq.username,
                "WebType" to webType,
                "Language" to lang

        )

        val url = this.getRequestUrl("/ThirdApi.asmx/ThirdApi.asmx/Login", param)

        val result = okHttpUtil.doGetXml(url = url, clz = BcsResult::class.java)
        //TODO check

        return result.data["result"]?.toString()?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
    }

    override fun queryBetOrder(betOrderReq: GameValue.BetOrderReq): Any {

        val token = betOrderReq.token as DefaultClientToken

        val param = mapOf(
                "APIPassword" to token.key,
                "MemberAccount" to betOrderReq.username,
                "Status" to "0",
                "ReportDate" to "${betOrderReq.startTime.toLocalDate()}"
        )

        val url = getRequestUrl(path = "/GetBetSheetByReport", data = param)
        val result = okHttpUtil.doGetXml(url = url, clz = BcsResult::class.java)

        val data = (result.data["result"] as Map<*, *>)["betlist"] as List<*>
        return objectMapper.writeValueAsString(data)
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {

        val token = pullBetOrderReq.token as DefaultClientToken

        return pullByNextId(clientId = pullBetOrderReq.clientId, platform = Platform.Bcs) { nowId ->

            val param = mapOf(
                    "APIPassword" to token.key,
                    "AgentID" to token.appId,
                    "SortNo" to nowId,
                    "Rows" to 1000
            )

            val url = getRequestUrl(path = "/GetBetSheetByAgent", data = param)

            val result = okHttpUtil.doGetXml(url = url, clz = BcsResult::class.java)

            val list = result.data["result"] as List<Map<String, Any>>

            if (list.isEmpty()) {
                nowId to emptyList()
            } else {
                var nextId = ""
                val orders = list.map { bet ->
                    val orderId = bet["BetID"]?.toString()?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
                    val username = bet["Account"]?.toString()?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
                    val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.Bcs, platformUsername = username)
                    val betAmount = bet["BetAmount"]?.toString()?.toBigDecimal()?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
                    val winAmount = bet["Win"]?.toString()?.toBigDecimal()?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
                    val betTime = bet["BetDate"]?.toString()?.let { LocalDateTime.parse(it, DEFAULT_DATETIMEFORMATTER) }?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
                    val settleTime = bet["UpdateTime"]?.toString()?.let { LocalDateTime.parse(it, DEFAULT_DATETIMEFORMATTER) }?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)

                    val sortNo = bet["SortNo"]?.toString()?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
                    if (sortNo > nextId) nextId = sortNo

                    val originData = objectMapper.writeValueAsString(bet)
                    BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, platform = Platform.Bcs, orderId = orderId, betAmount = betAmount,
                            winAmount = winAmount, betTime = betTime, settleTime = settleTime, originData = originData)
                }

                nextId to orders
            }
        }

    }


}