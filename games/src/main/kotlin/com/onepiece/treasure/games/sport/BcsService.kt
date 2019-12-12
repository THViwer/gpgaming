package com.onepiece.treasure.games.sport

import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.BcsClientToken
import com.onepiece.treasure.beans.model.token.ClientToken
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.core.PlatformUsernameUtil
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformService
import com.onepiece.treasure.games.bet.DEFAULT_DATETIMEFORMATTER
import com.onepiece.treasure.games.bet.MapUtil
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service
import java.math.BigDecimal

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
class BcsService : PlatformService() {


    override fun getRequestUrl(path: String, data: Map<String, Any>): String {
        val urlParam = data.map { "${it.key}=${it.value}" }.joinToString(separator = "&")
        return "${gameConstant.getDomain(Platform.Bcs)}${path}?$urlParam"
    }

    fun startDoGetXml(url: String): MapUtil {
        val result = okHttpUtil.doGetXml(url = url, clz = BcsValue.Result::class.java)
        check(result.errorCode == "000000") { OnePieceExceptionCode.PLATFORM_DATA_FAIL }

        return MapUtil.instance(result.data)

    }

    override fun register(registerReq: GameValue.RegisterReq): String {

        val token = registerReq.token as BcsClientToken
        val param = mapOf(
                "APIPassword" to token.key,
                "MemberAccount" to registerReq.username,
                "NickName" to registerReq.name,
                "Currency" to "${token.currency}"
        )


        val url = this.getRequestUrl("/ThirdApi.asmx/Register", param)

        this.startDoGetXml(url = url)
        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {

        val token = balanceReq.token as BcsClientToken
        val param = mapOf(
                "APIPassword" to token.key,
                "MemberAccount" to balanceReq.username
        )

        val url = this.getRequestUrl(path = "/ThirdApi.asmx/GetBalance", data = param)
        val mapUtil = this.startDoGetXml(url = url)
        return mapUtil.asMap("result").asBigDecimal("Balance")
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameValue.TransferResp {

        val token = transferReq.token as BcsClientToken
        val transferType = if (transferReq.amount.toDouble() > 0) 0 else 1

        // :MD5(APIPassword+MemberAccount+Amount)
        val sign = DigestUtils.md5Hex("${token.key}${transferReq.username}${transferReq.amount.abs().setScale(4,2)}")
        val signLast6 = sign.substring(sign.length - 6, sign.length)

        val param = mapOf(
                "APIPassword" to token.key,
                "MemberAccount" to transferReq.username,
                "SerialNumber" to transferReq.orderId,
                "Amount" to transferReq.amount.abs(),
                "TransferType" to transferType,
                "Key" to signLast6
        )

        val url = this.getRequestUrl("/ThirdApi.asmx/Transfer", param)
        val mapUtil = this.startDoGetXml(url)
        val platformOrderId =  mapUtil.asMap("result").asString("SerialNumber")
        val balance = mapUtil.asMap("result").asBigDecimal("Balance")
        return GameValue.TransferResp.successful(balance = balance, platformOrderId = platformOrderId)
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameValue.TransferResp {

        val token = checkTransferReq.token as BcsClientToken

        val param = mapOf(
                "APIPassword" to token.key,
                "SerialNumber" to checkTransferReq.orderId
        )

        val url = this.getRequestUrl("/ThirdApi.asmx/CheckTransfer", param)
        return try {
            val mapUtil = this.startDoGetXml(url)
            GameValue.TransferResp.successful()
        } catch (e: Exception) {
            GameValue.TransferResp.failed()
        }
    }

    override fun start(startReq: GameValue.StartReq): String {

        val webType = when (startReq.launch) {
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

        val token = startReq.token as BcsClientToken
        val param = mapOf(
                "APIPassword" to token.key,
                "MemberAccount" to startReq.username,
                "WebType" to webType,
                "Language" to lang,
                "LoginIP" to "127.0.0.1",
                "GameID" to "1", // 1:体育 2:Keno
                "PageStyle" to "SP3" // SP1:TBS SP2:SBO SP3:LBC SP4:HG

        )
        val url = this.getRequestUrl("/ThirdApi.asmx/Login", param)
        val mapUtil = this.startDoGetXml(url)

        return mapUtil.asString("result")
    }

    override fun startDemo(token: ClientToken, language: Language, launch: LaunchMethod): String {
        val webType = when (launch) {
            LaunchMethod.Web -> "PC"
            LaunchMethod.Wap -> "Wap"
            else -> "Smart"
        }

        val lang = when (language) {
            Language.EN -> "EN"
            Language.CN -> "CH"
            Language.VI -> "VN"
            Language.TH -> "TH"
            else -> "EN"
        }

        return "https://sport.ballcrown.com/?WebType=$webType&Language=$lang"

    }

//    override fun queryBetOrder(betOrderReq: GameValue.BetOrderReq): Any {
//
//        val token = betOrderReq.token as BcsClientToken
//
//        val param = mapOf(
//                "APIPassword" to token.key,
//                "MemberAccount" to betOrderReq.username,
//                "Status" to "0",
//                "ReportDate" to "${betOrderReq.startTime.toLocalDate()}"
//        )
//
//        val url = getRequestUrl(path = "/ThirdApi.asmx/GetBetSheetByReport", data = param)
//
//        val betResult = okHttpUtil.doGetXml(url = url, clz = BcsValue.BetResult::class.java)
//        check(betResult.errorCode == "000000") { OnePieceExceptionCode.PLATFORM_DATA_FAIL }
//
//        return betResult.result.betlist
//    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {

        val token = pullBetOrderReq.token as BcsClientToken

        return pullByNextId(clientId = pullBetOrderReq.clientId, platform = Platform.Bcs) { nowId ->

            val param = mapOf(
                    "APIPassword" to token.key,
                    "SortNo" to nowId,
                    "Rows" to 1000
            )

            val url = getRequestUrl(path = "/ThirdApi.asmx/GetBetSheetBySort", data = param)
            val result = okHttpUtil.doGetXml(url = url, clz = BcsValue.PullBetResult::class.java)

            if (result.result.isNullOrEmpty()) {
                nowId to emptyList()
            } else {
                var nextId = "0"
                val orders = result.result.filter { it.data["Status"] == "2" }.map { bet1 ->
                    val bet = bet1.getMapUtil()
                    val orderId = bet.asString("BetID")
                    val username = bet.asString("Account")
                    val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.Bcs, platformUsername = username)
                    val betAmount = bet.asBigDecimal("BetAmount")
                    val winAmount = bet.asBigDecimal("Win")
                    val betTime = bet.asLocalDateTime("BetDate", DEFAULT_DATETIMEFORMATTER)
                    val settleTime = bet.asLocalDateTime("UpdateTime", DEFAULT_DATETIMEFORMATTER)

                    val sortNo = bet.asString("SortNo")
                    if (sortNo > nextId) nextId = sortNo

                    val originData = objectMapper.writeValueAsString(bet.data)
                    BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, platform = Platform.Bcs, orderId = orderId, betAmount = betAmount,
                            winAmount = winAmount, betTime = betTime, settleTime = settleTime, originData = originData)
                }

                nextId to orders
            }
        }

    }


}