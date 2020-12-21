package com.onepiece.gpgaming.games.sport

import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.U9RequestStatus
import com.onepiece.gpgaming.beans.model.token.BcsClientToken
import com.onepiece.gpgaming.beans.model.token.ClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.utils.PlatformUsernameUtil.prefixPlatformUsername
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.bet.DEFAULT_DATETIMEFORMATTER
import com.onepiece.gpgaming.games.http.GameResponse
import com.onepiece.gpgaming.games.http.OKParam
import com.onepiece.gpgaming.games.http.OKResponse
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal

/**
 * 货币：
 * MYR Malysia
 * USD United States
 * RMB China
 * HKD HongKong
 * THB Thailand
 * JPY Japan
 * KHR Korea(1:1000)
 * VND Vietnam(1:1000)
 * IDR Indonisia(1:1000)
 * SGD Singapore
 *
 * 语言：
 * CH 简体中文
 * EN 英文
 * JP 日文
 * KO 韩文
 * TH 泰文
 * TW 繁体中文
 * VN 越南文
 */

//fun main() {
//    val data = """
//        <response>
//          <errcode>000000</errcode>
//          <errtext />
//          <result>
//            <Bets>
//              <Bet>
//                <Account>01000001yb</Account>
//                <BetAmount>0</BetAmount>
//                <DeductAmount>0</DeductAmount>
//                <Count>0</Count>
//              </Bet>
//            </Bets>
//          </result>
//        </response>
//    """.trimIndent()
//
//    val p = XmlMapper().registerKotlinModule()
//            .readValue<BcsValue.OutstandingResult>(data)
//    println(p)
//}


@Service
class BcsService : PlatformService() {

    private val log = LoggerFactory.getLogger(BcsService::class.java)

//    fun doGetXml(clientToken: BcsClientToken, method: String, data: Map<String, Any>): OKResponse {
//
//        val url = "${clientToken.apiPath}${method}"
//        val param = data.map { "${it.key}=${it.value}" }.joinToString(separator = "&")
//
//        val okParam = OKParam.ofGetXml(url = url, param = param)
//        val okResponse = u9HttpRequest.startRequest(okParam = okParam)
//        if (!okResponse.ok) return okResponse
//
//
//        val status = try {
//            when (okResponse.asString("errcode")) {
//                "000000" -> U9RequestStatus.OK
//
//                else -> U9RequestStatus.Fail
//            }
//        } catch (e: Exception) {
//            U9RequestStatus.Fail
//        }
//        return okResponse.copy(status = status)
//    }

    fun doPostJson(clientToken: BcsClientToken, method: String, data: String): OKResponse {
        val url = "${clientToken.apiPath}${method}"
        val okParam = OKParam.ofPost(url = url, param = data)
        val okResponse = u9HttpRequest.startRequest(okParam = okParam)
        if (!okResponse.ok) return okResponse

        val status = try {
            when (okResponse.asString("ErrorCode")) {
                "000000" -> U9RequestStatus.OK
                else -> U9RequestStatus.Fail
            }
        } catch (e: Exception) {
            U9RequestStatus.Fail
        }
        return okResponse.copy(status = status)
    }

    override fun register(registerReq: GameValue.RegisterReq): GameResponse<String> {

        val token = registerReq.token as BcsClientToken

        val data = """
            {
               "CompanyKey":"${token.companyKey}",
               "APIPassword":"${token.key}",
               "MemberAccount":"${registerReq.username}",
               "NickName":"${registerReq.name}",
               "Currency":"${token.currency}"
            }
        """.trimIndent()

        val okResponse = this.doPostJson(clientToken = token, method = "/SportApi/Register", data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            registerReq.username
        }


//        val token = registerReq.token as BcsClientToken
//        val param = mapOf(
//                "APIPassword" to token.key,
//                "MemberAccount" to registerReq.username,
//                "NickName" to registerReq.name,
//                "Currency" to token.currency
//        )
//
//        val okResponse = this.doGetXml(clientToken = token, method = "/ThirdApi.asmx/Register", data = param)
//        return this.bindGameResponse(okResponse = okResponse) {
//            registerReq.username
//        }
    }

    override fun balance(balanceReq: GameValue.BalanceReq): GameResponse<BigDecimal> {
        val token = balanceReq.token as BcsClientToken
        val data = """
            {
               "CompanyKey":"${token.companyKey}",
               "APIPassword":"${token.key}",
               "MemberAccount":"${balanceReq.username}"
            }
        """.trimIndent()

        val okResponse = this.doPostJson(clientToken = token, method = "/SportApi/GetBalance", data = data)
        val outstanding = queryOutstanding(balanceReq = balanceReq)
        return this.bindGameResponse(okResponse = okResponse) {
            it.asMap("Data").asBigDecimal("Balance")
        }.copy(outstanding = outstanding)


//        val token = balanceReq.token as BcsClientToken
//        val param = mapOf(
//                "APIPassword" to token.key,
//                "MemberAccount" to balanceReq.username
//        )
//
//        val okResponse = this.doGetXml(clientToken = token, method = "/ThirdApi.asmx/GetBalance", data = param)
//
//        val outstanding = queryOutstanding(balanceReq = balanceReq)
//
//        return this.bindGameResponse(okResponse = okResponse) {
//            it.asMap("result").asBigDecimal("Balance")
//        }.copy(outstanding = outstanding)
    }

    private fun queryOutstanding(balanceReq: GameValue.BalanceReq): BigDecimal {

        val token = balanceReq.token as BcsClientToken
        val param = mapOf(
                "APIPassword" to token.key,
                "MemberAccount" to balanceReq.username
        )

        val data = """
            {
               "CompanyKey":"${token.companyKey}",
               "APIPassword":"${token.key}",
               "MemberAccounts":[
                  "${balanceReq.username}"
               ]
            }
        """.trimIndent()

        val okResponse = this.doPostJson(clientToken = token, method = "/SportApi/GetBetTotalByUnSettlement", data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            val result = objectMapper.readValue<BcsValue.OutstandingResultForJson>(okResponse.response)
            result.bets.first().getMapUtil().asBigDecimal("DeductAmount")
        }.data ?: BigDecimal.ZERO


//        val token = balanceReq.token as BcsClientToken
//        val param = mapOf(
//                "APIPassword" to token.key,
//                "MemberAccount" to balanceReq.username
//        )
//
//        val okResponse = this.doGetXml(clientToken = token, method = "/ThirdApi.asmx/GetBetTotalByUnSettlement", data = param)
//        return this.bindGameResponse(okResponse = okResponse) {
//            val result = xmlMapper.readValue<BcsValue.OutstandingResult>(okResponse.response)
//            result.result.bets.first().getMapUtil().asBigDecimal("DeductAmount")
//        }.data ?: BigDecimal.ZERO
    }


    override fun transfer(transferReq: GameValue.TransferReq): GameResponse<GameValue.TransferResp> {
        val token = transferReq.token as BcsClientToken
        val transferType = if (transferReq.amount.toDouble() > 0) 0 else 1

        // :MD5(APIPassword+MemberAccount+Amount)
        val signKey = "${token.key}${transferReq.username}${transferReq.amount.abs().setScale(4, 2)}".toLowerCase()
        val sign = DigestUtils.md5Hex(signKey)
        log.info("签名key=$signKey")
        val signLast6 = sign.substring(sign.length - 6, sign.length)
        log.info("最终签名：$signLast6")

        val data = """
            {
               "CompanyKey":"${token.companyKey}",
               "APIPassword":"${token.key}",
               "MemberAccount":"${transferReq.username}",
               "Amount":${transferReq.amount.abs()},
               "TransferType":${transferType},
               "Key":"$signLast6",
               "SerialNumber":"${transferReq.orderId}"
            }
        """.trimIndent()

        val okResponse = this.doPostJson(clientToken = token, method = "/SportApi/Transfer", data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            val platformOrderId = it.asMap("Data").asString("SerialNumber")
            val balance = it.asMap("Data").asBigDecimal("Balance")
            GameValue.TransferResp.successful(balance = balance, platformOrderId = platformOrderId)
        }


//        val token = transferReq.token as BcsClientToken
//        val transferType = if (transferReq.amount.toDouble() > 0) 0 else 1
//
//        // :MD5(APIPassword+MemberAccount+Amount)
//        val signKey = "${token.key}${transferReq.username}${transferReq.amount.abs().setScale(4, 2)}".toLowerCase()
//        val sign = DigestUtils.md5Hex(signKey)
//        log.info("签名key=$signKey")
//        val signLast6 = sign.substring(sign.length - 6, sign.length)
//        log.info("最终签名：$signLast6")
//
//        val param = mapOf(
//                "APIPassword" to token.key,
//                "MemberAccount" to transferReq.username,
//                "SerialNumber" to transferReq.orderId,
//                "Amount" to transferReq.amount.abs(),
//                "TransferType" to transferType,
//                "Key" to signLast6
//        )
//
//        val okResponse = this.doGetXml(clientToken = token, method = "/ThirdApi.asmx/Transfer", data = param)
//        return this.bindGameResponse(okResponse = okResponse) {
//            val platformOrderId = it.asMap("result").asString("SerialNumber")
//            val balance = it.asMap("result").asBigDecimal("Balance")
//            GameValue.TransferResp.successful(balance = balance, platformOrderId = platformOrderId)
//        }
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameResponse<GameValue.TransferResp> {

        val token = checkTransferReq.token as BcsClientToken
        val data = """
            {
               "CompanyKey":"${token.companyKey}",
               "APIPassword":"${token.key}",
               "SerialNumber":"${checkTransferReq.orderId}"
            }
        """.trimIndent()

        val okResponse = this.doPostJson(clientToken = token, method = "/SportApi/CheckTransfer", data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            GameValue.TransferResp.successful()
        }

//        val token = checkTransferReq.token as BcsClientToken
//
//        val param = mapOf(
//                "APIPassword" to token.key,
//                "SerialNumber" to checkTransferReq.orderId
//        )
//
//        val okResponse = this.doGetXml(clientToken = token, method = "/ThirdApi.asmx/CheckTransfer", data = param)
//        return this.bindGameResponse(okResponse = okResponse) {
//            GameValue.TransferResp.successful()
//        }
    }

    override fun start(startReq: GameValue.StartReq): GameResponse<String> {

        val webType = when (startReq.launch) {
            LaunchMethod.Web -> "PC"
            LaunchMethod.Wap -> "Smart"
            else -> "Smart"
        }

        val lang = when (startReq.language) {
            Language.EN -> "EN"
            Language.CN -> "CH"
            Language.VI -> "VN"
            Language.TH -> "TH"
            else -> "EN"
        }

        val token = startReq.token as BcsClientToken
//        val param = mapOf(
//                "APIPassword" to token.key,
//                "MemberAccount" to startReq.username,
//                "WebType" to webType,
//                "Language" to lang,
//                "LoginIP" to "127.0.0.1",
//                "GameID" to "1", // 1:体育 2:Keno
//                "PageStyle" to "SP3" // SP1:TBS SP2:SBO SP3:LBC SP4:HG
//        )
        val data = """
            {
               "CompanyKey":"${token.companyKey}",
               "APIPassword":"${token.key}",
               "MemberAccount":"${startReq.username}",
               "WebType":"$webType",
               "LoginIP":"127.0.0.1",
               "Language":"$lang",
               "PageStyle":"SP3",
               "OddsStyle":"MY"
            }
        """.trimIndent()
        val okResponse = this.doPostJson(clientToken = token, method = "/SportApi/Login", data = data)
        return this.bindGameResponse(okResponse = okResponse) {
            it.asString("Data")
        }


//        val webType = when (startReq.launch) {
//            LaunchMethod.Web -> "PC"
//            LaunchMethod.Wap -> "Smart"
//            else -> "Smart"
//        }
//
//        val lang = when (startReq.language) {
//            Language.EN -> "EN"
//            Language.CN -> "CH"
//            Language.VI -> "VN"
//            Language.TH -> "TH"
//            else -> "EN"
//        }
//
//        val token = startReq.token as BcsClientToken
//        val param = mapOf(
//                "APIPassword" to token.key,
//                "MemberAccount" to startReq.username,
//                "WebType" to webType,
//                "Language" to lang,
//                "LoginIP" to "127.0.0.1",
//                "GameID" to "1", // 1:体育 2:Keno
//                "PageStyle" to "SP3" // SP1:TBS SP2:SBO SP3:LBC SP4:HG
//        )
//        val okResponse = this.doGetXml(clientToken = token, method = "/ThirdApi.asmx/Login", data = param)
//        return this.bindGameResponse(okResponse = okResponse) {
//            it.asString("result")
//        }
    }

    override fun startDemo(token: ClientToken, language: Language, launch: LaunchMethod): GameResponse<String> {
        val webType = when (launch) {
            LaunchMethod.Web -> "PC"
            LaunchMethod.Wap -> "Smart"
            else -> "Smart"
        }

        val lang = when (language) {
            Language.EN -> "EN"
            Language.CN -> "CH"
            Language.VI -> "VN"
            Language.TH -> "TH"
            else -> "EN"
        }

        val path = "https://sport.ballcrown.com/?WebType=$webType&Language=$lang"
        return GameResponse.of(data = path)

    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): GameResponse<List<BetOrderValue.BetOrderCo>> {

        val token = pullBetOrderReq.token as BcsClientToken

        return pullByNextId(clientId = pullBetOrderReq.clientId, platform = Platform.Bcs) { nowId ->
            val data = """
                {
                   "CompanyKey":"${token.companyKey}",
                   "APIPassword":"${token.key}",
                   "SortNo":$nowId,
                   "Rows": 1000
                }
            """.trimIndent()

            val okResponse = this.doPostJson(clientToken = token, method = "/SportApi/GetBetSheetBySort", data = data)
            var nextId = "0"

            val gameResponse = this.bindGameResponse(okResponse = okResponse) {
                val result = objectMapper.readValue<BcsValue.PullBetResultForJson>(okResponse.response)

                result.result?.filter { it.data["Status"] == "2" }?.map { bet1 ->
                    val bet = bet1.getMapUtil()
                    val orderId = bet.asString("BetID")
                    val username = bet.asString("Account")
                    val (clientId, memberId) = prefixPlatformUsername(platform = Platform.Bcs, platformUsername = username)
                    val betAmount = bet.asBigDecimal("BetAmount")
//                    val turnover = bet.asBigDecimal("Turnover")
                    val win = bet.asBigDecimal("Win")
                    val payout = betAmount.plus(win)
                    val betTime = bet.asLocalDateTime("BetDate", DEFAULT_DATETIMEFORMATTER)
                    val settleTime = bet.asLocalDateTime("UpdateTime", DEFAULT_DATETIMEFORMATTER)

                    val sortNo = bet.asString("SortNo")
                    if (sortNo > nextId) nextId = sortNo

                    val originData = objectMapper.writeValueAsString(bet.data)
                    BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, platform = Platform.Bcs, orderId = orderId, betAmount = betAmount,
                            payout = payout, betTime = betTime, settleTime = settleTime, originData = originData, validAmount = betAmount)
                } ?: emptyList()
            }

            BetNextIdData(nextId = nextId, gameResponse = gameResponse)

        }


//        val token = pullBetOrderReq.token as BcsClientToken
//
//        return pullByNextId(clientId = pullBetOrderReq.clientId, platform = Platform.Bcs) { nowId ->
//
//            val param = mapOf(
//                    "APIPassword" to token.key,
//                    "SortNo" to nowId,
//                    "Rows" to 1000
//            )
//
//            val okResponse = this.doGetXml(clientToken = token, method = "/ThirdApi.asmx/GetBetSheetBySort", data = param)
//            var nextId = "0"
//
//            val gameResponse = this.bindGameResponse(okResponse = okResponse) {
//                val result = xmlMapper.readValue<BcsValue.PullBetResult>(okResponse.response)
//
//                result.result?.filter { it.data["Status"] == "2" }?.map { bet1 ->
//                    val bet = bet1.getMapUtil()
//                    val orderId = bet.asString("BetID")
//                    val username = bet.asString("Account")
//                    val (clientId, memberId) = prefixPlatformUsername(platform = Platform.Bcs, platformUsername = username)
//                    val betAmount = bet.asBigDecimal("BetAmount")
////                    val turnover = bet.asBigDecimal("Turnover")
//                    val win = bet.asBigDecimal("Win")
//                    val payout = betAmount.plus(win)
//                    val betTime = bet.asLocalDateTime("BetDate", DEFAULT_DATETIMEFORMATTER)
//                    val settleTime = bet.asLocalDateTime("UpdateTime", DEFAULT_DATETIMEFORMATTER)
//
//                    val sortNo = bet.asString("SortNo")
//                    if (sortNo > nextId) nextId = sortNo
//
//                    val originData = objectMapper.writeValueAsString(bet.data)
//                    BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, platform = Platform.Bcs, orderId = orderId, betAmount = betAmount,
//                            payout = payout, betTime = betTime, settleTime = settleTime, originData = originData, validAmount = betAmount)
//                } ?: emptyList()
//            }
//
//            BetNextIdData(nextId = nextId, gameResponse = gameResponse)
//
//        }

    }


}