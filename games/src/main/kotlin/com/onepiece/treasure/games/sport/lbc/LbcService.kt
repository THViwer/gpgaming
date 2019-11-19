package com.onepiece.treasure.games.sport.lbc

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.DefaultClientToken
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformApi
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.utils.RedisService
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class LbcService : PlatformApi() {


    fun checkCode(code: Int) {
        check(code == 0) { OnePieceExceptionCode.PLATFORM_METHOD_FAIL }
    }

    override fun register(registerReq: GameValue.RegisterReq): String {
        val url = LbcBuild.instance("/api/CreateMember")
                .set("OpCode", (registerReq.token as DefaultClientToken).appId)
                .set("PlayerName", registerReq.username)
                .set("FirstName", registerReq.name)
                .set("LastName", registerReq.name)
                .set("OddsType", "1")
                .set("MaxTransfer", "1000")
                .set("MinTransfer", "1")
                .build(token = registerReq.token)

        val result = okHttpUtil.doGet(url = url, clz = LbcValue.RegisterResult::class.java)
        this.checkCode(result.errorCode)
        return registerReq.username

    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {

        val url = LbcBuild.instance("/api/CheckUserBalance")
                .set("OpCode", (balanceReq.token as DefaultClientToken).appId)
                .set("PlayerName", balanceReq.username)
                .build(token = balanceReq.token)


        val result = okHttpUtil.doGet(url = url, clz = LbcValue.CheckBalanceRespond::class.java)
        this.checkCode(result.errorCode)
        return result.data.first().balance
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {

        val direct = if (transferReq.amount.toDouble() > 0) 1 else 0

        val url = LbcBuild.instance("/api/FundTransfer")
                .set("OpCode", (transferReq.token as DefaultClientToken).appId)
                .set("playerName", transferReq.username)
                .set("OpTransId", transferReq.orderId)
                .set("amount", transferReq.amount.abs())
                .set("Direction", direct)
                .build(token = transferReq.token)

        val result= okHttpUtil.doGet(url = url, clz = LbcValue.Transfer::class.java)
        this.checkCode(result.errorCode)

        return transferReq.orderId
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): Boolean {


        val url = LbcBuild.instance("/api/CheckFundTransfer")
                .set("OpCode", (checkTransferReq.token as DefaultClientToken).appId)
                .set("playerName", checkTransferReq.username)
                .set("OpTransId", checkTransferReq.orderId)
                .build(token = checkTransferReq.token)

        val result = okHttpUtil.doGet(url = url, clz = LbcValue.CheckTransfer::class.java)
        this.checkCode(result.errorCode)

        return result.data.status == 0

    }

    override fun start(startReq: GameValue.StartReq): String {

        val url = LbcBuild.instance("/api/Login")
                .set("OpCode", (startReq.token as DefaultClientToken).appId)
                .set("playerName", startReq.username)
                .build(token = startReq.token)

        val result = okHttpUtil.doGet(url = url, clz = LbcValue.Login::class.java)
        this.checkCode(result.errorCode)

        val lang = when (startReq.language) {
            Language.EN -> "en"
            Language.CN -> "cs"
            Language.ID -> "id"
            Language.TH -> "th"
            Language.VI -> "vn"
            else -> "en"
        }

        return when (startReq.startPlatform) {
            LaunchMethod.Web -> "${GameConstant.LBC_START_URL}${result.sessionToken}&lang=$lang"
            LaunchMethod.Wap -> "${GameConstant.LBC_START_MOBILE_URL}${result.sessionToken}&lang=$lang"
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {
        val token = pullBetOrderReq.token as DefaultClientToken

        val redisKey = OnePieceRedisKeyConstant.pullBetOrderLastKey(clientId = pullBetOrderReq.clientId, platform = Platform.Lbc)
        val lastVersionKey = redisService.get(redisKey, Int::class.java) { 0 }!!

        val url = LbcBuild.instance("/api/GetSportBetLog")
                .set("OpCode",token.appId)
                .set("LastVersionKey", lastVersionKey)
                .build(token = token)

        val lbcBetOrder = okHttpUtil.doGet(url = url, clz = LbcBetOrder::class.java)
        val orders = lbcBetOrder.getBetOrders(objectMapper)

        redisService.put(redisKey, lbcBetOrder.lastVersionKey)

        return orders
    }
}