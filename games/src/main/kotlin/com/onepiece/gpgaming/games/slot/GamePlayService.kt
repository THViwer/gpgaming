package com.onepiece.gpgaming.games.slot

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.token.GamePlayClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.utils.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.http.GameResponse
import com.onepiece.gpgaming.games.http.OKParam
import com.onepiece.gpgaming.games.http.OKResponse
import org.apache.commons.codec.binary.Base64
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.net.URLEncoder
import java.time.format.DateTimeFormatter


@Service
class GamePlayService : PlatformService() {

    private val log = LoggerFactory.getLogger(GamePlayService::class.java)
    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun startGetXml(clientToken: GamePlayClientToken, method: String, data: List<String>): OKResponse {

        val urlParam = data.joinToString("&")
        val url = "${clientToken.apiPath}${method}?$urlParam".let {
            URLEncoder.encode(it, "UTF-8")
        }

        val okParam = OKParam.ofGetXml(url = "https://proxy.u996.com/api/v1/proxy/get/xml", param = "path=${url}")
        val okResponse = u9HttpRequest.startRequest(okParam = okParam)

        if (!okResponse.ok) return okResponse

        val ok = try {
            val errorCode = okResponse.asInt("error_code")
            errorCode == 0
        } catch (e: Exception) {
            false
        }
        return okResponse.copy(ok = ok)
    }

    fun startGetBetXml(clientToken: GamePlayClientToken, method: String, data: List<String>): OKResponse {

        val urlParam = data.joinToString("&")
        val url = "${clientToken.apiOrderPath}${method}?$urlParam".let {
            URLEncoder.encode(it, "UTF-8")
        }

        val okParam = OKParam.ofGetXml(url = "https://proxy.u996.com/api/v1/proxy/get/xml", param = "path=${url}")
        val okResponse = u9HttpRequest.startRequest(okParam = okParam)

        if (!okResponse.ok) return okResponse

        val ok = try {
            val errorCode = okResponse.asInt("error_code")
            errorCode == 0
        } catch (e: Exception) {
            false
        }
        return okResponse.copy(ok = ok)
//        val urlParam = data.joinToString("&")
//        val result = okHttpUtil.doGetXml(platform = Platform.GamePlay, url = "$url?$urlParam", clz = GamePlayValue.BetResult::class.java)

//        check(result.error_code == 0) { OnePieceExceptionCode.PLATFORM_DATA_FAIL }
//        log.info("result: $result")
//        return result.mapUtil
//        return result
    }

    override fun register(registerReq: GameValue.RegisterReq): GameResponse<String> {
        val clientToken = registerReq.token as GamePlayClientToken
        val data = listOf(
                "merch_id=${clientToken.merchId}",
                "merch_pwd=${clientToken.merchPwd}",
                "cust_id=${registerReq.username}",
                "cust_name=${registerReq.name}",
                "currency=${clientToken.currency}"
        )

        val okResponse = this.startGetXml(clientToken = clientToken, method = "/op/createuser", data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            registerReq.username
        }
    }

    override fun balance(balanceReq: GameValue.BalanceReq): GameResponse<BigDecimal> {
        val clientToken = balanceReq.token as GamePlayClientToken
        val data = listOf(
                "merch_id=${clientToken.merchId}",
                "merch_pwd=${clientToken.merchPwd}",
                "cust_id=${balanceReq.username}",
                "currency=${clientToken.currency}"
        )
        val okResponse = this.startGetXml(clientToken = clientToken, method = "/op/getbalance", data = data)

        return this.bindGameResponse(okResponse = okResponse) {
            it.asBigDecimal("balance")
        }
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameResponse<GameValue.TransferResp> {
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

        val okResponse = this.startGetXml(clientToken = clientToken, method = method, data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            val balance = it.asBigDecimal("after")
            GameValue.TransferResp.successful(balance = balance)
        }
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameResponse<GameValue.TransferResp> {

        val clientToken = checkTransferReq.token as GamePlayClientToken

        val data = listOf(
                "merch_id=${clientToken.merchId}",
                "merch_pwd=${clientToken.merchPwd}",
                "trx_id=${checkTransferReq.orderId}"
        )
        val okResponse = this.startGetXml(clientToken = clientToken, method = "/op/check", data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            val balance = it.asBigDecimal("after")
            GameValue.TransferResp.successful(balance = balance)
        }

    }

    override fun startSlotDemo(startSlotReq: GameValue.StartSlotReq): GameResponse<String> {

        val clientToken = startSlotReq.token as GamePlayClientToken


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


        val baseUrl = if (startSlotReq.launchMethod == LaunchMethod.Wap) clientToken.mobileGamePath else clientToken.gamePath
        val path = "$baseUrl/${startSlotReq.gameId}?$urlParam"
        return GameResponse.of(data = path)
    }

    private fun getTicket(startSlotReq: GameValue.StartSlotReq): String {
        val key = "${startSlotReq.username}:${startSlotReq.gameId}"
        return URLEncoder.encode(Base64.encodeBase64String(key.toByteArray()), "utf-8")
    }

    // 启动游戏需要白名单 193.110.203.190 香港 CN2 01 - 443 单端口
    override fun startSlot(startSlotReq: GameValue.StartSlotReq): GameResponse<String> {
        val clientToken = startSlotReq.token as GamePlayClientToken

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

        val baseUrl = if (startSlotReq.launchMethod == LaunchMethod.Wap) clientToken.mobileGamePath else clientToken.gamePath
        val path = "$baseUrl/${startSlotReq.gameId}?$urlParam"
        return GameResponse.of(data = path)
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): GameResponse<List<BetOrderValue.BetOrderCo>> {

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

        val okResponse = this.startGetBetXml(clientToken = clientToken, method = "/csnbo/api/gateway/betDetail.html", data = data)

        return this.bindGameResponse(okResponse = okResponse) {

            val content = okResponse.response
            val result = xmlMapper.readValue<GamePlayValue.BetResult>(content)

            result.betDetailList?.map { x ->
                val mapUtil = x.mapUtil
                val username = mapUtil.asString("user_id")
                val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.GamePlay, platformUsername = username)
                val orderId = mapUtil.asString("bet_id")
                val betAmount = mapUtil.asBigDecimal("bet")
                val winLose = mapUtil.asBigDecimal("winlose")
                val betTime = mapUtil.asLocalDateTime("trans_date", dateTimeFormat)

                val originData = jacksonObjectMapper().writeValueAsString(mapUtil.data)

                BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, platform = Platform.GamePlay, orderId = orderId, betAmount = betAmount,
                        winAmount = betAmount.plus(winLose), betTime = betTime, settleTime = betTime, originData = originData, validAmount = betAmount)
            }?: emptyList<BetOrderValue.BetOrderCo>()
        }
    }

}



