package com.onepiece.treasure.games

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.StartPlatform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.ClientToken
import com.onepiece.treasure.beans.model.token.DefaultClientToken
import com.onepiece.treasure.beans.model.token.Kiss918ClientToken
import com.onepiece.treasure.core.order.BetOrderValue
import com.onepiece.treasure.core.order.Cta666BetOrderDao
import com.onepiece.treasure.core.order.JokerBetOrderDao
import com.onepiece.treasure.core.service.PlatformBindService
import com.onepiece.treasure.core.service.PlatformMemberService
import com.onepiece.treasure.core.service.WalletService
import com.onepiece.treasure.games.live.cta666.Cta666Api
import com.onepiece.treasure.games.slot.joker.JokerApi
import com.onepiece.treasure.games.slot.kiss918.Kiss918Api
import com.onepiece.treasure.games.sport.sbo.SboApi
import com.onepiece.treasure.games.value.SlotGame
import com.onepiece.treasure.utils.StringUtil
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate

@Component
class GameApi(
        private val platformBindService: PlatformBindService,
        private val platformMemberService: PlatformMemberService,

        private val jokerApi: JokerApi,
        private val cta666Api: Cta666Api,
        private val kiss918Api: Kiss918Api,
        private val sboApi: SboApi,

        private val cta666BetOrderDao: Cta666BetOrderDao,
        private val jokerBetOrderDao: JokerBetOrderDao

) {

    /**
     * 注册账号
     */
    fun register(clientId: Int, memberId: Int, platform: Platform) {

        // 生成用户名
        val generatorUsername = when (platform) {
            Platform.Joker, Platform.Cta666, Platform.Sbo -> this.generatorUsername(clientId = clientId, memberId = memberId)
            Platform.Kiss918 -> ""
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
        val generatorPassword = StringUtil.generatePassword()

        // 获得配置信息
        val clientToken = this.getClientToken(clientId = clientId, platform = platform)

        // 注册账号
        val username = when (platform) {
            Platform.Joker -> jokerApi.register(token = clientToken as DefaultClientToken, username = generatorUsername, password = generatorPassword)
            Platform.Cta666 -> cta666Api.signup(token = clientToken as DefaultClientToken, username = generatorUsername, password = generatorPassword)
            Platform.Kiss918 -> kiss918Api.addUser(token = clientToken as Kiss918ClientToken, password = generatorPassword)
            Platform.Sbo -> sboApi.registerPlayer(token = clientToken as DefaultClientToken, username = generatorUsername)
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }

        platformMemberService.create(clientId = clientId, memberId = memberId, platform = platform, platformUsername = username, platformPassword = generatorPassword)
    }

    /**
     * 老虎机游戏列表
     */
    fun slotGames(clientId: Int, platform: Platform): List<SlotGame> {

        val clientToken = this.getClientToken(clientId = clientId, platform = platform)

        return when (platform) {
            Platform.Joker -> jokerApi.slotGames(token = clientToken as DefaultClientToken)
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }


    /**
     * 开始游戏(平台)
     */
    fun start(clientId: Int, platformUsername: String, platform: Platform, startPlatform: StartPlatform = StartPlatform.Pc): String {

        val clientToken = this.getClientToken(clientId = clientId, platform = platform)

        return when (platform) {
            Platform.Cta666 -> cta666Api.login(token = clientToken as DefaultClientToken, startPlatform = startPlatform, username = platformUsername)
            Platform.Sbo -> sboApi.login(token = clientToken as DefaultClientToken, username = platformUsername)
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    /**
     * 开始游戏(老虎机)
     */
    fun start(clientId: Int, platformUsername: String, platform: Platform, gameId: String): String {

        val clientToken = this.getClientToken(clientId = clientId, platform = platform)

        return when (platform) {
            Platform.Joker -> jokerApi.start(token = clientToken as DefaultClientToken, gameId = gameId, username = platformUsername)
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }


    /**
     * 查询会员余额
     */
    fun balance(clientId: Int, platformUsername: String, platform: Platform): BigDecimal {
        val clientToken = this.getClientToken(clientId = clientId, platform = platform)

        return when (platform) {
            Platform.Joker -> jokerApi.getCredit(token = clientToken as DefaultClientToken, username = platformUsername)
            Platform.Cta666 -> cta666Api.getBalance(token = clientToken as DefaultClientToken, username = platformUsername)
            Platform.Kiss918 -> kiss918Api.userinfo(token = clientToken as Kiss918ClientToken, username = platformUsername)
            Platform.Sbo -> sboApi.getPlayerBalance(token = clientToken as DefaultClientToken, username = platformUsername)
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }


    /**
     * 转账
     */
    fun transfer(clientId: Int, platformUsername: String, platform: Platform, orderId: String, amount: BigDecimal) {

        val clientToken = this.getClientToken(clientId = clientId, platform = platform)

        when (platform) {
            Platform.Joker -> jokerApi.transferCredit(token = clientToken as DefaultClientToken, username = platformUsername, orderId = orderId, amount = amount)
            Platform.Cta666 -> cta666Api.transfer(token = clientToken as DefaultClientToken, username = platformUsername, orderId = orderId, amount = amount)
            Platform.Kiss918 -> kiss918Api.setScore(token = clientToken as Kiss918ClientToken, username = platformUsername, orderId = orderId, amount = amount)
            Platform.Sbo -> sboApi.depositOrWithdraw(token = clientToken as DefaultClientToken, username = platformUsername, orderId = orderId, amount = amount)
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    /**
     * 查询下注订单
     */
    fun queryBetOrder(clientId: Int, memberId: Int, platform: Platform, startDate: LocalDate, endDate: LocalDate): Any {
        val query = BetOrderValue.Query(clientId = clientId, memberId = memberId, startTime = startDate.atStartOfDay(), endTime = endDate.atStartOfDay())

        return when (platform) {
            Platform.Cta666 -> cta666BetOrderDao.query(query)
            Platform.Joker -> jokerBetOrderDao.query(query)
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    /**
     * 查询下注订单
     */
    fun queryBetOrder(clientId: Int, platformUsername: String, platform: Platform, startDate: LocalDate, endDate: LocalDate): Any {
        val clientToken = getClientToken(clientId = clientId, platform = platform)
        return when(platform) {
            Platform.Kiss918 -> kiss918Api.accountReport(token = clientToken as Kiss918ClientToken, username = platformUsername, startDate = startDate, endDate = endDate)
            Platform.Sbo -> sboApi.getCustomerReport(token = clientToken as DefaultClientToken, username = platformUsername, startDate = startDate, endDate = endDate)
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }

    }

    // 获得代理token
    private fun getClientToken(clientId: Int, platform: Platform): ClientToken {
        return platformBindService.find(clientId = clientId, platform = platform).clientToken
    }

    // 生成用户名
    private fun generatorUsername(clientId: Int, memberId: Int): String {
        return when  {
            clientId < 10 -> "00$clientId"
            clientId < 100 -> "0$clientId"
            else -> "$clientId"
        }.let {
            "A$it$memberId"
        }
    }


}