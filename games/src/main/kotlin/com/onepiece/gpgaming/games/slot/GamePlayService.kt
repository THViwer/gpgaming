package com.onepiece.gpgaming.games.slot

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.token.GamePlayClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.bet.MapUtil
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
//        log.info("result: $result")
        return result.mapUtil
    }

    fun startGetBetXml(url: String, data: List<String>): GamePlayValue.BetResult {
        val urlParam = data.joinToString("&")
        val result = okHttpUtil.doGetXml(url = "$url?$urlParam", clz = GamePlayValue.BetResult::class.java)

//        check(result.error_code == 0) { OnePieceExceptionCode.PLATFORM_DATA_FAIL }
//        log.info("result: $result")
//        return result.mapUtil
        return result
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
            Language.EN -> "en-us"
            else -> "en-us"
        }

        val urlParam = listOf(
                "token=test",
                "fun=1",
                "op=${clientToken.merchId}",
                "lang=${lang}",
                "lobbyURL=${startSlotReq.redirectUrl}",
                "fundsURL=${startSlotReq.redirectUrl}"
        ).joinToString(separator = "&")


        return "http://rslots.gpiuat.com/${startSlotReq.gameId}?$urlParam"

//        val urlParam = "token=test&op=${clientToken.merchId}&lang=$lang&homeURL=${startSlotReq.redirectUrl}&sys=CUSTOM"
//        val url = when (startSlotReq.launchMethod) {
//            LaunchMethod.Wap -> "http://casino.w88uat.com/v2/html5/mobile"
//            LaunchMethod.Web -> "http://casino.w88uat.com/v2"
//            else -> error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
//        }
//        return "$url?$urlParam"
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

    // 启动游戏需要白名单 193.110.203.190 香港 CN2 01 - 443 单端口
    override fun startSlot(startSlotReq: GameValue.StartSlotReq): String {
        val  clientToken = startSlotReq.token as GamePlayClientToken

        val token = this.getTicket(startSlotReq)

        val lang = when (startSlotReq.language) {
            Language.CN -> "zh-cn"
            Language.ID -> "id-id"
            Language.TH -> "th-th"
            Language.VI -> "vi-vn"
            Language.EN -> "en-us"
            else -> "en-us"
        }

        val urlParam = listOf(
                "token=$token",
                "fun=0",
                "op=${clientToken.merchId}",
                "lang=${lang}",
                "lobbyURL=${startSlotReq.redirectUrl}",
                "fundsURL=${startSlotReq.redirectUrl}"
        ).joinToString(separator = "&")


        return "http://rslots.gpiuat.com/${startSlotReq.gameId}?$urlParam"
//        val urlParam = "token=$ticket&op=${clientToken.merchId}&lang=$lang&homeURL=${startSlotReq.redirectUrl}&sys=CUSTOM"
//        val url = when (startSlotReq.launchMethod) {
//            LaunchMethod.Wap -> "http://casino.w88uat.com/v2/html5/mobile"
//            LaunchMethod.Web -> "http://casino.w88uat.com/v2"
//            else -> error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
//        }
//        return "$url?$urlParam"
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {

        val clientToken = pullBetOrderReq.token as GamePlayClientToken


        val data = listOf(
                "merch_id=${clientToken.merchId}",
                "merch_pwd=${clientToken.merchPwd}",
                "date_from=${pullBetOrderReq.startTime.format(dateTimeFormat)}",
                "date_to=${pullBetOrderReq.endTime.format(dateTimeFormat)}",
                "page_num=1",
                "page_size=2000",
                "product=slots"
        )
        val result = this.startGetBetXml(url = "${gameConstant.getOrderApiUrl(Platform.GamePlay)}/csnbo/api/gateway/betDetail.html", data = data)

        return result.betDetailList?.map { bet ->
            val mapUtil = bet.mapUtil
            val username = mapUtil.asString("user_id")
            val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.GamePlay, platformUsername = username)
            val orderId = mapUtil.asString("bet_id")
            val betAmount = mapUtil.asBigDecimal("bet")
            val winLose = mapUtil.asBigDecimal("winlose")
            val betTime = mapUtil.asLocalDateTime("trans_date", dateTimeFormat)

            val originData = jacksonObjectMapper().writeValueAsString(mapUtil.data)

            BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, platform = Platform.GamePlay, orderId = orderId, betAmount = betAmount,
                    winAmount = betAmount.plus(winLose), betTime = betTime, settleTime = betTime, originData = originData)
        }?: emptyList()

    }

}


/*
fun main() {
    val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    val xml = "<resp>\n" +
            "<error_code>0</error_code>\n" +
            "<items page_num=\"1\" page_size=\"2000\" total_row=\"5\" total_page=\"1\">\n" +
            "<item winlose=\"-0.50\" bet_id=\"47403073\" bundle_id=\"47403073\" trans_date=\"2019-12-20 22:52:59\" user_id=\"01000001jk\" game_type=\"1\" balance=\"0.00\" bet=\"0.50\" status=\"1\" game_result=\"\" currency=\"\" player_hand=\"Spin\" table_id=\"Wuxia Princess Mega Reels\" game_id=\"1\" lucky_num=\"\" platform=\"4\" round_id=\"\" game_code=\"\" fround=\"0\" jcon=\"0\" jwin=\"0.0\" rebate_amount=\"\"/>\n" +
            "<item winlose=\"-0.50\" bet_id=\"47403074\" bundle_id=\"47403074\" trans_date=\"2019-12-20 22:53:03\" user_id=\"01000001jk\" game_type=\"1\" balance=\"0.00\" bet=\"0.50\" status=\"1\" game_result=\"\" currency=\"\" player_hand=\"Spin\" table_id=\"Wuxia Princess Mega Reels\" game_id=\"1\" lucky_num=\"\" platform=\"4\" round_id=\"\" game_code=\"\" fround=\"0\" jcon=\"0\" jwin=\"0.0\" rebate_amount=\"\"/>\n" +
            "<item winlose=\"-0.42\" bet_id=\"47403075\" bundle_id=\"47403075\" trans_date=\"2019-12-20 22:53:06\" user_id=\"01000001jk\" game_type=\"1\" balance=\"0.00\" bet=\"0.50\" status=\"1\" game_result=\"\" currency=\"\" player_hand=\"Spin\" table_id=\"Wuxia Princess Mega Reels\" game_id=\"1\" lucky_num=\"\" platform=\"4\" round_id=\"\" game_code=\"\" fround=\"0\" jcon=\"0\" jwin=\"0.0\" rebate_amount=\"\"/>\n" +
            "<item winlose=\"-0.50\" bet_id=\"47403076\" bundle_id=\"47403076\" trans_date=\"2019-12-20 22:53:10\" user_id=\"01000001jk\" game_type=\"1\" balance=\"0.00\" bet=\"0.50\" status=\"1\" game_result=\"\" currency=\"\" player_hand=\"Spin\" table_id=\"Wuxia Princess Mega Reels\" game_id=\"1\" lucky_num=\"\" platform=\"4\" round_id=\"\" game_code=\"\" fround=\"0\" jcon=\"0\" jwin=\"0.0\" rebate_amount=\"\"/>\n" +
            "<item winlose=\"-0.50\" bet_id=\"47403077\" bundle_id=\"47403077\" trans_date=\"2019-12-20 22:53:13\" user_id=\"01000001jk\" game_type=\"1\" balance=\"0.00\" bet=\"0.50\" status=\"1\" game_result=\"\" currency=\"\" player_hand=\"Spin\" table_id=\"Wuxia Princess Mega Reels\" game_id=\"1\" lucky_num=\"\" platform=\"4\" round_id=\"\" game_code=\"\" fround=\"0\" jcon=\"0\" jwin=\"0.0\" rebate_amount=\"\"/>\n" +
            "</items>\n" +
            "</resp>"

    val result = XmlMapper().readValue(xml, GamePlayValue.BetResult::class.java)

    val orders = result.betDetailList?.map { bet ->
        val mapUtil = bet.mapUtil
        val username = mapUtil.asString("user_id")
        val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.GamePlay, platformUsername = username)
        val orderId = mapUtil.asString("bet_id")
        val betAmount = mapUtil.asBigDecimal("bet")
        val winLose = mapUtil.asBigDecimal("winlose")
        val betTime = mapUtil.asLocalDateTime("trans_date", dateTimeFormat)

        val originData = jacksonObjectMapper().writeValueAsString(mapUtil.data)

        BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, platform = Platform.GamePlay, orderId = orderId, betAmount = betAmount,
                winAmount = betAmount.plus(winLose), betTime = betTime, settleTime = betTime, originData = originData)
    }?: emptyList()

    println(orders)

}
*/


