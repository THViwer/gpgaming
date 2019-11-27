package com.onepiece.treasure.games.slot

import com.onepiece.treasure.beans.enums.*
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.ClientToken
import com.onepiece.treasure.beans.model.token.DefaultClientToken
import com.onepiece.treasure.beans.value.internet.web.SlotGame
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformService
import com.onepiece.treasure.games.bet.MapUtil
import okhttp3.FormBody
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.HmacUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.net.URLEncoder
import java.time.format.DateTimeFormatter

@Service
class JokerService : PlatformService() {

    private val log = LoggerFactory.getLogger(JokerService::class.java)
    private val gameUrl = "http://94.237.64.70/iframe.html"

    private fun generatorSign(clientToken: DefaultClientToken, data: Map<String, Any>): String {
        val urlParam = data.map { "${it.key}=${it.value}" }.sorted().joinToString(separator = "&")
        val bytes = HmacUtils.getHmacSha1(clientToken.key.toByteArray()).doFinal(urlParam.toByteArray())
        return URLEncoder.encode(Base64.encodeBase64String(bytes), "utf-8")
    }

    private fun startPostForm(clientToken: DefaultClientToken, data: Map<String, Any>): MapUtil {
        val sign = this.generatorSign(clientToken = clientToken, data = data)
        val urlParam = "AppID=${clientToken.appId}&Signature=${sign}"
        val url = "${gameConstant.getDomain(Platform.Joker)}?$urlParam"

        val body = FormBody.Builder()
        data.forEach { body.add(it.key, "${it.value}") }

        val result = okHttpUtil.doPostForm(url = url, body = body.build(), clz = JokerValue.Result::class.java)
        // check status
//        check(result.status == "OK") { OnePieceExceptionCode.PLATFORM_DATA_FAIL }

        return result.mapUtil
    }

    override fun register(registerReq: GameValue.RegisterReq): String {
        val clientToken = registerReq.token as DefaultClientToken

        val data = mapOf(
                "Method" to "CU",
                "Timestamp" to System.currentTimeMillis(),
                "Username" to registerReq.username
        )
        this.startPostForm(clientToken = clientToken, data = data)

        this.setPassword(registerReq)
        return registerReq.username
    }

    private fun setPassword(registerReq: GameValue.RegisterReq) {
        val clientToken = registerReq.token as DefaultClientToken

        val data = mapOf(
                "Method" to "SP",
                "Timestamp" to System.currentTimeMillis(),
                "Username" to registerReq.username,
                "Password" to registerReq.password
        )
        this.startPostForm(clientToken = clientToken, data = data)
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val clientToken = balanceReq.token as DefaultClientToken
        val data = mapOf(
                "Method" to "GC",
                "Timestamp" to System.currentTimeMillis(),
                "Username" to balanceReq.username
        )
        val mapUtil = this.startPostForm(clientToken = clientToken, data = data)
        return mapUtil.asBigDecimal("Credit")
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {

        val clientToken = transferReq.token as DefaultClientToken

        val data = mapOf(
                "Method" to "TC",
                "Timestamp" to System.currentTimeMillis(),
                "Amount" to transferReq.amount,
                "RequestID" to transferReq.orderId,
                "Username" to transferReq.username
        )
        this.startPostForm(clientToken = clientToken, data = data)
        return transferReq.orderId
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): Boolean {
        val clientToken = checkTransferReq.token as DefaultClientToken

        val data = mapOf(
                "Method" to "TCH",
                "Timestamp" to System.currentTimeMillis(),
                "RequestID" to checkTransferReq.orderId
        )
        return try {
            this.startPostForm(clientToken = clientToken, data = data)
            true
        } catch (e: Exception) {
            log.error("checkTransfer error", e)
            false
        }
    }


    override fun slotGames(token: ClientToken, launch: LaunchMethod): List<SlotGame> {
        val clientToken = token as DefaultClientToken

        val data = mapOf(
                "Method" to "ListGames",
                "Timestamp" to System.currentTimeMillis()
        )

        val sign = this.generatorSign(clientToken = clientToken, data = data)
        val body = FormBody.Builder()
        data.forEach { body.add(it.key, "${it.value}") }

        val urlParam = "AppID=${clientToken.appId}&Signature=${sign}"
        val url = "${gameConstant.getDomain(Platform.Joker)}?$urlParam"
        val result = okHttpUtil.doPostForm(url = url, body = body.build(), clz = JokerValue.GameResult::class.java)

        return result.games.filter {
            when (launch) {
                LaunchMethod.Web -> it.asString("SupportedPlatForms").contains("Desktop")
                LaunchMethod.Wap -> it.asString("SupportedPlatForms").contains("Mobile")
                else -> error(OnePieceExceptionCode.DATA_FAIL)
            }
        }.map { game ->
            val category = when (game.asString("GameType")) {
                "Slot" -> GameCategory.SLOT
                "Fishing" -> GameCategory.FISHING
                "ECasino" -> GameCategory.ECASINO
                else -> {
                    error(OnePieceExceptionCode.DATA_FAIL)
                }
            }

            val hot = game.asString("specials").contains("hot")
            val new = game.asString("specials").contains("new")

            val gameCode = game.asString("GameCode")
            val gameName = game.asString("GameName")
            val icon = "http://${game.asString("Image1")}"
            val touchIcon = "http://${game.asString("Image2")}"
            SlotGame(gameId = gameCode, category = category, gameName = gameName, icon = icon, touchIcon = touchIcon,
                    hot = hot, new = new, status = Status.Normal, chineseGameName = gameName)
        }
    }

    override fun startSlot(startSlotReq: GameValue.StartSlotReq): String {
        val clientToken = startSlotReq.token as DefaultClientToken

        val data = mapOf(
                "Method" to "RT",
                "Timestamp" to System.currentTimeMillis(),
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


}