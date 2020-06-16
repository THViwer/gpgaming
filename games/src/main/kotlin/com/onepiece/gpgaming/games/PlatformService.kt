package com.onepiece.gpgaming.games

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.token.ClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.beans.value.internet.web.SlotGame
import com.onepiece.gpgaming.core.OnePieceRedisKeyConstant
import com.onepiece.gpgaming.games.http.OkHttpUtil
import com.onepiece.gpgaming.utils.RedisService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.math.BigDecimal

abstract class PlatformService {

    private val log = LoggerFactory.getLogger(PlatformService::class.java)

    @Autowired
    lateinit var okHttpUtil: OkHttpUtil

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var redisService: RedisService

//    @Autowired
//    lateinit var gameConstant: GameConstant

    open fun getRequestUrl(path: String, data: Map<String, Any>): String {
        return ""
    }

    /**
     * 注册
     */
    abstract fun register(registerReq: GameValue.RegisterReq): String

    /**
     * 查询余额
     */
    abstract fun balance(balanceReq: GameValue.BalanceReq): BigDecimal

    /**
     * 转账
     */
    abstract fun transfer(transferReq: GameValue.TransferReq): GameValue.TransferResp

    /**
     * 检查转账状态
     */
    abstract fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameValue.TransferResp

    /**
     * 更新密码
     */
    open fun updatePassword(updatePasswordReq: GameValue.UpdatePasswordReq) {
        error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
    }

    /**
     * 老虎机菜单
     */
    open fun slotGames(token: ClientToken, launch: LaunchMethod, language: Language): List<SlotGame> {
        error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
    }


    /**
     * 启动老虎机
     */
    open fun start(startReq: GameValue.StartReq): String {
        error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
    }

    /**
     * 启动试玩平台
     */
    open fun startDemo(token: ClientToken, language: Language, launch: LaunchMethod): String {
        error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
    }

    /**
     * 启动老虎机游戏
     */
    open fun startSlot(startSlotReq: GameValue.StartSlotReq): String {
        error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
    }

    /**
     * 开始老虎机试玩
     */
    open fun startSlotDemo(startSlotReq: GameValue.StartSlotReq): String {
        error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
    }

    /**
     * 查询订单
     */
    open fun queryBetOrder(betOrderReq: GameValue.BetOrderReq): List<BetOrderValue.BetOrderCo> {
        error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
    }

    // 已废弃
    open fun asynBetOrder(syncBetOrderReq: GameValue.SyncBetOrderReq): String {
        error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
    }

    /**
     * 拉取订单
     */
    open fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {
        error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
    }

    /**
     * 查询报表
     */
    open fun queryReport(reportQueryReq: GameValue.ReportQueryReq): List<GameValue.PlatformReportData> {
        error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
    }

    /**
     * 拉取订单(工具),提供redis缓存
     */
    fun pullByNextId(clientId: Int, platform: Platform, function: (nowId: String) -> Pair<String, List<BetOrderValue.BetOrderCo>>): List<BetOrderValue.BetOrderCo> {

        val redisKey = OnePieceRedisKeyConstant.pullBetOrderLastKey(clientId = clientId, platform = platform)
        val nowId = redisService.get(redisKey, String::class.java) { "0" }!!

        val (nextId, data) = function(nowId)

        redisService.put(redisKey, nextId)

        return data
    }

    open fun getRequestIp(): String {
        return try {
            val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
            var ip = request.getHeader("x-forwarded-for")
            if (ip.isNullOrBlank() || "unknown" == ip.toLowerCase()) {
                ip = request.getHeader("Proxy-Client-IP")
            }

            if (ip.isNullOrBlank() || "unknown" == ip.toLowerCase()) {
                ip = request.getHeader("WL-Proxy-Client-IP")
            }

            if (ip.isNullOrBlank() || "unknown" == ip.toLowerCase()) {
                ip = request.getHeader("HTTP_CLIENT_IP")
            }

            if (ip.isNullOrBlank() || "unknown" == ip.toLowerCase()) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR")
            }

            if (ip.isNullOrBlank() || "unknown" == ip.toLowerCase()) {
                ip = request.remoteAddr
            }

            ip.split(",").first()
        } catch (e: Exception) {
            "12.213.1.24"
        }
    }

}