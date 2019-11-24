package com.onepiece.treasure.games

import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.PlatformBind
import com.onepiece.treasure.beans.model.token.ClientToken
import com.onepiece.treasure.beans.model.token.MegaClientToken
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.beans.value.internet.web.SlotGame
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.PlatformUsernameUtil
import com.onepiece.treasure.core.service.PlatformBindService
import com.onepiece.treasure.core.service.PlatformMemberService
import com.onepiece.treasure.games.fishing.GGFishingService
import com.onepiece.treasure.games.live.*
import com.onepiece.treasure.games.live.sexy.SexyService
import com.onepiece.treasure.games.slot.joker.JokerService
import com.onepiece.treasure.games.slot.kiss918.Kiss918Service
import com.onepiece.treasure.games.slot.mega.MegaService
import com.onepiece.treasure.games.slot.pussy888.Pussy888Service
import com.onepiece.treasure.games.sport.BcsService
import com.onepiece.treasure.games.sport.LbcService
import com.onepiece.treasure.games.sport.sbo.SboService
import com.onepiece.treasure.utils.RedisService
import com.onepiece.treasure.utils.StringUtil
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime

@Component
class GameApi(
        private val platformBindService: PlatformBindService,
        private val platformMemberService: PlatformMemberService,
        private val redisService: RedisService,


        // slot
        private val jokerService: JokerService,
        private val kiss918Service: Kiss918Service,
        private val pussy888Service: Pussy888Service,
        private val megaService: MegaService,
        private val pragmaticService: PragmaticService,
        private val spadeGamingService: SpadeGamingService,

        // live game
        private val goldDeluxeService: GoldDeluxeService,
        private val evolutionService: EvolutionService,
        private val sexyService: SexyService,
        private val fggService: FggService,
        private val allBetService: AllBetService,
        private val dreamGamingService: DreamGamingService,

        // sport
        private val sboService: SboService,
        private val lbcService: LbcService,
        private val bcsService: BcsService,

        // fishing
        private val ggFishingService: GGFishingService


) {

    private fun getPlatformApi(platform: Platform): PlatformService {
        return when (platform) {

            // slot
            Platform.Joker -> jokerService
            Platform.Kiss918 -> kiss918Service
            Platform.Pussy888 -> pussy888Service
            Platform.Mega -> megaService
            Platform.Pragmatic -> pragmaticService
            Platform.SpadeGaming -> spadeGamingService

            // live game
            Platform.Fgg -> fggService
            Platform.Evolution -> evolutionService
            Platform.AllBet -> allBetService
            Platform.DreamGaming -> dreamGamingService

            // sport
            Platform.Lbc -> lbcService
            Platform.Sbo -> sboService
            Platform.Bcs -> bcsService

            // fishing
            Platform.GGFishing -> ggFishingService

            // 未完成测试
            Platform.GoldDeluxe -> goldDeluxeService
            Platform.SexyGaming -> sexyService

            else -> error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
        }

    }

    /**
     * 注册账号
     */
    fun register(clientId: Int, memberId: Int, platform: Platform) {

        // 生成用户名
        val generatorUsername = PlatformUsernameUtil.generatorPlatformUsername(clientId = clientId, memberId = memberId, platform = platform)
        val generatorPassword = StringUtil.generatePassword()

        // 获得配置信息
        val clientToken = this.getClientToken(clientId = clientId, platform = platform)

        // 注册账号
        val registerReq = GameValue.RegisterReq(token = clientToken, username = generatorUsername, password = generatorPassword, name = generatorUsername)
        val platformUsername = getPlatformApi(platform).register(registerReq)

        platformMemberService.create(clientId = clientId, memberId = memberId, platform = platform, platformUsername = platformUsername, platformPassword = generatorPassword)
    }

    /**
     * 老虎机游戏列表
     */
    fun slotGames(clientId: Int, platform: Platform, launch: LaunchMethod): List<SlotGame> {

        val redisKey = OnePieceRedisKeyConstant.slotGames(platform = platform, launch = launch)

        return redisService.getList(key = redisKey, clz = SlotGame::class.java, timeout = 3600) {
            val clientToken = this.getClientToken(clientId = clientId, platform = platform)

            when (platform) {
                Platform.Joker, Platform.Pragmatic -> jokerService.slotGames(token = clientToken, launch = launch)
                else -> error(OnePieceExceptionCode.DATA_FAIL)
            }
        }
    }


    /**
     * 开始游戏(平台)
     */
    fun start(clientId: Int, platformUsername: String, platformPassword: String, platform: Platform, startPlatform: LaunchMethod = LaunchMethod.Web, language: Language): String {

        val clientToken = this.getClientToken(clientId = clientId, platform = platform)

        return when (platform) {
            Platform.Evolution,
            Platform.Lbc,
            Platform.Sbo,
            Platform.GoldDeluxe,
            Platform.Fgg,
            Platform.AllBet,
            Platform.GGFishing,
            Platform.Bcs -> {
                val startReq = GameValue.StartReq(token = clientToken, username = platformUsername, startPlatform = startPlatform, language = language, password = platformPassword)
                this.getPlatformApi(platform).start(startReq)
            }
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    /**
     * 开始游戏(老虎机)
     */
    fun start(clientId: Int, platformUsername: String, platform: Platform, gameId: String, language: Language, launchMethod: LaunchMethod): String {

        val clientToken = this.getClientToken(clientId = clientId, platform = platform)

        //TODO 跳转url
        val startSlotReq = GameValue.StartSlotReq(token = clientToken, username = platformUsername, gameId = gameId, language = language, launchMethod = launchMethod)
        return when (platform) {
            Platform.Joker -> jokerService.startSlot(startSlotReq)
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }


    /**
     * 查询会员余额
     */
    fun balance(clientId: Int, platformUsername: String, platformPassword: String, platform: Platform): BigDecimal {
        val clientToken = this.getClientToken(clientId = clientId, platform = platform)

        val balanceReq = GameValue.BalanceReq(token = clientToken, username = platformUsername, password = platformPassword)
        return this.getPlatformApi(platform).balance(balanceReq).setScale(2, 2)
    }


    /**
     * 转账
     */
    fun transfer(clientId: Int, platformUsername: String, platform: Platform, orderId: String, amount: BigDecimal) {

        val clientToken = this.getClientToken(clientId = clientId, platform = platform)

        val transferReq = GameValue.TransferReq(token = clientToken, orderId = orderId, username = platformUsername, amount = amount)
        this.getPlatformApi(platform).transfer(transferReq)
    }

//    /**
//     * 查询下注订单
//     */
//    fun queryBetOrder(clientId: Int, memberId: Int, platform: Platform, startDate: LocalDate, endDate: LocalDate): Any {
//        val query = BetOrderValue.Query(clientId = clientId, memberId = memberId, startTime = startDate.atStartOfDay(), endTime = endDate.atStartOfDay())
//
//        return when (platform) {
//            Platform.Joker -> jokerBetOrderDao.query(query)
//            else -> error(OnePieceExceptionCode.DATA_FAIL)
//        }
//    }

    /**
     * 查询下注订单
     */
    fun queryBetOrder(clientId: Int, platformUsername: String, platform: Platform, startTime: LocalDateTime, endTime: LocalDateTime): Any {
        val clientToken = getClientToken(clientId = clientId, platform = platform)

        return when(platform) {
            Platform.Kiss918, Platform.Mega, Platform.Pussy888, Platform.SexyGaming, Platform.Bcs, Platform.AllBet -> {
                val betOrderReq = GameValue.BetOrderReq(token = clientToken, startTime = startTime, endTime = endTime, username = platformUsername)
                getPlatformApi(platform).queryBetOrder(betOrderReq)
            }
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    /**
     * 同步订单
     */
    fun pullBets(platformBind: PlatformBind, startTime: LocalDateTime, endTime: LocalDateTime): List<BetOrderValue.BetOrderCo> {

        return when (platformBind.platform) {
            Platform.SpadeGaming,
            Platform.Joker,
            Platform.Lbc,
            Platform.Bcs,
            Platform.Fgg,
            Platform.AllBet,
            Platform.GGFishing -> {
                val pullBetOrderReq = GameValue.PullBetOrderReq(clientId = platformBind.clientId, startTime = startTime, endTime = endTime, token = platformBind.clientToken)
                getPlatformApi(platformBind.platform).pullBetOrders(pullBetOrderReq)
            }
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }


    }

    /**
     * 查询下载地址
     */
    fun getAppDownload(clientId: Int, platform: Platform): String {
        val clientToken = getClientToken(clientId = clientId, platform = platform)

        return when(platform) {
            Platform.Mega -> megaService.downApp(token = clientToken as MegaClientToken)
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