package com.onepiece.gpgaming.games

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.PlatformBind
import com.onepiece.gpgaming.beans.model.PullOrderTask
import com.onepiece.gpgaming.beans.model.token.ClientToken
import com.onepiece.gpgaming.beans.model.token.MegaClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.beans.value.internet.web.SlotGame
import com.onepiece.gpgaming.core.OnePieceRedisKeyConstant
import com.onepiece.gpgaming.core.dao.PullOrderTaskDao
import com.onepiece.gpgaming.core.service.GamePlatformService
import com.onepiece.gpgaming.core.service.PlatformBindService
import com.onepiece.gpgaming.core.service.PlatformMemberService
import com.onepiece.gpgaming.core.utils.PlatformUsernameUtil
import com.onepiece.gpgaming.games.combination.AsiaGamingService
import com.onepiece.gpgaming.games.combination.MicroGamingService
import com.onepiece.gpgaming.games.combination.PlaytechService
import com.onepiece.gpgaming.games.fishing.GGFishingService
import com.onepiece.gpgaming.games.http.GameResponse
import com.onepiece.gpgaming.games.http.OKResponse
import com.onepiece.gpgaming.games.live.AllBetService
import com.onepiece.gpgaming.games.live.DreamGamingService
import com.onepiece.gpgaming.games.live.EBetService
import com.onepiece.gpgaming.games.live.EvolutionService
import com.onepiece.gpgaming.games.live.SaGamingService
import com.onepiece.gpgaming.games.live.SexyGamingService
import com.onepiece.gpgaming.games.slot.GamePlayService
import com.onepiece.gpgaming.games.slot.Kiss918Service
import com.onepiece.gpgaming.games.slot.MegaService
import com.onepiece.gpgaming.games.slot.PNGService
import com.onepiece.gpgaming.games.slot.PragmaticService
import com.onepiece.gpgaming.games.slot.Pussy888Service
import com.onepiece.gpgaming.games.slot.SimplePlayService
import com.onepiece.gpgaming.games.slot.SpadeGamingService
import com.onepiece.gpgaming.games.slot.TTGService
import com.onepiece.gpgaming.games.sport.BcsService
import com.onepiece.gpgaming.games.sport.BtiService
import com.onepiece.gpgaming.games.sport.CMDService
import com.onepiece.gpgaming.games.sport.LbcService
import com.onepiece.gpgaming.utils.RedisService
import com.onepiece.gpgaming.utils.RequestUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class GameApi(
        private val platformBindService: PlatformBindService,
        private val platformMemberService: PlatformMemberService,
        private val redisService: RedisService,
        private val gamePlatformService: GamePlatformService,

        // slot
//        private val jokerService: JokerService,
        private val kiss918Service: Kiss918Service,
        private val pussy888Service: Pussy888Service,
        private val megaService: MegaService,
        private val pragmaticService: PragmaticService,
        private val spadeGamingService: SpadeGamingService,
        private val ttgService: TTGService,
        private val microGameService: MicroGamingService,
        private val gamePlayService: GamePlayService,
        private val simplePlayService: SimplePlayService,

        // live game
//        private val goldDeluxeService: GoldDeluxeService,
        private val evolutionService: EvolutionService,
        private val sexyGamingService: SexyGamingService,
//        private val fggService: FggService,
        private val allBetService: AllBetService,
        private val dreamGamingService: DreamGamingService,
        private val pngService: PNGService,
        private val eBetService: EBetService,

        // sport
//        private val sboService: SboService,
        private val lbcService: LbcService,
        private val bcsService: BcsService,
        private val cmdService: CMDService,
        private val btiService: BtiService,

        // fishing
        private val ggFishingService: GGFishingService,

        // slot and live
        private val playtechService: PlaytechService,
        private val saGamingService: SaGamingService,
        private val asiaGamingService: AsiaGamingService,

        private val pullOrderTaskDao: PullOrderTaskDao
) {

    private val log = LoggerFactory.getLogger(GameApi::class.java)

    private fun getPlatformApi(platform: Platform): PlatformService {
        return when (platform) {

            // slot
//            Platform.Joker -> jokerService
            Platform.Kiss918 -> kiss918Service
            Platform.Pussy888 -> pussy888Service
            Platform.Mega -> megaService
            Platform.Pragmatic -> pragmaticService
            Platform.SpadeGaming -> spadeGamingService
            Platform.TTG -> ttgService
            Platform.GamePlay -> gamePlayService
            Platform.SimplePlay -> simplePlayService

            // live game
//            Platform.Fgg -> fggService
            Platform.Evolution -> evolutionService
            Platform.AllBet -> allBetService
            Platform.DreamGaming -> dreamGamingService
//            Platform.GoldDeluxe -> goldDeluxeService
            Platform.SexyGaming -> sexyGamingService
            Platform.PNG -> pngService
            Platform.SaGaming -> saGamingService
            Platform.EBet -> eBetService

            // sport
            Platform.Lbc -> lbcService
            Platform.Bcs -> bcsService
            Platform.CMD -> cmdService
            Platform.BTI -> btiService

            // fishing
            Platform.GGFishing -> ggFishingService

            // slot and live
            Platform.MicroGaming -> microGameService
            Platform.MicroGamingLive -> microGameService
            Platform.AsiaGamingSlot -> asiaGamingService
            Platform.AsiaGamingLive -> asiaGamingService
            Platform.PlaytechSlot -> playtechService
            Platform.PlaytechLive -> playtechService

            else -> error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
        }
    }

    private fun bindLogHead(clientId: Int, memberId: Int, platform: Platform, method: String): String {
        return "clientId=$clientId,memberId=$memberId,platform=$platform => $method"
    }

    private fun <T> useRemoteLog(
            clientId: Int,
            platform: Platform,
            taskType: PullOrderTask.OrderTaskType,
            head: String,
            gameResponse: GameResponse<T>,
            save: Boolean = true
    ): T {

        val okResponse = gameResponse.okResponse

        val logInfo = "\r\n--------start--------\r\n" +
                "---- use remote $head ---- \r\n" +
                "---- nonce： ${gameResponse.okResponse.okParam.nonce} ---- \r\n" +
                "---- 请求是否成功: ${gameResponse.okResponse.ok} ---- \r\n" +
                "---- 请求方式: ${gameResponse.okResponse.method} ---- \r\n" +
                "---- 请求地址: ${okResponse.url} ---- \r\n" +
                "---- 请求头: ${okResponse.headers} ---- \r\n" +
                "---- 请求参数: ${okResponse.param} ---- \r\n" +
                "---- 表单数据: ${okResponse.okParam.formParam} ---- \r\n" +
                "---- 响应参数: ${okResponse.response} ---- \r\n" +
                "--------end--------\r\n"

        if (save) {
            try {
                this.saveErrMsg(clientId = clientId, platform = platform, taskType = taskType, okResponse = gameResponse.okResponse, logInfo = logInfo)
            } catch (e: Exception) {
                log.error("保存异常信息失败", e)
            }
        }


        if (okResponse.ok) {
            log.info(logInfo)
            return gameResponse.data!!
        }

        log.error(logInfo)
        error(OnePieceExceptionCode.PLATFORM_REQUEST_ERROR)

    }

    private fun <T> useRemoteLogByPull(
            clientId: Int,
            platform: Platform,
            taskType: PullOrderTask.OrderTaskType,
            head: String,
            gameResponse: GameResponse<T>,
            save: Boolean = true
    ): String {

        val okResponse = gameResponse.okResponse

        val logInfo = "\r\n--------start--------\r\n" +
                "---- use remote $head ---- \r\n" +
                "---- nonce： ${gameResponse.okResponse.okParam.nonce} ---- \r\n" +
                "---- 请求是否成功: ${gameResponse.okResponse.ok} ---- \r\n" +
                "---- 请求方式: ${gameResponse.okResponse.method} ---- \r\n" +
                "---- 请求地址: ${okResponse.url} ---- \r\n" +
                "---- 请求头: ${okResponse.headers} ---- \r\n" +
                "---- 请求参数: ${okResponse.param} ---- \r\n" +
                "---- 表单数据: ${okResponse.okParam.formParam} ---- \r\n" +
                "---- 响应参数: ${okResponse.response} ---- \r\n" +
                "--------end--------\r\n"

        if (save) {
            try {
                this.saveErrMsg(clientId = clientId, platform = platform, taskType = taskType, okResponse = gameResponse.okResponse, logInfo = logInfo)
            } catch (e: Exception) {
                log.error("保存异常信息失败", e)
            }
        }

        if (okResponse.ok) {
            log.info(logInfo)
        } else {
            log.error(logInfo)
        }

        return logInfo
    }

    private fun saveErrMsg(clientId: Int, platform: Platform, okResponse: OKResponse, taskType: PullOrderTask.OrderTaskType, logInfo: String) {
        // TODO 请求平台 失败的时候才记录
        if (!okResponse.ok) {
            val formParam = okResponse.okParam.formParam.map { "${it.key}=${it.value}" }.joinToString(separator = "&")
            val headers = okResponse.okParam.headers.map { "${it.key}=${it.value}" }.joinToString(separator = "&")

            val now = LocalDateTime.now()
            val task = PullOrderTask(id = -1, clientId = clientId, platform = platform, param = okResponse.param,
                    path = okResponse.url, response = okResponse.response, type = taskType, status = okResponse.status,
                    startTime = now, endTime = now, message = okResponse.message ?: "", formParam = formParam,
                    headers = headers, nonce = okResponse.okParam.nonce, logInfo = logInfo)
            pullOrderTaskDao.create(task)
        }
    }

    /**
     * 注册账号
     */
    fun register(clientId: Int, memberId: Int, platform: Platform, name: String) {
        check(gamePlatformService.all().first { it.platform == platform }.status == Status.Normal) { OnePieceExceptionCode.PLATFORM_MAINTAIN }

        log.info("lock redis key = ${clientId}:$memberId:$platform}")
        redisService.lock(key = "register:${clientId}:$memberId:$platform", error = {
            log.error("注册账号：register:${clientId}:$memberId:$platform, 已被锁定")
            Thread.sleep(2000)
        }) {
            log.error("注册账号：register:${clientId}:$memberId:$platform, 没有被锁定")

            val has = platformMemberService.find(memberId, platform)
            if (has != null) return@lock

            log.info("用户：$memberId, 开始注册平台:$platform 现在时间：${LocalDateTime.now()}")

            // 生成用户名
            val (generatorUsername, generatorPassword) = PlatformUsernameUtil.generatorPlatformUsername(clientId = clientId, memberId = memberId, platform = platform)

            // 获得配置信息
            val clientToken = this.getClientToken(clientId = clientId, platform = platform)

            // 注册账号
            val registerReq = GameValue.RegisterReq(token = clientToken, username = generatorUsername, password = generatorPassword, name = name,
                    clientId = clientId, memberId = memberId)
            val gameResponse = getPlatformApi(platform).register(registerReq)

            try {
                val platformUsername = this.useRemoteLog(clientId = clientId, platform = platform, head = bindLogHead(clientId, memberId, platform, "register"),
                        gameResponse = gameResponse, taskType = PullOrderTask.OrderTaskType.API_REGISTER)
                platformMemberService.create(clientId = clientId, memberId = memberId, platform = platform, platformUsername = platformUsername, platformPassword = generatorPassword)
            } catch (e: Exception) {

                if (platform == Platform.Kiss918 || platform == Platform.Pussy888) {
                    if (e.message != "java.lang.IllegalStateException: 4010" && e.message != "4010") {
                        throw e
                    } else {
                        // NOTHING
                    }
                } else {
                    throw e
                }
            }

        }
    }

    /**
     * 修改密码
     */
    fun updatePassword(clientId: Int, platform: Platform, username: String, password: String) {
        check(gamePlatformService.all().first { it.platform == platform }.status == Status.Normal) { OnePieceExceptionCode.PLATFORM_MAINTAIN }

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
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    /**
     * 老虎机游戏列表
     */
    fun slotGames(clientId: Int, platform: Platform, launch: LaunchMethod, language: Language): List<SlotGame> {

        val redisKey = OnePieceRedisKeyConstant.slotGames(platform = platform, launch = launch)

//        return redisService.getList(key = redisKey, clz = SlotGame::class.java, timeout = 3600) {
        val clientToken = this.getClientToken(clientId = clientId, platform = platform)

        return when (platform) {
            Platform.Joker,
            Platform.Pragmatic,
            Platform.TTG,
            Platform.SpadeGaming -> getPlatformApi(platform).slotGames(token = clientToken, launch = launch, language = language)
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }

//        }
    }

    private fun getRequestDomain(): String {
        val request = RequestUtil.getRequest()
        return request.requestURI
    }

    /**
     * 开始游戏(平台)
     */
    fun start(clientId: Int, memberId: Int, platformUsername: String, platformPassword: String, platform: Platform, launch: LaunchMethod = LaunchMethod.Web, language: Language): String {

        check(gamePlatformService.all().first { it.platform == platform }.status == Status.Normal) { OnePieceExceptionCode.PLATFORM_MAINTAIN }

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
            Platform.MicroGamingLive,
            Platform.AsiaGamingLive,
            Platform.EBet,
            Platform.BTI,
            Platform.Bcs -> {
                val startReq = GameValue.StartReq(token = clientToken, username = platformUsername, launch = launch, language = language, password = platformPassword, redirectUrl = getRequestDomain())
                val gameResponse = this.getPlatformApi(platform).start(startReq)
                this.useRemoteLog(clientId = clientId, platform = platform, head = bindLogHead(clientId, memberId, platform, "start"), gameResponse = gameResponse,
                        taskType = PullOrderTask.OrderTaskType.API_LAUNCH_GAME)
            }
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    /**
     * 开始平台试玩
     */
    fun startDemo(clientId: Int, platform: Platform, language: Language, launch: LaunchMethod): String {
        val clientToken = this.getClientToken(clientId = clientId, platform = platform)

        return when (platform) {
            Platform.Lbc,
            Platform.Bcs,
            Platform.BTI,
                    Platform . CMD -> {
                val gameResponse = this.getPlatformApi(platform).startDemo(token = clientToken, language = language, launch = launch)
                this.useRemoteLog(clientId = clientId, platform = platform, head = this.bindLogHead(clientId, -1, platform, "start sport demo"),
                        gameResponse = gameResponse, taskType = PullOrderTask.OrderTaskType.API_LAUNCH_GAME)
            }
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    /**
     * 开始游戏(老虎机)
     */
    fun start(clientId: Int, memberId: Int, platformUsername: String, platformPassword: String, platform: Platform, gameId: String, language: Language,
              launchMethod: LaunchMethod): String {

        val clientToken = this.getClientToken(clientId = clientId, platform = platform)

        //TODO 跳转url
        val startSlotReq = GameValue.StartSlotReq(token = clientToken, username = platformUsername, gameId = gameId, language = language,
                launchMethod = launchMethod, password = platformPassword, redirectUrl = getRequestDomain())
        return when (platform) {
            Platform.Joker,
            Platform.Pragmatic,
            Platform.TTG,
            Platform.MicroGaming,
            Platform.PlaytechSlot,
            Platform.PNG,
            Platform.GamePlay,
            Platform.SimplePlay,
            Platform.SpadeGaming,
            Platform.AsiaGamingSlot -> {
                val gameResponse = getPlatformApi(platform).startSlot(startSlotReq)

                this.useRemoteLog(clientId = clientId, platform = platform, head = bindLogHead(clientId, memberId, platform, "start"), gameResponse = gameResponse,
                        taskType = PullOrderTask.OrderTaskType.API_LAUNCH_GAME)
            }
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
                launchMethod = launchMethod, password = "-", redirectUrl = getRequestDomain())

        return when (platform) {
            Platform.SpadeGaming,
            Platform.MicroGaming,
            Platform.TTG,
            Platform.PNG,
            Platform.GamePlay,
            Platform.SimplePlay,
            Platform.Pragmatic -> {
                val gameResponse = getPlatformApi(platform).startSlotDemo(startSlotReq)
                gameResponse.data ?: error(OnePieceExceptionCode.SYSTEM)
            }
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }

    }

    fun getBalanceIncludeOutstanding(clientId: Int, memberId: Int, platformUsername: String, platformPassword: String, platform: Platform): Pair<BigDecimal, BigDecimal> {
        check(gamePlatformService.all().first { it.platform == platform }.status == Status.Normal) { OnePieceExceptionCode.PLATFORM_MAINTAIN }

        val clientToken = this.getClientToken(clientId = clientId, platform = platform)


        val balanceReq = GameValue.BalanceReq(token = clientToken, username = platformUsername, password = platformPassword)
        val gameResponse = this.getPlatformApi(platform).balance(balanceReq)

        val balance = this.useRemoteLog(clientId = clientId, platform = platform, head = bindLogHead(clientId, memberId, platform, "balance"),
                gameResponse = gameResponse, taskType = PullOrderTask.OrderTaskType.API_BALANCE)
                .setScale(2, BigDecimal.ROUND_DOWN)

        return balance to (gameResponse.outstanding)
    }


    /**
     * 查询会员余额
     */
    fun balance(clientId: Int, memberId: Int, platformUsername: String, platformPassword: String, platform: Platform): BigDecimal {
        check(gamePlatformService.all().first { it.platform == platform }.status == Status.Normal) { OnePieceExceptionCode.PLATFORM_MAINTAIN }

        val clientToken = this.getClientToken(clientId = clientId, platform = platform)


        val balanceReq = GameValue.BalanceReq(token = clientToken, username = platformUsername, password = platformPassword)
        val gameResponse = this.getPlatformApi(platform).balance(balanceReq)

        return this.useRemoteLog(clientId = clientId, platform = platform, head = bindLogHead(clientId, memberId, platform, "balance"),
                gameResponse = gameResponse, taskType = PullOrderTask.OrderTaskType.API_BALANCE)
                .setScale(2, BigDecimal.ROUND_DOWN)
    }


    /**
     * 转账
     */
    fun transfer(clientId: Int,
                 memberId: Int,
                 platformUsername: String,
                 platformPassword: String,
                 platform: Platform,
                 orderId: String,
                 originBalance: BigDecimal,
                 amount: BigDecimal,
                 index: Int = 0
    ): GameValue.TransferResp {

        check(gamePlatformService.all().first { it.platform == platform }.status == Status.Normal) { OnePieceExceptionCode.PLATFORM_MAINTAIN }

        val msg = if (amount.toDouble() > 0) {
            "中心 => $platform"
        } else {
            "$platform => 中心"
        }
        log.info("转账开始: 用户Id：$memberId, 平台用户名=$platformUsername, 订单Id:$orderId, $msg, 第${index}次转账，clientId=$clientId,  平台：$platform,  金额：$amount, 平台金额:$originBalance")

        val clientToken = this.getClientToken(clientId = clientId, platform = platform)
        val transferReq = GameValue.TransferReq(token = clientToken, orderId = orderId, username = platformUsername, amount = amount, password = platformPassword)

        // 重试两次
        if (index > 2) {
            val type = if (amount.toDouble() > 0) "deposit" else "withdraw"
            // 检查转账是否成功
            val checkTransferReq = GameValue.CheckTransferReq(token = clientToken, username = platformUsername, orderId = orderId, platformOrderId = orderId,
                    amount = amount, type = type)
            return this.checkTransfer(clientId = clientId, memberId = memberId, platform = platform, checkTransferReq = checkTransferReq)
        }

        return try {
            val gameResponse = this.getPlatformApi(platform).transfer(transferReq)
            val resp = this.useRemoteLog(clientId = clientId, platform = platform, head = this.bindLogHead(clientId, memberId, platform, "transfer"),
                    gameResponse = gameResponse, taskType = PullOrderTask.OrderTaskType.API_TRANSFER)

            val type = if (amount.toDouble() > 0) "deposit" else "withdraw"
            val checkTransferReq = GameValue.CheckTransferReq(token = clientToken, username = platformUsername, orderId = orderId, platformOrderId = resp.platformOrderId,
                    amount = amount, type = type)

            //TODO 如果是kiss918和Pussy888 则不能check
            val checkResp = when {
                platform == Platform.Kiss918 || platform == Platform.Pussy888 -> {
                    if (resp.transfer)
                        resp
                    else
                        this.checkTransfer(clientId = clientId, memberId = memberId, platform = platform, checkTransferReq = checkTransferReq)
                }
                resp.transfer -> this.checkTransfer(clientId = clientId, memberId = memberId, platform = platform, checkTransferReq = checkTransferReq)
                else -> resp
            }

            val balance = when {
                platform == Platform.Kiss918 || platform == Platform.Pussy888 -> originBalance.plus(amount)
                checkResp.transfer && checkResp.balance.toInt() <= 0 -> originBalance.plus(amount)
                checkResp.transfer -> checkResp.balance
                else -> BigDecimal.valueOf(-1)
            }
            return checkResp.copy(balance = balance, msg = gameResponse.okResponse.message ?: "")
        } catch (e: Exception) {
            log.error("转账失败第${index}次，请求参数：$transferReq ", e)

            if ((platform == Platform.Kiss918 || platform == Platform.Pussy888) && (e.message == "5013" || e.message == "java.lang.IllegalStateException: 5013")) {
                return GameValue.TransferResp.failed()
            }

            this.transfer(clientId, memberId, platformUsername, platformPassword, platform, orderId, originBalance, amount, index + 1)
        }

    }

    fun checkTransfer(clientId: Int, memberId: Int, platform: Platform, checkTransferReq: GameValue.CheckTransferReq, index: Int = 0): GameValue.TransferResp {

//        if (platform == Platform.Kiss918 || platform == Platform.Pussy888) {
//
//            val balanceReq = GameValue.BalanceReq(token = checkTransferReq.token, username = checkTransferReq.username, password = "-")
//            val balance = this.getPlatformApi(platform).balance(balanceReq)
//            GameValue.TransferResp(transfer = true, platformOrderId = "-", balance = balance)
//        }
        try {
            if (index > 2) return GameValue.TransferResp.failed()

            val gameResponse = this.getPlatformApi(platform).checkTransfer(checkTransferReq)
            return this.useRemoteLog(clientId = clientId, platform = platform, head = this.bindLogHead(clientId, memberId, platform, "check transfer"),
                    gameResponse = gameResponse, taskType = PullOrderTask.OrderTaskType.API_TRANSFER_CHECK)
        } catch (e: Exception) {
            log.error("查询转账失败第${index}次，请求参数：$checkTransferReq ", e)
            return this.checkTransfer(clientId, memberId, platform, checkTransferReq, index + 1)
        }
    }


    /**
     * 查询下注订单
     */
    fun queryBetOrder(clientId: Int, memberId: Int, platformUsername: String, platform: Platform, startTime: LocalDateTime, endTime: LocalDateTime): List<BetOrderValue.BetOrderCo> {
        val clientToken = getClientToken(clientId = clientId, platform = platform)

        return when (platform) {
            Platform.Kiss918, Platform.Mega, Platform.Pussy888, Platform.SexyGaming, Platform.Bcs, Platform.AllBet, Platform.TTG -> {
                val betOrderReq = GameValue.BetOrderReq(token = clientToken, startTime = startTime, endTime = endTime, username = platformUsername)
                val gameResponse = getPlatformApi(platform).queryBetOrder(betOrderReq)
                this.useRemoteLog(clientId = clientId, platform = platform, head = this.bindLogHead(clientId, memberId, platform, "query bet order"), gameResponse = gameResponse,
                        taskType = PullOrderTask.OrderTaskType.API_QUERY_BET)
            }
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    /**
     * 查询报表
     */
    fun queryReport(clientId: Int, platform: Platform, startDate: LocalDate): List<GameValue.PlatformReportData> {
        val clientToken = getClientToken(clientId = clientId, platform = platform)

        return when (platform) {
            Platform.Kiss918, Platform.Pussy888, Platform.Mega -> {
                val req = GameValue.ReportQueryReq(token = clientToken, startDate = startDate)
                val gameResponse = getPlatformApi(platform).queryReport(req)
                this.useRemoteLog(clientId = clientId, platform = platform, head = this.bindLogHead(clientId, -1, platform, "query report"),
                        gameResponse = gameResponse, taskType = PullOrderTask.OrderTaskType.API_QUERY_REPORT)
            }
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    /**
     * 同步订单
     */
    fun pullBets(platformBind: PlatformBind, startTime: LocalDateTime, endTime: LocalDateTime): GameResponse<List<BetOrderValue.BetOrderCo>> {

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
            Platform.MicroGamingLive,
            Platform.SexyGaming,
            Platform.SaGaming,
            Platform.GamePlay,
            Platform.SimplePlay,
            Platform.GoldDeluxe,
            Platform.AsiaGamingLive,
            Platform.AsiaGamingSlot,
            Platform.PlaytechSlot,
            Platform.EBet,
            Platform.PlaytechLive -> {
                val pullBetOrderReq = GameValue.PullBetOrderReq(clientId = platformBind.clientId, startTime = startTime, endTime = endTime, token = platformBind.clientToken,
                        platform = platformBind.platform)
                val gameResponse = getPlatformApi(platformBind.platform).pullBetOrders(pullBetOrderReq)
                val logInfo = this.useRemoteLogByPull(clientId = platformBind.clientId, platform = platformBind.platform, head = "clientId=${platformBind.clientId},platform=${platformBind.platform} -> pullBets",
                        gameResponse = gameResponse, taskType = PullOrderTask.OrderTaskType.API_PULL_BET, save = false)

                gameResponse.copy(logInfo = logInfo)
            }
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }


    }

    /**
     * 查询下载地址
     */
    fun getAppDownload(clientId: Int, platform: Platform): String {
        val clientToken = getClientToken(clientId = clientId, platform = platform)

        return when (platform) {
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
        return when {
            clientId < 10 -> "00$clientId"
            clientId < 100 -> "0$clientId"
            else -> "$clientId"
        }.let {
            "A$it$memberId"
        }
    }

}
