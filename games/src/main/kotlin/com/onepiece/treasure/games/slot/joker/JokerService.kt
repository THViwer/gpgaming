package com.onepiece.treasure.games.slot.joker

import com.onepiece.treasure.beans.enums.GameCategory
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.DefaultClientToken
import com.onepiece.treasure.beans.value.internet.web.SlotGame
import com.onepiece.treasure.beans.value.order.BetCacheVo
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.order.JokerBetOrder
import com.onepiece.treasure.core.order.JokerBetOrderDao
import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformApi
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.utils.RedisService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class JokerService(
        private val okHttpUtil: OkHttpUtil,
        private val redisService: RedisService,
        private val jokerBetOrderDao: JokerBetOrderDao
) : PlatformApi() {

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
//        check(registerResult.status == "Created") { OnePieceExceptionCode.PLATFORM_MEMBER_REGISTER_FAIL }

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
        val userToken = this.requestUserToken(startSlotReq.token as DefaultClientToken, startSlotReq.username)
        return "${GameConstant.JOKER_GAME_URL}?token=$userToken&game=${startSlotReq.gameId}&redirectUrl=${startSlotReq.redirectUrl}"
    }

    override fun asynBetOrder(syncBetOrderReq: GameValue.SyncBetOrderReq): String {

        val nextId = redisService.get(OnePieceRedisKeyConstant.jokerNextId(), String::class.java) {
            //TODO 从数据库中查询
            UUID.randomUUID().toString().replace("-", "")
        }!!


        val startTime = syncBetOrderReq.startTime
        val endTime = syncBetOrderReq.endTime
        val token = syncBetOrderReq.token as DefaultClientToken

        val (url, formBody) = JokerBuild.instance("TS")
                .set("StartDate", startTime.format(dateFormatter))
                .set("EndDate", endTime.format(dateFormatter))
                .set("NextId", nextId)
                .build(token)

        log.info("StartDate=${startTime.format(dateFormatter)}&EndDate=${endTime.format(dateFormatter)}")

        val betResult = okHttpUtil.doPostForm(url, formBody, JokerValue.BetResult::class.java)

        val orders = betResult.data["Game"]?.map {
            val username= it.username
            val clientId = username.substring(1, 4).toInt()
            val memberId = username.substring(4, username.length).toInt()
            val now = LocalDateTime.now()

            JokerBetOrder(oCode = it.oCode, clientId = clientId, memberId = memberId, gameCode = it.gameCode, description = it.description,
                    type = it.type, amount = it.amount, result = it.result, time = it.time.withZoneSameInstant(ZoneId.of("Asia/Shanghai")).toLocalDateTime(), appId = it.appId, createdTime = now,
                    username = username, currencyCode = it.currencyCode, details = it.details, freeAmount = it.freeAmount, roundId = it.roundId)
        }

        if (orders != null && orders.isNotEmpty()) {

            jokerBetOrderDao.create(orders)

            // 放到缓存
            val caches = orders.groupBy { it.memberId }.map {
                val memberId = it.key
                val money = it.value.sumByDouble { it.amount.toDouble() }.toBigDecimal().setScale(2, 2)

                BetCacheVo(memberId = memberId, bet = money, platform = Platform.Joker)
            }
            val redisKey = OnePieceRedisKeyConstant.betCache(nextId)
            redisService.put(redisKey, caches)

            // 下一次的值set到redis中
            redisService.put(OnePieceRedisKeyConstant.jokerNextId(), betResult.nextId)
        }

        return nextId

    }

}