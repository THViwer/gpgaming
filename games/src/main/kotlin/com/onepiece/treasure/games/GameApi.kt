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
import com.onepiece.treasure.games.combination.PlaytechService
import com.onepiece.treasure.games.fishing.GGFishingService
import com.onepiece.treasure.games.live.*
import com.onepiece.treasure.games.slot.*
import com.onepiece.treasure.games.sport.BcsService
import com.onepiece.treasure.games.sport.CMDService
import com.onepiece.treasure.games.sport.LbcService
import com.onepiece.treasure.utils.RedisService
import org.slf4j.LoggerFactory
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
        private val ttgService: TTGService,
        private val microGameService: MicroGamingService,
        private val gamePlayService: GamePlayService,

        // live game
        private val goldDeluxeService: GoldDeluxeService,
        private val evolutionService: EvolutionService,
        private val sexyGamingService: SexyGamingService,
        private val fggService: FggService,
        private val allBetService: AllBetService,
        private val dreamGamingService: DreamGamingService,
        private val pngService: PNGService,

        // sport
//        private val sboService: SboService,
        private val lbcService: LbcService,
        private val bcsService: BcsService,
        private val cmdService: CMDService,

        // fishing
        private val ggFishingService: GGFishingService,

        // slot and live
        private val playtechService: PlaytechService,
        private val saGamingService: SaGamingService,
        private val asiaGamingService: AsiaGamingService

) {

    private val log = LoggerFactory.getLogger(GameApi::class.java)

    private fun getPlatformApi(platform: Platform): PlatformService {
        return when (platform) {

            // slot
            Platform.Joker -> jokerService
            Platform.Kiss918 -> kiss918Service
            Platform.Pussy888 -> pussy888Service
            Platform.Mega -> megaService
            Platform.Pragmatic -> pragmaticService
            Platform.SpadeGaming -> spadeGamingService
            Platform.TTG -> ttgService
            Platform.MicroGaming -> microGameService
            Platform.GamePlay -> gamePlayService

            // live game
            Platform.Fgg -> fggService
            Platform.Evolution -> evolutionService
            Platform.AllBet -> allBetService
            Platform.DreamGaming -> dreamGamingService
            Platform.GoldDeluxe -> goldDeluxeService
            Platform.SexyGaming -> sexyGamingService
            Platform.PNG -> pngService

            // sport
            Platform.Lbc -> lbcService
            Platform.Bcs -> bcsService
            Platform.CMD -> cmdService

            // fishing
            Platform.GGFishing -> ggFishingService

            // slot and live
            Platform.PlaytechSlot -> playtechService
            Platform.SaGaming -> saGamingService
            Platform.AsiaGaming -> asiaGamingService


            else -> error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
        }

    }

    /**
     * 注册账号
     */
    fun register(clientId: Int, memberId: Int, platform: Platform) {

        // 生成用户名
        val (generatorUsername, generatorPassword) = PlatformUsernameUtil.generatorPlatformUsername(clientId = clientId, memberId = memberId, platform = platform)

        // 获得配置信息
        val clientToken = this.getClientToken(clientId = clientId, platform = platform)

        // 注册账号
        val registerReq = GameValue.RegisterReq(token = clientToken, username = generatorUsername, password = generatorPassword, name = generatorUsername,
                clientId = clientId, memberId = memberId)
        val platformUsername = getPlatformApi(platform).register(registerReq)

        platformMemberService.create(clientId = clientId, memberId = memberId, platform = platform, platformUsername = platformUsername, platformPassword = generatorPassword)
    }

    /**
     * 修改密码
     */
    fun updatePassword(clientId: Int, platform: Platform, username: String, password: String) {

        when (platform) {
            Platform.Joker,
            Platform.Kiss918,
            Platform.Pussy888,
            Platform.AllBet,
            Platform.DreamGaming -> {
                // 获得配置信息
                val clientToken = this.getClientToken(clientId = clientId, platform = platform)

                // 修改平台密码
                val updatePasswordReq = GameValue.UpdatePasswordReq(token = clientToken, username = username, password = password)
                getPlatformApi(platform).updatePassword(updatePasswordReq)

            }
            else -> error( OnePieceExceptionCode.DATA_FAIL )
        }
    }

    /**
     * 老虎机游戏列表
     */
    fun slotGames(clientId: Int, platform: Platform, launch: LaunchMethod): List<SlotGame> {

        val redisKey = OnePieceRedisKeyConstant.slotGames(platform = platform, launch = launch)

//        return redisService.getList(key = redisKey, clz = SlotGame::class.java, timeout = 3600) {
        val clientToken = this.getClientToken(clientId = clientId, platform = platform)

        return when (platform) {
            Platform.Joker,
            Platform.Pragmatic,
            Platform.TTG,
            Platform.SpadeGaming -> getPlatformApi(platform).slotGames(token = clientToken, launch = launch)
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }

//        }
    }


    /**
     * 开始游戏(平台)
     */
    fun start(clientId: Int, platformUsername: String, platformPassword: String, platform: Platform, launch: LaunchMethod = LaunchMethod.Web, language: Language): String {

        val clientToken = this.getClientToken(clientId = clientId, platform = platform)

        return when (platform) {
            Platform.Evolution,
            Platform.Lbc,
            Platform.GoldDeluxe,
            Platform.Fgg,
            Platform.AllBet,
            Platform.GGFishing,
            Platform.DreamGaming,
            Platform.CMD,
            Platform.SexyGaming,
            Platform.SaGaming,
            Platform.PNG,
            Platform.Bcs -> {
                val startReq = GameValue.StartReq(token = clientToken, username = platformUsername, launch = launch, language = language, password = platformPassword)
                this.getPlatformApi(platform).start(startReq)
            }
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    /**
     * 开始平台试玩
     */
    fun startDemo(clientId: Int, platform: Platform,  language: Language, launch: LaunchMethod): String {
        val clientToken = this.getClientToken(clientId = clientId, platform = platform)

        return when (platform) {
            Platform.Lbc,
            Platform.Bcs,
            Platform.CMD -> this.getPlatformApi(platform).startDemo(token = clientToken, language = language, launch = launch)
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    /**
     * 开始游戏(老虎机)
     */
    fun start(clientId: Int, platformUsername: String, platform: Platform, gameId: String, language: Language,
              launchMethod: LaunchMethod): String {

        val clientToken = this.getClientToken(clientId = clientId, platform = platform)

        //TODO 跳转url
        val startSlotReq = GameValue.StartSlotReq(token = clientToken, username = platformUsername, gameId = gameId, language = language,
                launchMethod = launchMethod)
        return when (platform) {
            Platform.Joker,
            Platform.Pragmatic,
            Platform.TTG,
            Platform.MicroGaming,
            Platform.PlaytechSlot,
            Platform.PNG,
            Platform.SpadeGaming -> getPlatformApi(platform).startSlot(startSlotReq)
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }


    /**
     * 开始老虎机试玩
     */
    fun startSlotDemo(clientId: Int, platform: Platform, gameId: String, language: Language,
                      launchMethod: LaunchMethod): String {

        val clientToken = this.getClientToken(clientId = clientId, platform = platform)
        val startSlotReq = GameValue.StartSlotReq(token = clientToken, username = "", gameId = gameId, language = language,
                launchMethod = launchMethod)

        return when (platform) {
            Platform.SpadeGaming,
            Platform.MicroGaming,
            Platform.TTG,
            Platform.PNG,
            Platform.Pragmatic -> getPlatformApi(platform).startSlotDemo(startSlotReq)
            else  -> error(OnePieceExceptionCode.DATA_FAIL)
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
    fun transfer(clientId: Int, platformUsername: String, platform: Platform, orderId: String, originBalance: BigDecimal, amount: BigDecimal, index: Int = 0): GameValue.TransferResp {


        val clientToken = this.getClientToken(clientId = clientId, platform = platform)
        val transferReq = GameValue.TransferReq(token = clientToken, orderId = orderId, username = platformUsername, amount = amount)


        // 重试两次
        if (index > 2) {
            val type = if (amount.toDouble() > 0) "deposit" else "withdraw"
            // 检查转账是否成功
            val checkTransferReq = GameValue.CheckTransferReq(token = clientToken, username = platformUsername, orderId = orderId, platformOrderId = orderId,
                    amount = amount, type = type)
            return this.checkTransfer(platform = platform, checkTransferReq = checkTransferReq)
        }

        return try {
            val resp = this.getPlatformApi(platform).transfer(transferReq)
//            return resp

            val type = if (amount.toDouble() > 0) "deposit" else "withdraw"
            val checkTransferReq = GameValue.CheckTransferReq(token = clientToken, username = platformUsername, orderId = orderId, platformOrderId = resp.platformOrderId,
                    amount = amount, type = type)
            val checkResp = this.checkTransfer(platform = platform, checkTransferReq = checkTransferReq)

            val balance = when {
                checkResp.transfer && checkResp.balance.toInt() <= 0 -> originBalance.minus(amount.abs())
                checkResp.transfer -> checkResp.balance
                else -> BigDecimal.valueOf(-1)
            }
            return checkResp.copy(balance = balance)
        } catch (e: Exception) {
            log.error("转账失败第${index}次，请求参数：$transferReq ", e)
            this.transfer(clientId, platformUsername, platform, orderId, originBalance, amount, index + 1)
        }

    }

    private fun checkTransfer(platform: Platform, checkTransferReq: GameValue.CheckTransferReq, index: Int = 0): GameValue.TransferResp {

        if (platform == Platform.Kiss918 || platform == Platform.Pussy888) {

            val balanceReq = GameValue.BalanceReq(token = checkTransferReq.token, username = checkTransferReq.username, password = "-")
            val balance = this.getPlatformApi(platform).balance(balanceReq)
            GameValue.TransferResp(transfer = true, platformOrderId = "-", balance = balance)
        }

        try {
            if (index > 2) return GameValue.TransferResp.failed()

            return this.getPlatformApi(platform).checkTransfer(checkTransferReq)
        } catch (e: Exception) {
            log.error("查询转账失败第${index}次，请求参数：$checkTransferReq ", e)
            return this.checkTransfer(platform, checkTransferReq, index + 1)
        }
    }


    /**
     * 查询下注订单
     */
    fun queryBetOrder(clientId: Int, platformUsername: String, platform: Platform, startTime: LocalDateTime, endTime: LocalDateTime): List<BetOrderValue.BetOrderCo> {
        val clientToken = getClientToken(clientId = clientId, platform = platform)

        return when(platform) {
            Platform.Kiss918, Platform.Mega, Platform.Pussy888, Platform.SexyGaming, Platform.Bcs, Platform.AllBet, Platform.TTG -> {
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
            Platform.GGFishing,
            Platform.Pragmatic,
            Platform.TTG,
            Platform.CMD,
            Platform.Evolution,
            Platform.DreamGaming,
            Platform.MicroGaming,
            Platform.SexyGaming,
            Platform.SaGaming,
            Platform.GoldDeluxe -> {
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
            Platform.Mega -> megaService.downApp(clientToken = clientToken as MegaClientToken)
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