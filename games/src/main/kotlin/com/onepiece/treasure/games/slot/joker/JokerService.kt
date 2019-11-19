package com.onepiece.treasure.games.slot.joker

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.treasure.beans.enums.*
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.DefaultClientToken
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.beans.value.internet.web.SlotGame
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformApi
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.utils.RedisService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

/**
 * 启动平台支持语言
 * en - ENGLISH、id - INDONESIA、ms - BAHASA MALAYSIA、th - THAILAND、zh - CHINESE
 *
 */
@Service
class JokerService : PlatformApi() {

    private val log = LoggerFactory.getLogger(JokerService::class.java)
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    override fun register(registerReq: GameValue.RegisterReq): String {

        val username = registerReq.username
        val token = registerReq.token as DefaultClientToken

        // register
        val (url, formBody) = JokerBuild.instance("CU")
                .set("Username", username)
                .build(token)

        val registerResult = okHttpUtil.doPostForm(url, formBody, JokerValue.JokerRegisterResult:: class.java)
        check(registerResult.status == "Created") { OnePieceExceptionCode.PLATFORM_MEMBER_REGISTER_FAIL }

        // set password
        val (url2, formBody2) = JokerBuild.instance("SP")
                .set("Username", username)
                .set("Password", registerReq.password)
                .build(token)
        okHttpUtil.doPostForm(url2, formBody2)


        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {

        val (url, formBody) = JokerBuild.instance("GC")
                .set("Username", balanceReq.username)
                .build(balanceReq.token as DefaultClientToken)

        val result =  okHttpUtil.doPostForm(url, formBody, JokerValue.GetCreditResult::class.java)
        return result.credit

    }

    override fun transfer(transferReq: GameValue.TransferReq): String {

        val (url, formBody) = JokerBuild.instance("TC")
                .set("Amount", transferReq.amount)
                .set("RequestID", transferReq.orderId)
                .set("Username", transferReq.username)
                .build(transferReq.token as DefaultClientToken)
        val result = okHttpUtil.doPostForm(url, formBody, JokerValue.TransferCreditResult::class.java)
        return result.requestId
    }

    /**
     * 获得用户token
     */
    private fun requestUserToken(token: DefaultClientToken, username: String): String {
        val (url, formBody) = JokerBuild.instance("RT")
                .set("Username", username)
                .build(token)
        val result = okHttpUtil.doPostForm(url, formBody, JokerValue.RequestUserTokenResult::class.java)
        return result.token
    }


    override fun slotGames(token: DefaultClientToken, launch: LaunchMethod): List<SlotGame> {
        val (url, formBody) = JokerBuild.instance("ListGames").build(token)

        val data: List<JokerValue.JokerSlotGame> = okHttpUtil.doPostForm(url, formBody, JokerValue.JokerSlotGameResult::class.java).listGames



        return data.filter {
            when (launch) {
                LaunchMethod.Web -> it.supportedPlatForms.contains("Desktop")
                LaunchMethod.Wap -> it.supportedPlatForms.contains("Mobile")
                else -> error(OnePieceExceptionCode.DATA_FAIL)
            }
        }.map {

            val category = when (it.gameType) {
                "Slot" -> GameCategory.SLOT
                "Fishing" -> GameCategory.FISHING
                "ECasino" -> GameCategory.ECASINO
                else -> {
                    error(OnePieceExceptionCode.DATA_FAIL)
                }
            }

            val hot = it.specials?.contains("hot")?: false
            val new = it.specials?.contains("new")?: false

            SlotGame(gameId = it.gameCode, category = category, gameName = it.gameName, icon = "http:${it.image1}", touchIcon = "http:${it.image2}",
                    hot = hot, new = new, status = Status.Normal)
        }
    }


    override fun startSlot(startSlotReq: GameValue.StartSlotReq): String {
        val lang = when (startSlotReq.language) {
            Language.CN -> "zh"
            Language.ID -> "id"
            Language.MY -> "ms"
            Language.TH -> "th"
            Language.EN -> "en"
            else -> "en"
        }

        val userToken = this.requestUserToken(startSlotReq.token as DefaultClientToken, startSlotReq.username)
        return "${GameConstant.JOKER_GAME_URL}?token=$userToken&game=${startSlotReq.gameId}&redirectUrl=${startSlotReq.redirectUrl}&lang=${lang}"
    }


    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {

        val nextId = redisService.get(OnePieceRedisKeyConstant.jokerNextId(), String::class.java) { "" }!!

        val startTime = pullBetOrderReq.startTime
        val endTime = pullBetOrderReq.endTime
        val token = pullBetOrderReq.token as DefaultClientToken

        val (url, formBody) = JokerBuild.instance("TS")
                .set("StartDate", startTime.format(dateFormatter))
                .set("EndDate", endTime.format(dateFormatter))
                .set("NextId", nextId)
                .build(token)

        log.info("StartDate=${startTime.format(dateFormatter)}&EndDate=${endTime.format(dateFormatter)}")
        val betResult = okHttpUtil.doPostForm(url, formBody, JokerBetOrder::class.java)

        val orders = betResult.getBetOrders(objectMapper = objectMapper)
        if (orders.isEmpty()) return emptyList()

        val nextKey = OnePieceRedisKeyConstant.pullBetOrderLastKey(clientId = pullBetOrderReq.clientId, platform = Platform.Joker)
        redisService.put(nextKey, betResult.nextId)

        return orders
    }

}