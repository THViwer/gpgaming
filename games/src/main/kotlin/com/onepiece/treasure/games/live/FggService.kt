package com.onepiece.treasure.games.live

import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.DefaultClientToken
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.PlatformUsernameUtil
import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformApi
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 *
 * 货币编码
 * USD 美金、MYR 马币、CNY 人民币、HKD 港币、THB 泰珠、JPY 日元、KHR 柬埔寨瑞尔、KHR_000 1000 柬埔寨瑞尔、VND 越南盾、VND_000 1000 越南盾、KRW 韩元
 * IDR 印尼卢比、IDR_000 1000 印尼卢比、SGD 新加坡元、KES 肯尼亚先令、MMK 缅甸元、MMK_000 1000 缅甸元、AUD 澳元、COP 哥伦比亚比、MNT 蒙古图格里克
 * PHP 菲律宾比索、INR 印度卢比、GBP 英镑、EUR 欧元、IRR 伊朗里亚尔、RUB 俄罗斯卢
 */
/**
 * 语言编码：
 * zh_CN Chinese(simple)
 * zh_TW Chinese(Traditional)
 * en_US English
 * th_TH Thailand
 * vi_VN Vietnam
 * id_ID Indonesia
 * ja_JP Japanese
 * ko_KR Korean
 */

@Service
class FggService: PlatformApi() {

    override fun register(registerReq: GameValue.RegisterReq): String {
        val startReq = GameValue.StartReq(token = registerReq.token, username = registerReq.username, startPlatform = LaunchMethod.Web, language = Language.EN)
        this.start(startReq)

        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {

        val token = balanceReq.token as DefaultClientToken
        val param = """
            {
                "Key": "${token.appId}",
                "Secret": "${token.key}",
                "Account": "${balanceReq.username}"
            }
        """.trimIndent()

        val result = okHttpUtil.doPostJson(url = "${GameConstant.FGG_API_URL}/GetBalance", data = param, clz = FggValue.Result::class.java)
        result.checkErrorCode()

        return result.data["Balance"]?.toString()?.toBigDecimal()?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {

        val token = transferReq.token as DefaultClientToken
        val param = """
            {
                "Key": "${token.appId}",
                "Secret": "${token.key}",
                "Account": "${transferReq.username}",
                "Amount": ${transferReq.amount},
                "SerialNumber": "${transferReq.orderId}"
            }
        """.trimIndent()
        val result = okHttpUtil.doPostJson(url = "${GameConstant.FGG_API_URL}/Transfer", data = param, clz = FggValue.Result::class.java)
        result.checkErrorCode()

        return transferReq.orderId
    }


    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): Boolean {
        val token = checkTransferReq.token as DefaultClientToken
        val param = """
            {
                "Key": "${token.appId}",
                "Secret": "${token.key}",
                "SerialNumber": "${checkTransferReq.orderId}"
            }
        """.trimIndent()
        val result = okHttpUtil.doPostJson(url = "${GameConstant.FGG_API_URL}/GetTransferInfo", data = param, clz = FggValue.Result::class.java)
        result.checkErrorCode()

        return result.data["Exist"] == true
    }

    override fun start(startReq: GameValue.StartReq): String {
        val token = startReq.token as DefaultClientToken

        val clientType = when (startReq.startPlatform) {
            LaunchMethod.Web -> "PC"
            LaunchMethod.Wap -> "Mobile"
            else -> "App"
        }

        val lang = when (startReq.language) {
            Language.VI -> "vi_VN"
            Language.ID -> "id_ID"
            Language.CN -> "zh_CN"
            else -> "en_US"
        }

        val param = """
           {
                "Key": "${token.appId}",
                "Secret": "${token.key}",
                "Account": "${startReq.username}",
                "Currency": "MYR",
                "LimitID": "3",
                "ClientType": "$clientType",
                "Lang": "$lang  ",
                "GameID": "0",
                
           } 
            
        """.trimIndent()

        val result = okHttpUtil.doPostJson(url = "${GameConstant.FGG_API_URL}/GetGameUrl", data = param, clz = FggValue.Result::class.java)
        result.checkErrorCode()

        return result.data["Url"]?.toString() ?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
    }


    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {

        val redisKey = OnePieceRedisKeyConstant.pullBetOrderLastKey(clientId = pullBetOrderReq.clientId, platform = Platform.Fgg)
        val sortNo = redisService.get(redisKey, Int::class.java) { 0 }!!

        val token = pullBetOrderReq.token as DefaultClientToken
        val param = """
            {
                "Key": "${token.appId}",
                "Secret": "${token.key}",
                "SortNo": "$sortNo",
                "Rows": 1000
            }
        """.trimIndent()
        val result = okHttpUtil.doPostJson(url = "${GameConstant.FGG_API_URL}/GetBets", data = param, clz = FggValue.Result::class.java)
        result.checkErrorCode()


        val nextSortNo = result.data["SortNo"]?.toString()?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
        val bets = result.data["Bets"]?.let { it as List<Map<String, Any>> } ?: emptyList()
        if (bets.isEmpty()) return emptyList()

        val orders = bets.map {  bet ->
            val orderId = bet["BetID"]?.toString()?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
            val username = bet["Account"]?.toString()?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
            val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.Fgg, platformUsername = username)
            val betAmount = bet["Turnover"]?.toString()?.toBigDecimal()?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
            val winAmount = bet["TotalPay"]?.toString()?.toBigDecimal()?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)

            val betTime = bet["BetTime"]?.toString()
                    ?.toLong()
                    ?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.of("Asia/Shanghai")) }
                    ?: error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)

            val originData = objectMapper.writeValueAsString(bet)
            BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, orderId = orderId, betAmount = betAmount, winAmount = winAmount,
                    betTime = betTime, settleTime = betTime, platform = Platform.Fgg, originData = originData)
        }

        redisService.put(redisKey, nextSortNo)


        return orders
    }

}