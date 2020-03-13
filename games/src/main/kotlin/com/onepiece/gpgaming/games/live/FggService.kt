package com.onepiece.gpgaming.games.live

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.token.DefaultClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.bet.MapUtil
import org.slf4j.LoggerFactory
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
class FggService: PlatformService() {

    private val log = LoggerFactory.getLogger(FggService::class.java)

    fun startPostJson(clientToken: DefaultClientToken, method: String, data: String): MapUtil {

        val url = "${clientToken.apiPath}/Game/$method"
        val result = okHttpUtil.doPostJson(url = url, data = data, clz = FggValue.Result::class.java)
        check(result.errorCode.isBlank()) {
            log.error("fgg platform error: ${result.errorCode}, ${result.errorDesc}")
            OnePieceExceptionCode.PLATFORM_DATA_FAIL
        }

        return result.mapUtil
    }


    override fun register(registerReq: GameValue.RegisterReq): String {
        val startReq = GameValue.StartReq(token = registerReq.token, username = registerReq.username, launch = LaunchMethod.Web, language = Language.EN, password = "-")
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

        val mapUtil = this.startPostJson(clientToken = token, method = "GetBalance", data = param)
        return mapUtil.asBigDecimal("Balance")
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameValue.TransferResp {

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
        val mapUtil = this.startPostJson(clientToken = token, method = "Transfer", data = param)
        val balance = mapUtil.asBigDecimal("Balance")
        return GameValue.TransferResp.successful(balance = balance)
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameValue.TransferResp {
        val token = checkTransferReq.token as DefaultClientToken
        val param = """
            {
                "Key": "${token.appId}",
                "Secret": "${token.key}",
                "SerialNumber": "${checkTransferReq.orderId}"
            }
        """.trimIndent()
        val mapUtil = this.startPostJson(clientToken = token, method = "GetTransferInfo", data = param)
        val successful = mapUtil.asBoolean("Exist")
        return GameValue.TransferResp.of(successful)
    }

    override fun start(startReq: GameValue.StartReq): String {
        val token = startReq.token as DefaultClientToken

        val clientType = when (startReq.launch) {
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
                "Lang": "$lang",
                "GameID": "0",
                
           } 
            
        """.trimIndent()

        val mapUtil = this.startPostJson(clientToken = token, method = "GetGameUrl", data = param)
        return mapUtil.asString("Url")
    }


    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): List<BetOrderValue.BetOrderCo> {
        val token = pullBetOrderReq.token as DefaultClientToken

        return this.pullByNextId(clientId = pullBetOrderReq.clientId, platform = Platform.Fgg) { startId ->
            val param = """
            {
                "Key": "${token.appId}",
                "Secret": "${token.key}",
                "SortNo": "$startId",
                "Rows": 1000
            }
        """.trimIndent()

            val mapUtil = this.startPostJson(clientToken = token, method = "GetBets", data = param)


            val nextSortNo =  mapUtil.asString("SortNo")
            val bets = mapUtil.asList("Bets")

            val orders = bets.map {  bet ->
                val orderId = bet.asString("BetID")
                val username = bet.asString("Account")
                val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.Fgg, platformUsername = username)
                val betAmount = bet.asBigDecimal("Turnover")
                val winAmount = bet.asBigDecimal("TotalPay")

                val betTime = bet.asLong("BetTime").let { LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.of("Asia/Shanghai"))  }

                val originData = objectMapper.writeValueAsString(bet.data)
                BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, orderId = orderId, betAmount = betAmount, winAmount = winAmount,
                        betTime = betTime, settleTime = betTime, platform = Platform.Fgg, originData = originData)
            }

            nextSortNo to orders
        }
    }

}