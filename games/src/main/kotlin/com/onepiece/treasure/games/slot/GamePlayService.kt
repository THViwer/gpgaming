package com.onepiece.treasure.games.slot

import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.GamePlayClientToken
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformService
import com.onepiece.treasure.games.bet.MapUtil
import org.apache.commons.codec.binary.Base64
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.net.URLEncoder
import java.time.format.DateTimeFormatter


@Service
class GamePlayService: PlatformService() {

    private val log = LoggerFactory.getLogger(GamePlayService::class.java)
    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun startGetXml(method: String, data: List<String>): MapUtil {
        val urlParam = data.joinToString("&")
        val result = okHttpUtil.doGetXml(url = "${gameConstant.getDomain(Platform.GamePlay)}${method}?$urlParam", clz = GamePlayValue.Result::class.java)

        check(result.error_code == 0) { OnePieceExceptionCode.PLATFORM_DATA_FAIL }
        log.info("result: $result")
        return result.mapUtil
    }

    override fun register(registerReq: GameValue.RegisterReq): String {
        val clientToken = registerReq.token as GamePlayClientToken
        val data = listOf(
                "merch_id=${clientToken.merchId}",
                "merch_pwd=${clientToken.merchPwd}",
                "cust_id=${registerReq.username}",
                "cust_name=${registerReq.name}",
                "currency=${clientToken.currency}"
        )

        this.startGetXml(method = "/op/createuser", data = data)
        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val clientToken = balanceReq.token as GamePlayClientToken
        val data = listOf(
                "merch_id=${clientToken.merchId}",
                "merch_pwd=${clientToken.merchPwd}",
                "cust_id=${balanceReq.username}",
                "currency=${clientToken.currency}"
        )
        val mapUtil = this.startGetXml(method = "/op/getbalance", data = data)

        return mapUtil.asBigDecimal("balance")
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameValue.TransferResp {
        val clientToken = transferReq.token as GamePlayClientToken
        val method = if (transferReq.amount.toDouble() > 0) "/op/credit" else "/op/debit"
        val data = listOf(
                "merch_id=${clientToken.merchId}",
                "merch_pwd=${clientToken.merchPwd}",
                "cust_id=${transferReq.username}",
                "currency=${clientToken.currency}",
                "amount=${transferReq.amount.abs()}",
                "trx_id=${transferReq.orderId}"
        )

        val mapUtil = this.startGetXml(method = method, data = data)
        val balance = mapUtil.asBigDecimal("after")
        return GameValue.TransferResp.successful(balance = balance)
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameValue.TransferResp {

        val clientToken = checkTransferReq.token as GamePlayClientToken

        val data = listOf(
                "merch_id=${clientToken.merchId}",
                "merch_pwd=${clientToken.merchPwd}",
                "trx_id=${checkTransferReq.orderId}"
        )
        val mapUtil = this.startGetXml(method = "/op/check", data = data)
        val balance = mapUtil.asBigDecimal("after")
        return GameValue.TransferResp.successful(balance = balance)
    }

    override fun startSlotDemo(startSlotReq: GameValue.StartSlotReq): String {

        val  clientToken = startSlotReq.token as GamePlayClientToken


        val lang = when (startSlotReq.language) {
            Language.CN -> "zh-cn"
            Language.ID -> "id-id"
            Language.TH -> "th-th"
            Language.VI -> "vi-vn"
            Language.EN -> "km-kh"
            else -> "km-kh"
        }

        val urlParam = "token=test&op=${clientToken.merchId}&lang=$lang&homeURL=${startSlotReq.redirectUrl}&sys=CUSTOM"
        val url = when (startSlotReq.launchMethod) {
            LaunchMethod.Wap -> "http://casino.w88uat.com/html5/mobile"
            LaunchMethod.Web -> "http://casino.w88uat.com"
            else -> error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
        }
        return "$url?$urlParam"
    }

    private fun getTicket(startSlotReq: GameValue.StartSlotReq): String {
//        val  clientToken = startSlotReq.token as GamePlayClientToken
//        val data = listOf(
//                "merch_id=${clientToken.merchId}",
//                "merch_pwd=${clientToken.merchPwd}",
//                "cust_id=${startSlotReq.username}",
//                "cust_name=${startSlotReq.username}",
//                "currency=${clientToken.currency}"
//        )
//
//        val mapUtil = this.startGetXml(method = "/op/createuser", data = data)
//        return ""

        val key = "${startSlotReq.username}:${startSlotReq.gameId}"

        return URLEncoder.encode(Base64.encodeBase64String(key.toByteArray()), "utf-8")

    }

    // 启动游戏需要白名单 193.110.203.190
    override fun startSlot(startSlotReq: GameValue.StartSlotReq): String {
        val  clientToken = startSlotReq.token as GamePlayClientToken

        val ticket = this.getTicket(startSlotReq)

        val lang = when (startSlotReq.language) {
            Language.CN -> "zh-cn"
            Language.ID -> "id-id"
            Language.TH -> "th-th"
            Language.VI -> "vi-vn"
            Language.EN -> "km-kh"
            else -> "km-kh"
        }

        val urlParam = "token=$ticket&op=${clientToken.merchId}&lang=$lang&homeURL=${startSlotReq.redirectUrl}&sys=CUSTOM"
        val url = when (startSlotReq.launchMethod) {
            LaunchMethod.Wap -> "http://casino.w88uat.com/v2/html5/mobile"
            LaunchMethod.Web -> "http://casino.w88uat.com/v2"
            else -> error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
        }
        return "$url?$urlParam"
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {

        val clientToken = pullBetOrderReq.token as GamePlayClientToken


        val data = listOf(
                "merch_id=${clientToken.merchId}",
                "merch_pwd=${clientToken.merchPwd}",
                "date_from=${pullBetOrderReq.startTime.format(dateTimeFormat)}",
                "date_to=${pullBetOrderReq.endTime.format(dateTimeFormat)}",
                "page_num=1",
                "page_size=2000"
        )
        val mapUtil = this.startGetXml(method = "/api/gateway/betDetail.html", data = data)

        //TODO 处理订单
        return emptyList()
    }

}

