package com.onepiece.treasure.games.slot.joker

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.token.DefaultClientToken
import com.onepiece.treasure.beans.value.order.BetCacheVo
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.order.JokerBetOrder
import com.onepiece.treasure.core.order.JokerBetOrderDao
import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.games.value.SlotGame
import com.onepiece.treasure.utils.RedisService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class JokerApiService(
        private val okHttpUtil: OkHttpUtil,
        private val redisService: RedisService,
        private val jokerBetOrderDao: JokerBetOrderDao
) : JokerApi {

    private val log = LoggerFactory.getLogger(JokerApiService::class.java)
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")


    override fun slotGames(token: DefaultClientToken): List<SlotGame> {
        val (url, formBody) = JokerBuild.instance("ListGames").build(token)

        val data: List<JokerValue.JokerSlotGame> = okHttpUtil.doPostForm(url, formBody, JokerValue.JokerSlotGameResult::class.java).listGames

        return data.map {
            val platforms = it.supportedPlatForms.split(",").map { platformName ->
                when (platformName) {
                    "Desktop" -> SlotGame.GamePlatform.PC
                    "Mobile" -> SlotGame.GamePlatform.Mobile
                    else -> SlotGame.GamePlatform.PC
                }
            }.toList()

            val specials = it.specials?.split(",")?.map { sp ->
                when (sp) {
                    "new" -> SlotGame.Special.New
                    "hot" -> SlotGame.Special.Hot
                    else -> SlotGame.Special.Hot
                }
            }?: emptyList()

            SlotGame(gameId = it.gameCode, gameName = it.gameName, platforms = platforms, specials = specials, icon = it.image1)
        }
    }

    override fun register(token: DefaultClientToken, username: String, password: String): String {

        // register
        val (url, formBody) = JokerBuild.instance("CU")
                .set("Username", username)
                .build(token)

        val registerResult = okHttpUtil.doPostForm(url, formBody, JokerValue.JokerRegisterResult:: class.java)
//        check(registerResult.status == "Created") { OnePieceExceptionCode.PLATFORM_MEMBER_REGISTER_FAIL }

        // set password
        val (url2, formBody2) = JokerBuild.instance("SP")
                .set("Username", username)
                .set("Password", password)
                .build(token)
        okHttpUtil.doPostForm(url2, formBody2)

        return username

    }

    override fun getCredit(token: DefaultClientToken, username: String): BigDecimal {
        val (url, formBody) = JokerBuild.instance("GC")
                .set("Username", username)
                .build(token)

        val result =  okHttpUtil.doPostForm(url, formBody, JokerValue.GetCreditResult::class.java)
        return result.credit
    }

    override fun transferCredit(token: DefaultClientToken, orderId: String, username: String, amount: BigDecimal): String {
        val (url, formBody) = JokerBuild.instance("TC")
                .set("Amount", amount.toString())
                .set("RequestID", orderId)
                .set("Username", username)
                .build(token)
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

    override fun start(token: DefaultClientToken, username: String, gameId: String): String {
        val userToken = this.requestUserToken(token, username)
        return "${GameConstant.JOKER_GAME_URL}?token=$userToken&game=$gameId&redirectUrl=http://www.baidu.com"
    }

    override fun retrieveTransactions(token: DefaultClientToken, startTime: LocalDateTime, endTime: LocalDateTime): String {

        val nextId = redisService.get(OnePieceRedisKeyConstant.jokerNextId(), String::class.java) {
            //TODO 从数据库中查询
            UUID.randomUUID().toString().replace("-", "")
        }!!

        val (url, formBody) = JokerBuild.instance("TS")
                .set("StartDate", startTime.format(dateFormatter))
                .set("EndDate", endTime.format(dateFormatter))
                .set("NextId", nextId)
                .build(token)

        log.info("StartDate=${startTime.format(dateFormatter)}&EndDate=${endTime.format(dateFormatter)}")

        val betResult = okHttpUtil.doPostForm(url, formBody, JokerValue.BetResult::class.java)

        val orders = betResult.data["Game"]?.map {
            val username= it.username
            val clientId = username.substring(0, 3).toInt()
            val memberId = username.substring(3, username.length).toInt()
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


    override fun getMembersWinLoss(token: DefaultClientToken, username: String?, startDate: LocalDate, endDate: LocalDate): JokerValue.GetMembersWinLoss {
        val (url, formBody) = JokerBuild.instance("RWL")
                .set("StartDate", startDate.toString())
                .set("EndDate", endDate.toString())
                .set("Username", username)
                .build(token)
        return okHttpUtil.doPostForm(url, formBody, JokerValue.GetMembersWinLoss::class.java)
    }

}