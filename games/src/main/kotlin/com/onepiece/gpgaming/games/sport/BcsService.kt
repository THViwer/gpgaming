package com.onepiece.gpgaming.games.sport

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
            val url = it.asString("Data")
            if (url.contains("https")) url else "https:$url"
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

                result.result?.filter { it.data["Status"] == 2 }?.map { bet1 ->
                    val bet = bet1.getMapUtil()
                    val orderId = bet.asString("BetID")
                    val username = bet.asString("Account")
                    val (clientId, memberId) = prefixPlatformUsername(platform = Platform.Bcs, platformUsername = username)
                    val betAmount = bet.asBigDecimal("BetAmount")
//                    val turnover = bet.asBigDecimal("Turnover")
                    val win = bet.asBigDecimal("Win")
                    val payout = betAmount.plus(win)

                    val betTime = bet.asString("BetDate").split("+").firstOrNull().let {
                        LocalDateTime.parse(it)
                    }?: LocalDateTime.MIN
                    val settleTime = bet.asString("UpdateTime").split("+").firstOrNull()?.let {
                        LocalDateTime.parse(it)
                    }?: LocalDateTime.MIN

                    val sortNo = bet.asString("SortNo")
                    if (sortNo > nextId) nextId = sortNo

                    val originData = objectMapper.writeValueAsString(bet.data)
                    BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, platform = Platform.Bcs, orderId = orderId, betAmount = betAmount,
                            payout = payout, betTime = betTime, settleTime = settleTime, originData = originData, validAmount = betAmount)
                } ?: emptyList()
            }

            BetNextIdData(nextId = nextId, gameResponse = gameResponse)

        }
    }


}


//
//fun main() {
//    val json = """
//        {
//            "Data": [
//                {
//                    "BetID": "SP315482857",
//                    "Account": "10001000519ag",
//                    "Payout": 188,
//                    "Currency": "MYR",
//                    "BetAmount": 100,
//                    "DeductAmount": 100,
//                    "AllWin": 88,
//                    "Turnover": 100,
//                    "BetOdds": 0.88,
//                    "Win": 88,
//                    "OddsStyle": "MY",
//                    "BetDate": "2020-11-19T21:17:00.713+08:00",
//                    "Status": 2,
//                    "Result": 1,
//                    "ReportDate": "2020-11-19T00:00:00+08:00",
//                    "BetIP": "42.189.142.246",
//                    "UpdateTime": "2020-11-19T22:59:34.347+08:00",
//                    "AgentID": 0,
//                    "GroupComm": "A",
//                    "BetWay": 1,
//                    "MpID": 0,
//                    "SortNo": 3,
//                    "SubBets": [
//                        {
//                            "BetID": "SP315482857",
//                            "SubID": 248990099,
//                            "SportID": 1,
//                            "LeagueID": 6203,
//                            "MatchID": 2476435,
//                            "HomeID": 279975,
//                            "AwayID": 38330,
//                            "Stage": 3,
//                            "MarketID": 3,
//                            "Odds": 0.88,
//                            "Hdp": 2.5,
//                            "HomeScore": 0,
//                            "AwayScore": 0,
//                            "HomeCard": 1,
//                            "AwayCard": 0,
//                            "BetPos": 2,
//                            "OutLevel": 0,
//                            "Result": 1,
//                            "Status": 2
//                        }
//                    ],
//                    "InsBets": []
//                },
//                {
//                    "BetID": "SP316725064",
//                    "Account": "10001001327ug",
//                    "Payout": 0,
//                    "Currency": "MYR",
//                    "BetAmount": 5000,
//                    "DeductAmount": 2200,
//                    "AllWin": 5000,
//                    "Turnover": 5000,
//                    "BetOdds": -0.44,
//                    "Win": -2200,
//                    "OddsStyle": "MY",
//                    "BetDate": "2020-11-29T21:22:14.923+08:00",
//                    "Status": 2,
//                    "Result": 2,
//                    "ReportDate": "2020-11-29T00:00:00+08:00",
//                    "BetIP": "42.189.142.246",
//                    "UpdateTime": "2020-11-29T21:32:22.063+08:00",
//                    "AgentID": 0,
//                    "GroupComm": "A",
//                    "BetWay": 1,
//                    "MpID": 0,
//                    "SortNo": 10,
//                    "SubBets": [
//                        {
//                            "BetID": "SP316725064",
//                            "SubID": 251025003,
//                            "SportID": 1,
//                            "LeagueID": 5511,
//                            "MatchID": 2478433,
//                            "HomeID": 33033,
//                            "AwayID": 33939,
//                            "Stage": 3,
//                            "MarketID": 3,
//                            "Odds": -0.44,
//                            "Hdp": 4.5,
//                            "HomeScore": 1,
//                            "AwayScore": 3,
//                            "HomeCard": 0,
//                            "AwayCard": 0,
//                            "BetPos": 1,
//                            "OutLevel": 0,
//                            "Result": 2,
//                            "Status": 2
//                        }
//                    ],
//                    "InsBets": []
//                },
//                {
//                    "BetID": "SP316725010",
//                    "Account": "10001001327ug",
//                    "Payout": 0,
//                    "Currency": "MYR",
//                    "BetAmount": 500,
//                    "DeductAmount": 470,
//                    "AllWin": 500,
//                    "Turnover": 500,
//                    "BetOdds": -0.94,
//                    "Win": -470,
//                    "OddsStyle": "MY",
//                    "BetDate": "2020-11-29T21:22:02.033+08:00",
//                    "Status": 2,
//                    "Result": 2,
//                    "ReportDate": "2020-11-29T00:00:00+08:00",
//                    "BetIP": "42.189.142.246",
//                    "UpdateTime": "2020-11-29T21:33:34.047+08:00",
//                    "AgentID": 0,
//                    "GroupComm": "A",
//                    "BetWay": 1,
//                    "MpID": 0,
//                    "SortNo": 11,
//                    "SubBets": [
//                        {
//                            "BetID": "SP316725010",
//                            "SubID": 251024895,
//                            "SportID": 1,
//                            "LeagueID": 18625,
//                            "MatchID": 2484585,
//                            "HomeID": 96558,
//                            "AwayID": 96573,
//                            "Stage": 3,
//                            "MarketID": 1,
//                            "Odds": -0.94,
//                            "Hdp": -0.5,
//                            "HomeScore": 4,
//                            "AwayScore": 4,
//                            "HomeCard": 0,
//                            "AwayCard": 0,
//                            "BetPos": 1,
//                            "OutLevel": 0,
//                            "Result": 2,
//                            "Status": 2
//                        }
//                    ],
//                    "InsBets": []
//                },
//                {
//                    "BetID": "SP316724890",
//                    "Account": "10001001327ug",
//                    "Payout": 0,
//                    "Currency": "MYR",
//                    "BetAmount": 500,
//                    "DeductAmount": 500,
//                    "AllWin": 425,
//                    "Turnover": 500,
//                    "BetOdds": 0.85,
//                    "Win": -500,
//                    "OddsStyle": "MY",
//                    "BetDate": "2020-11-29T21:21:32.893+08:00",
//                    "Status": 2,
//                    "Result": 2,
//                    "ReportDate": "2020-11-29T00:00:00+08:00",
//                    "BetIP": "42.189.142.246",
//                    "UpdateTime": "2020-11-29T22:34:38.697+08:00",
//                    "AgentID": 0,
//                    "GroupComm": "A",
//                    "BetWay": 1,
//                    "MpID": 0,
//                    "SortNo": 12,
//                    "SubBets": [
//                        {
//                            "BetID": "SP316724890",
//                            "SubID": 251024694,
//                            "SportID": 1,
//                            "LeagueID": 5513,
//                            "MatchID": 2474503,
//                            "HomeID": 32124,
//                            "AwayID": 33399,
//                            "Stage": 3,
//                            "MarketID": 3,
//                            "Odds": 0.85,
//                            "Hdp": 3,
//                            "HomeScore": 0,
//                            "AwayScore": 0,
//                            "HomeCard": 0,
//                            "AwayCard": 0,
//                            "BetPos": 2,
//                            "OutLevel": 0,
//                            "Result": 2,
//                            "Status": 2
//                        }
//                    ],
//                    "InsBets": []
//                },
//                {
//                    "BetID": "SP317039426",
//                    "Account": "10001001399fd",
//                    "Payout": 0,
//                    "Currency": "MYR",
//                    "BetAmount": 1097,
//                    "DeductAmount": 394.92,
//                    "AllWin": 1097,
//                    "Turnover": 1097,
//                    "BetOdds": -0.36,
//                    "Win": -394.92,
//                    "OddsStyle": "MY",
//                    "BetDate": "2020-12-02T18:52:21.8+08:00",
//                    "Status": 2,
//                    "Result": 2,
//                    "ReportDate": "2020-12-02T00:00:00+08:00",
//                    "BetIP": "42.189.142.246",
//                    "UpdateTime": "2020-12-02T18:57:49.25+08:00",
//                    "AgentID": 0,
//                    "GroupComm": "A",
//                    "BetWay": 1,
//                    "MpID": 0,
//                    "SortNo": 18,
//                    "SubBets": [
//                        {
//                            "BetID": "SP317039426",
//                            "SubID": 251522503,
//                            "SportID": 1,
//                            "LeagueID": 28550,
//                            "MatchID": 2487857,
//                            "HomeID": 296826,
//                            "AwayID": 235555,
//                            "Stage": 3,
//                            "MarketID": 3,
//                            "Odds": -0.36,
//                            "Hdp": 2.5,
//                            "HomeScore": 0,
//                            "AwayScore": 2,
//                            "HomeCard": 0,
//                            "AwayCard": 0,
//                            "BetPos": 1,
//                            "OutLevel": 0,
//                            "Result": 2,
//                            "Status": 2
//                        }
//                    ],
//                    "InsBets": []
//                },
//                {
//                    "BetID": "SP317039317",
//                    "Account": "10001001399fd",
//                    "Payout": 1158,
//                    "Currency": "MYR",
//                    "BetAmount": 600,
//                    "DeductAmount": 600,
//                    "AllWin": 558,
//                    "Turnover": 600,
//                    "BetOdds": 0.93,
//                    "Win": 558,
//                    "OddsStyle": "MY",
//                    "BetDate": "2020-12-02T18:50:50.05+08:00",
//                    "Status": 2,
//                    "Result": 1,
//                    "ReportDate": "2020-12-02T00:00:00+08:00",
//                    "BetIP": "42.189.142.246",
//                    "UpdateTime": "2020-12-02T19:56:28.833+08:00",
//                    "AgentID": 0,
//                    "GroupComm": "A",
//                    "BetWay": 1,
//                    "MpID": 0,
//                    "SortNo": 19,
//                    "SubBets": [
//                        {
//                            "BetID": "SP317039317",
//                            "SubID": 251522335,
//                            "SportID": 1,
//                            "LeagueID": 17709,
//                            "MatchID": 2485541,
//                            "HomeID": 38021,
//                            "AwayID": 41244,
//                            "Stage": 3,
//                            "MarketID": 1,
//                            "Odds": 0.93,
//                            "Hdp": 0,
//                            "HomeScore": 0,
//                            "AwayScore": 0,
//                            "HomeCard": 0,
//                            "AwayCard": 0,
//                            "BetPos": 2,
//                            "OutLevel": 0,
//                            "Result": 1,
//                            "Status": 2
//                        }
//                    ],
//                    "InsBets": []
//                },
//                {
//                    "BetID": "SP317039341",
//                    "Account": "10001001399fd",
//                    "Payout": 230,
//                    "Currency": "MYR",
//                    "BetAmount": 500,
//                    "DeductAmount": 460,
//                    "AllWin": 500,
//                    "Turnover": 250,
//                    "BetOdds": -0.92,
//                    "Win": -230,
//                    "OddsStyle": "MY",
//                    "BetDate": "2020-12-02T18:51:12.207+08:00",
//                    "Status": 2,
//                    "Result": 4,
//                    "ReportDate": "2020-12-02T00:00:00+08:00",
//                    "BetIP": "42.189.142.246",
//                    "UpdateTime": "2020-12-02T20:28:32.183+08:00",
//                    "AgentID": 0,
//                    "GroupComm": "A",
//                    "BetWay": 1,
//                    "MpID": 0,
//                    "SortNo": 20,
//                    "SubBets": [
//                        {
//                            "BetID": "SP317039341",
//                            "SubID": 251522372,
//                            "SportID": 1,
//                            "LeagueID": 17709,
//                            "MatchID": 2485543,
//                            "HomeID": 309519,
//                            "AwayID": 32102,
//                            "Stage": 3,
//                            "MarketID": 1,
//                            "Odds": -0.92,
//                            "Hdp": -0.25,
//                            "HomeScore": 0,
//                            "AwayScore": 0,
//                            "HomeCard": 0,
//                            "AwayCard": 0,
//                            "BetPos": 2,
//                            "OutLevel": 0,
//                            "Result": 4,
//                            "Status": 2
//                        }
//                    ],
//                    "InsBets": []
//                },
//                {
//                    "BetID": "SP317202188",
//                    "Account": "10001001399fd",
//                    "Payout": 141,
//                    "Currency": "MYR",
//                    "BetAmount": 100,
//                    "DeductAmount": 100,
//                    "AllWin": 82,
//                    "Turnover": 50,
//                    "BetOdds": 0.82,
//                    "Win": 41,
//                    "OddsStyle": "MY",
//                    "BetDate": "2020-12-04T10:59:45.163+08:00",
//                    "Status": 2,
//                    "Result": 3,
//                    "ReportDate": "2020-12-03T00:00:00+08:00",
//                    "BetIP": "42.189.142.246",
//                    "UpdateTime": "2020-12-04T11:25:33.063+08:00",
//                    "AgentID": 0,
//                    "GroupComm": "A",
//                    "BetWay": 1,
//                    "MpID": 0,
//                    "SortNo": 25,
//                    "SubBets": [
//                        {
//                            "BetID": "SP317202188",
//                            "SubID": 251810754,
//                            "SportID": 1,
//                            "LeagueID": 21742,
//                            "MatchID": 2483814,
//                            "HomeID": 49682,
//                            "AwayID": 103038,
//                            "Stage": 3,
//                            "MarketID": 1,
//                            "Odds": 0.82,
//                            "Hdp": 0.25,
//                            "HomeScore": 0,
//                            "AwayScore": 3,
//                            "HomeCard": 0,
//                            "AwayCard": 0,
//                            "BetPos": 2,
//                            "OutLevel": 0,
//                            "Result": 3,
//                            "Status": 2
//                        }
//                    ],
//                    "InsBets": []
//                },
//                {
//                    "BetID": "SP317202201",
//                    "Account": "10001001399fd",
//                    "Payout": 0,
//                    "Currency": "MYR",
//                    "BetAmount": 200,
//                    "DeductAmount": 200,
//                    "AllWin": 188,
//                    "Turnover": 200,
//                    "BetOdds": 0.94,
//                    "Win": -200,
//                    "OddsStyle": "MY",
//                    "BetDate": "2020-12-04T10:59:54.037+08:00",
//                    "Status": 2,
//                    "Result": 2,
//                    "ReportDate": "2020-12-03T00:00:00+08:00",
//                    "BetIP": "42.189.142.246",
//                    "UpdateTime": "2020-12-04T11:33:06.907+08:00",
//                    "AgentID": 0,
//                    "GroupComm": "A",
//                    "BetWay": 1,
//                    "MpID": 0,
//                    "SortNo": 26,
//                    "SubBets": [
//                        {
//                            "BetID": "SP317202201",
//                            "SubID": 251810777,
//                            "SportID": 1,
//                            "LeagueID": 21773,
//                            "MatchID": 2488878,
//                            "HomeID": 104172,
//                            "AwayID": 198908,
//                            "Stage": 3,
//                            "MarketID": 3,
//                            "Odds": 0.94,
//                            "Hdp": 10.5,
//                            "HomeScore": 4,
//                            "AwayScore": 4,
//                            "HomeCard": 0,
//                            "AwayCard": 0,
//                            "BetPos": 1,
//                            "OutLevel": 0,
//                            "Result": 2,
//                            "Status": 2
//                        }
//                    ],
//                    "InsBets": []
//                },
//                {
//                    "BetID": "SP318582186",
//                    "Account": "10001001766ya",
//                    "Payout": 3.238,
//                    "Currency": "MYR",
//                    "BetAmount": 5,
//                    "DeductAmount": 3.8,
//                    "AllWin": 5,
//                    "Turnover": 5,
//                    "BetOdds": -0.76,
//                    "Win": -0.562,
//                    "OddsStyle": "MY",
//                    "BetDate": "2020-12-16T02:57:33.5+08:00",
//                    "Status": 2,
//                    "Result": 2,
//                    "ReportDate": "2020-12-15T00:00:00+08:00",
//                    "BetIP": "42.190.30.218",
//                    "UpdateTime": "2020-12-16T02:57:44.483+08:00",
//                    "AgentID": 0,
//                    "GroupComm": "A",
//                    "BetWay": 1,
//                    "MpID": 0,
//                    "SortNo": 28,
//                    "SubBets": [
//                        {
//                            "BetID": "SP318582186",
//                            "SubID": 254063213,
//                            "SportID": 1,
//                            "LeagueID": 5509,
//                            "MatchID": 2491511,
//                            "HomeID": 34081,
//                            "AwayID": 32296,
//                            "Stage": 3,
//                            "MarketID": 1,
//                            "Odds": -0.76,
//                            "Hdp": 0.25,
//                            "HomeScore": 0,
//                            "AwayScore": 0,
//                            "HomeCard": 0,
//                            "AwayCard": 0,
//                            "BetPos": 1,
//                            "OutLevel": 0,
//                            "Result": 0,
//                            "Status": 1
//                        }
//                    ],
//                    "InsBets": [
//                        {
//                            "InsID": 395878,
//                            "BetValue": 5,
//                            "BackValue": 3.238,
//                            "Win": -0.562,
//                            "BetDate": "2020-12-16T02:57:44.483+08:00"
//                        }
//                    ]
//                },
//                {
//                    "BetID": "SP319542867",
//                    "Account": "10001001399fd",
//                    "Payout": 7325,
//                    "Currency": "MYR",
//                    "BetAmount": 5000,
//                    "DeductAmount": 5000,
//                    "AllWin": 4650,
//                    "Turnover": 2500,
//                    "BetOdds": 0.93,
//                    "Win": 2325,
//                    "OddsStyle": "MY",
//                    "BetDate": "2020-12-26T02:07:15.51+08:00",
//                    "Status": 2,
//                    "Result": 3,
//                    "ReportDate": "2020-12-25T00:00:00+08:00",
//                    "BetIP": "42.189.142.246",
//                    "UpdateTime": "2020-12-26T03:30:29.373+08:00",
//                    "AgentID": 0,
//                    "GroupComm": "A",
//                    "BetWay": 1,
//                    "MpID": 0,
//                    "SortNo": 31,
//                    "SubBets": [
//                        {
//                            "BetID": "SP319542867",
//                            "SubID": 255599188,
//                            "SportID": 1,
//                            "LeagueID": 5628,
//                            "MatchID": 2506927,
//                            "HomeID": 32887,
//                            "AwayID": 215776,
//                            "Stage": 3,
//                            "MarketID": 1,
//                            "Odds": 0.93,
//                            "Hdp": 0.25,
//                            "HomeScore": 0,
//                            "AwayScore": 0,
//                            "HomeCard": 0,
//                            "AwayCard": 0,
//                            "BetPos": 2,
//                            "OutLevel": 0,
//                            "Result": 3,
//                            "Status": 2
//                        }
//                    ],
//                    "InsBets": []
//                },
//                {
//                    "BetID": "SP319693473",
//                    "Account": "10001001399fd",
//                    "Payout": 0,
//                    "Currency": "MYR",
//                    "BetAmount": 2000,
//                    "DeductAmount": 2000,
//                    "AllWin": 1940,
//                    "Turnover": 2000,
//                    "BetOdds": 0.97,
//                    "Win": -2000,
//                    "OddsStyle": "MY",
//                    "BetDate": "2020-12-27T17:16:32.25+08:00",
//                    "Status": 2,
//                    "Result": 2,
//                    "ReportDate": "2020-12-27T00:00:00+08:00",
//                    "BetIP": "42.189.142.246",
//                    "UpdateTime": "2020-12-27T18:07:01.063+08:00",
//                    "AgentID": 0,
//                    "GroupComm": "A",
//                    "BetWay": 1,
//                    "MpID": 0,
//                    "SortNo": 36,
//                    "SubBets": [
//                        {
//                            "BetID": "SP319693473",
//                            "SubID": 255842557,
//                            "SportID": 1,
//                            "LeagueID": 31443,
//                            "MatchID": 2508630,
//                            "HomeID": 268567,
//                            "AwayID": 311473,
//                            "Stage": 3,
//                            "MarketID": 1,
//                            "Odds": 0.97,
//                            "Hdp": -0.5,
//                            "HomeScore": 1,
//                            "AwayScore": 0,
//                            "HomeCard": 0,
//                            "AwayCard": 0,
//                            "BetPos": 1,
//                            "OutLevel": 0,
//                            "Result": 2,
//                            "Status": 2
//                        }
//                    ],
//                    "InsBets": []
//                },
//                {
//                    "BetID": "SP319693492",
//                    "Account": "10001001399fd",
//                    "Payout": 0,
//                    "Currency": "MYR",
//                    "BetAmount": 2000,
//                    "DeductAmount": 1980,
//                    "AllWin": 2000,
//                    "Turnover": 2000,
//                    "BetOdds": -0.99,
//                    "Win": -1980,
//                    "OddsStyle": "MY",
//                    "BetDate": "2020-12-27T17:16:44.733+08:00",
//                    "Status": 2,
//                    "Result": 2,
//                    "ReportDate": "2020-12-27T00:00:00+08:00",
//                    "BetIP": "42.189.142.246",
//                    "UpdateTime": "2020-12-27T19:09:25.527+08:00",
//                    "AgentID": 0,
//                    "GroupComm": "A",
//                    "BetWay": 1,
//                    "MpID": 0,
//                    "SortNo": 37,
//                    "SubBets": [
//                        {
//                            "BetID": "SP319693492",
//                            "SubID": 255842605,
//                            "SportID": 1,
//                            "LeagueID": 20636,
//                            "MatchID": 2508631,
//                            "HomeID": 182755,
//                            "AwayID": 178188,
//                            "Stage": 3,
//                            "MarketID": 1,
//                            "Odds": -0.99,
//                            "Hdp": -0.25,
//                            "HomeScore": 1,
//                            "AwayScore": 0,
//                            "HomeCard": 0,
//                            "AwayCard": 0,
//                            "BetPos": 1,
//                            "OutLevel": 0,
//                            "Result": 2,
//                            "Status": 2
//                        }
//                    ],
//                    "InsBets": []
//                },
//                {
//                    "BetID": "SP320394663",
//                    "Account": "10001002596we",
//                    "Payout": 600,
//                    "Currency": "MYR",
//                    "BetAmount": 600,
//                    "DeductAmount": 600,
//                    "AllWin": 528,
//                    "Turnover": 0,
//                    "BetOdds": 0.88,
//                    "Win": 0,
//                    "OddsStyle": "MY",
//                    "BetDate": "2021-01-03T21:44:47.457+08:00",
//                    "Status": 2,
//                    "Result": 0,
//                    "ReportDate": "2021-01-03T00:00:00+08:00",
//                    "BetIP": "42.189.131.101",
//                    "UpdateTime": "2021-01-04T00:00:19.143+08:00",
//                    "AgentID": 0,
//                    "GroupComm": "A",
//                    "BetWay": 1,
//                    "MpID": 0,
//                    "SortNo": 39,
//                    "SubBets": [
//                        {
//                            "BetID": "SP320394663",
//                            "SubID": 256931386,
//                            "SportID": 1,
//                            "LeagueID": 5511,
//                            "MatchID": 2505294,
//                            "HomeID": 32062,
//                            "AwayID": 33623,
//                            "Stage": 2,
//                            "MarketID": 1,
//                            "Odds": 0.88,
//                            "Hdp": 1,
//                            "HomeScore": 0,
//                            "AwayScore": 0,
//                            "HomeCard": 0,
//                            "AwayCard": 0,
//                            "BetPos": 2,
//                            "OutLevel": 0,
//                            "Result": 0,
//                            "Status": 2
//                        }
//                    ],
//                    "InsBets": []
//                },
//                {
//                    "BetID": "SP320426782",
//                    "Account": "10001002596we",
//                    "Payout": 0,
//                    "Currency": "MYR",
//                    "BetAmount": 600,
//                    "DeductAmount": 600,
//                    "AllWin": 588,
//                    "Turnover": 600,
//                    "BetOdds": 0.98,
//                    "Win": -600,
//                    "OddsStyle": "MY",
//                    "BetDate": "2021-01-04T00:01:37.017+08:00",
//                    "Status": 2,
//                    "Result": 2,
//                    "ReportDate": "2021-01-03T00:00:00+08:00",
//                    "BetIP": "42.189.131.101",
//                    "UpdateTime": "2021-01-04T02:31:21.478+08:00",
//                    "AgentID": 0,
//                    "GroupComm": "A",
//                    "BetWay": 1,
//                    "MpID": 0,
//                    "SortNo": 41,
//                    "SubBets": [
//                        {
//                            "BetID": "SP320426782",
//                            "SubID": 256978851,
//                            "SportID": 1,
//                            "LeagueID": 5509,
//                            "MatchID": 2508624,
//                            "HomeID": 32296,
//                            "AwayID": 33149,
//                            "Stage": 2,
//                            "MarketID": 1,
//                            "Odds": 0.98,
//                            "Hdp": 0.25,
//                            "HomeScore": 0,
//                            "AwayScore": 0,
//                            "HomeCard": 0,
//                            "AwayCard": 0,
//                            "BetPos": 1,
//                            "OutLevel": 0,
//                            "Result": 2,
//                            "Status": 2
//                        }
//                    ],
//                    "InsBets": []
//                },
//                {
//                    "BetID": "SP320480948",
//                    "Account": "10001002596we",
//                    "Payout": 1273.86,
//                    "Currency": "MYR",
//                    "BetAmount": 674,
//                    "DeductAmount": 599.86,
//                    "AllWin": 674,
//                    "Turnover": 674,
//                    "BetOdds": -0.89,
//                    "Win": 674,
//                    "OddsStyle": "MY",
//                    "BetDate": "2021-01-04T11:18:49.66+08:00",
//                    "Status": 2,
//                    "Result": 1,
//                    "ReportDate": "2021-01-04T00:00:00+08:00",
//                    "BetIP": "175.140.99.99",
//                    "UpdateTime": "2021-01-04T15:38:20.906+08:00",
//                    "AgentID": 0,
//                    "GroupComm": "A",
//                    "BetWay": 1,
//                    "MpID": 0,
//                    "SortNo": 43,
//                    "SubBets": [
//                        {
//                            "BetID": "SP320480948",
//                            "SubID": 257051711,
//                            "SportID": 1,
//                            "LeagueID": 20685,
//                            "MatchID": 2504889,
//                            "HomeID": 208075,
//                            "AwayID": 32605,
//                            "Stage": 1,
//                            "MarketID": 1,
//                            "Odds": -0.89,
//                            "Hdp": 0,
//                            "HomeScore": 0,
//                            "AwayScore": 0,
//                            "HomeCard": 0,
//                            "AwayCard": 0,
//                            "BetPos": 2,
//                            "OutLevel": 0,
//                            "Result": 1,
//                            "Status": 2
//                        }
//                    ],
//                    "InsBets": []
//                },
//                {
//                    "BetID": "SP320493117",
//                    "Account": "10001002596we",
//                    "Payout": 2704.59,
//                    "Currency": "MYR",
//                    "BetAmount": 1431,
//                    "DeductAmount": 1273.59,
//                    "AllWin": 1431,
//                    "Turnover": 1431,
//                    "BetOdds": -0.89,
//                    "Win": 1431,
//                    "OddsStyle": "MY",
//                    "BetDate": "2021-01-04T19:30:40.343+08:00",
//                    "Status": 2,
//                    "Result": 1,
//                    "ReportDate": "2021-01-04T00:00:00+08:00",
//                    "BetIP": "175.140.99.99",
//                    "UpdateTime": "2021-01-04T23:59:46.721+08:00",
//                    "AgentID": 0,
//                    "GroupComm": "A",
//                    "BetWay": 1,
//                    "MpID": 0,
//                    "SortNo": 45,
//                    "SubBets": [
//                        {
//                            "BetID": "SP320493117",
//                            "SubID": 257076038,
//                            "SportID": 1,
//                            "LeagueID": 5450,
//                            "MatchID": 2510516,
//                            "HomeID": 35419,
//                            "AwayID": 32549,
//                            "Stage": 2,
//                            "MarketID": 1,
//                            "Odds": -0.89,
//                            "Hdp": -0.5,
//                            "HomeScore": 0,
//                            "AwayScore": 0,
//                            "HomeCard": 0,
//                            "AwayCard": 0,
//                            "BetPos": 2,
//                            "OutLevel": 0,
//                            "Result": 1,
//                            "Status": 2
//                        }
//                    ],
//                    "InsBets": []
//                }
//            ],
//            "ErrorCode": "000000",
//            "ErrorMessage": null
//        }
//    """.trimIndent()
//
//
//    val objectMapper = jacksonObjectMapper()
//    val result = objectMapper.readValue<BcsValue.PullBetResultForJson>(json)
//
//    val d = result.result?.filter { it.data["Status"] == 2 }?.map { bet1 ->
//        val bet = bet1.getMapUtil()
//        val orderId = bet.asString("BetID")
//        val username = bet.asString("Account")
//        val (clientId, memberId) = prefixPlatformUsername(platform = Platform.Bcs, platformUsername = username)
//        val betAmount = bet.asBigDecimal("BetAmount")
////                    val turnover = bet.asBigDecimal("Turnover")
//        val win = bet.asBigDecimal("Win")
//        val payout = betAmount.plus(win)
//        val betTime = bet.asString("BetDate").split("+").first().let {
//            LocalDateTime.parse(it)
//        }
//
//        val settleTime = bet.asString("UpdateTime").split("+").first().let {
//            LocalDateTime.parse(it)
//        }
//
//        val sortNo = bet.asString("SortNo")
//
//        val originData = objectMapper.writeValueAsString(bet.data)
//        BetOrderValue.BetOrderCo(clientId = clientId, memberId = memberId, platform = Platform.Bcs, orderId = orderId, betAmount = betAmount,
//                payout = payout, betTime = betTime, settleTime = settleTime, originData = originData, validAmount = betAmount)
//    } ?: emptyList()
//
//    println(d)
//
//
//}
