package com.onepiece.gpgaming.games.slot

import com.onepiece.gpgaming.beans.enums.GameCategory
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.token.ClientToken
import com.onepiece.gpgaming.beans.model.token.DefaultClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.beans.value.internet.web.SlotGame
import com.onepiece.gpgaming.core.utils.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.bet.MapUtil
import okhttp3.FormBody
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.HmacUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.net.URLEncoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class JokerService : PlatformService() {

    private val log = LoggerFactory.getLogger(JokerService::class.java)
    private val gameUrl = "https://iframe.gpgaming88.com/joker_iframe.html"
    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")


    private fun generatorSign(clientToken: DefaultClientToken, data: Map<String, Any>): String {
        val urlParam = data.map { "${it.key}=${it.value}" }.sorted().joinToString(separator = "&")
        val bytes = HmacUtils.getHmacSha1(clientToken.key.toByteArray()).doFinal(urlParam.toByteArray())
        return URLEncoder.encode(Base64.encodeBase64String(bytes), "utf-8")
    }

    private fun startPostForm(clientToken: DefaultClientToken, data: Map<String, Any>): MapUtil {
        val sign = this.generatorSign(clientToken = clientToken, data = data)
        val urlParam = "AppID=${clientToken.appId}&Signature=${sign}"
        val url = "${clientToken.apiPath}?$urlParam"

        val body = FormBody.Builder()
        data.forEach { body.add(it.key, "${it.value}") }

        val result = okHttpUtil.doPostForm(platform = Platform.Joker, url = url, body = body.build(), clz = JokerValue.Result::class.java)

        return result.mapUtil
    }

    override fun register(registerReq: GameValue.RegisterReq): String {
        val clientToken = registerReq.token as DefaultClientToken

//        val data = mapOf(
//                "Method" to "CU",
//                "Timestamp" to (System.currentTimeMillis()/1000),
//                "Username" to registerReq.username
//        )
        val data = mapOf(
                "Method" to "CU",
                "Timestamp" to System.currentTimeMillis() / 1000,
                "Username" to registerReq.username
        )
        this.startPostForm(clientToken = clientToken, data = data)

        val updatePasswordReq = GameValue.UpdatePasswordReq(token = clientToken, username = registerReq.username, password = registerReq.password)
        this.updatePassword(updatePasswordReq)
        return registerReq.username
    }

    override fun updatePassword(updatePasswordReq: GameValue.UpdatePasswordReq) {
        val clientToken = updatePasswordReq.token as DefaultClientToken

        val data = mapOf(
                "Method" to "SP",
                "Timestamp" to System.currentTimeMillis() / 1000,
                "Username" to updatePasswordReq.username,
                "Password" to updatePasswordReq.password
        )
        this.startPostForm(clientToken = clientToken, data = data)
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val clientToken = balanceReq.token as DefaultClientToken
        val data = mapOf(
                "Method" to "GC",
                "Timestamp" to System.currentTimeMillis() / 1000,
                "Username" to balanceReq.username
        )
        val mapUtil = this.startPostForm(clientToken = clientToken, data = data)
        return mapUtil.asBigDecimal("Credit")
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameValue.TransferResp {

        val clientToken = transferReq.token as DefaultClientToken

        val data = mapOf(
                "Method" to "TC",
                "Timestamp" to System.currentTimeMillis() / 1000,
                "Amount" to transferReq.amount,
                "RequestID" to transferReq.orderId,
                "Username" to transferReq.username
        )
        val mapUtil = this.startPostForm(clientToken = clientToken, data = data)

        val balance = mapUtil.asBigDecimal("Credit")
        return GameValue.TransferResp.successful(balance = balance)
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameValue.TransferResp {
        val clientToken = checkTransferReq.token as DefaultClientToken

        val data = mapOf(
                "Method" to "TCH",
                "Timestamp" to System.currentTimeMillis() / 1000,
                "RequestID" to checkTransferReq.orderId
        )
        return try {
            this.startPostForm(clientToken = clientToken, data = data)
            GameValue.TransferResp.successful()
        } catch (e: Exception) {
            log.error("checkTransfer error", e)
            GameValue.TransferResp.failed()
        }
    }

    override fun slotGames(token: ClientToken, launch: LaunchMethod, language: Language): List<SlotGame> {
        val clientToken = token as DefaultClientToken

        val data = mapOf(
                "Method" to "ListGames",
                "Timestamp" to System.currentTimeMillis() / 1000
        )

        val sign = this.generatorSign(clientToken = clientToken, data = data)
        val body = FormBody.Builder()
        data.forEach { body.add(it.key, "${it.value}") }

        val urlParam = "AppID=${clientToken.appId}&Signature=${sign}"
        val url = "${clientToken.apiPath}?$urlParam"
        val result = okHttpUtil.doPostForm(platform = Platform.Joker, url = url, body = body.build(), clz = JokerValue.GameResult::class.java)

        return result.games.filter {
            when (launch) {
                LaunchMethod.Web -> it.asString("SupportedPlatForms").contains("Desktop")
                LaunchMethod.Wap -> it.asString("SupportedPlatForms").contains("Mobile")
                else -> error(OnePieceExceptionCode.DATA_FAIL)
            }
        }.map { game ->
            //            val category = when (game.asString("GameType")) {
//                "Slot" -> GameCategory.Slot
//                "Fishing" -> GameCategory.Fishing
//                "ECasino" -> GameCategory.Ec
//                else -> {
//                    error(OnePieceExceptionCode.DATA_FAIL)
//                }
//            }

            val specials = game.data["specials"]?.toString()?: ""
            val hot = specials.contains("hot")
            val new = specials.contains("new")

            val gameCode = game.asString("GameCode")
            val gameName = game.asString("GameName")
            val icon = "http:${game.asString("Image1")}"
            val touchIcon = "http:${game.asString("Image2")}"
            SlotGame(gameId = gameCode, category = GameCategory.Slot, gameName = gameName, icon = icon, touchIcon = touchIcon,
                    hot = hot, new = new, status = Status.Normal, platform = Platform.Joker)
        }
    }

    override fun startSlot(startSlotReq: GameValue.StartSlotReq): String {
        val clientToken = startSlotReq.token as DefaultClientToken

        val data = mapOf(
                "Method" to "RT",
                "Timestamp" to System.currentTimeMillis() / 1000,
                "Username" to startSlotReq.username
        )
        val mapUtil = this.startPostForm(clientToken = clientToken, data = data)
        val token = mapUtil.asString("Token")

        val lang = when (startSlotReq.language) {
            Language.CN -> "zh"
            Language.ID -> "id"
            Language.MY -> "ms"
            Language.TH -> "th"
            Language.EN -> "en"
            else -> "en"
        }
        return "${gameUrl}?token=$token&game=${startSlotReq.gameId}&redirectUrl=${startSlotReq.redirectUrl}&lang=${lang}"
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {

        val clientToken = pullBetOrderReq.token as DefaultClientToken
        return this.pullByNextId(clientId = pullBetOrderReq.clientId, platform = Platform.Joker) { startId ->

            val data = mapOf(
                    "Method" to "TS",
                    "Timestamp" to System.currentTimeMillis() / 1000,
                    "StartDate" to pullBetOrderReq.startTime.format(dateTimeFormat),
                    "EndDate" to pullBetOrderReq.endTime.format(dateTimeFormat),
                    "NextId" to startId
            )

            val sign = this.generatorSign(clientToken = clientToken, data = data)
            val urlParam = "AppID=${clientToken.appId}&Signature=${sign}"
            val url = "${clientToken.apiPath}?$urlParam"

            val body = FormBody.Builder()
            data.forEach { body.add(it.key, "${it.value}") }
            val result = okHttpUtil.doPostForm(platform = Platform.Joker, url = url, body = body.build(), clz = JokerValue.BetResult::class.java)
            val list = result.mapUtil.asList("Game")


            val orders = list.map { bet ->
                val orderId = bet.asString("OCode")
                val username = bet.asString("Username")
                val (clientId, member) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.Joker, platformUsername = username)
                val betAmount = bet.asBigDecimal("Amount")
                val winAmount = bet.asBigDecimal("Result")
                val betTime = bet.asString("Time").substring(0, 19).let { LocalDateTime.parse(it) }

                val originData = objectMapper.writeValueAsString(bet
                )
                BetOrderValue.BetOrderCo(clientId = clientId, memberId = member, orderId = orderId, platform = Platform.Joker, betAmount = betAmount,
                        winAmount = winAmount, betTime = betTime, settleTime = betTime, originData = originData, validAmount = betAmount)
            }

            result.nextId to orders
        }
    }

}