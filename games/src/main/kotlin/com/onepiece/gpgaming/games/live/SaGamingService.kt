package com.onepiece.gpgaming.games.live

import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.U9RequestStatus
import com.onepiece.gpgaming.beans.model.token.SaGamingClientToken
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.core.utils.PlatformUsernameUtil
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.http.GameResponse
import com.onepiece.gpgaming.games.http.OKParam
import com.onepiece.gpgaming.games.http.OKResponse
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.net.URLEncoder
import java.security.spec.KeySpec
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import javax.crypto.spec.IvParameterSpec

/**
 *
Secret Key 密鑰: 08EFED20ECEC405F802246F1F0603CE4
MD5Key MD5鍵: GgaIMaiNNtg
EncryptKey 加密鍵: g9G16nTs
SA APP EncryptKey 加密鍵: M06!1OgI

我司根據不同功能設定了兩組API路徑。
Generic通用API路徑: http://sai-api.sa-apisvr.com/api/api.aspx
Get Bet Detail取得會員下注詳情API路徑 : http://sai-api.sa-rpt.com/api/api.aspx
 */

@Service
class SaGamingService : PlatformService() {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    private val dateTimeFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val log = LoggerFactory.getLogger(SaGamingService::class.java)

    fun encrypt(data: String, key: String): String {

        val encryptKey = key.toByteArray()
        val keySpec: KeySpec = DESKeySpec(encryptKey)
        val myDesKey = SecretKeyFactory.getInstance("DES").generateSecret(keySpec)
        val iv = IvParameterSpec(encryptKey)
        val desCipher: Cipher = Cipher.getInstance("DES/CBC/PKCS5Padding")
        desCipher.init(Cipher.ENCRYPT_MODE, myDesKey, iv)
        val textEncrypted: ByteArray = desCipher.doFinal(data.toByteArray())
        return Base64.encodeBase64String(textEncrypted)
    }

    fun doGetXml(clientToken: SaGamingClientToken, data: List<String>, time: String): OKResponse {
        val methodParam = data.joinToString(separator = "&")

        val desSign = this.encrypt(data = methodParam, key = clientToken.encryptKey).let { URLEncoder.encode(it, "utf-8") }

        val md5Param = "${methodParam}${clientToken.md5Key}${time}${clientToken.secretKey}"
        val md5Sign = DigestUtils.md5Hex(md5Param)

        val url = "${clientToken.apiPath}/api/api.aspx"
        val param = "q=$desSign&s=$md5Sign"

        val okParam = OKParam.ofGetXml(url = url, param = param)
        val okResponse = u9HttpRequest.startRequest(okParam = okParam)
        if (!okResponse.ok) return okResponse

        val status = try {
            when (okResponse.asInt("ErrorMsgId")) {
                0 -> U9RequestStatus.OK
                129 -> U9RequestStatus.Maintain
                else -> U9RequestStatus.Fail
            }
        } catch (e: Exception) {
            U9RequestStatus.Fail
        }
        return okResponse.copy(status = status)
    }

    fun doGetBetXml(clientToken: SaGamingClientToken, data: List<String>, time: String): OKResponse {
        val methodParam = data.joinToString(separator = "&")

        val desSign = this.encrypt(data = methodParam, key = clientToken.encryptKey).let { URLEncoder.encode(it, "utf-8") }

        val md5Param = "${methodParam}${clientToken.md5Key}${time}${clientToken.secretKey}"
        val md5Sign = DigestUtils.md5Hex(md5Param)

        val url = "${clientToken.apiPath}/api/api.aspx"
        val param = "q=$desSign&s=$md5Sign"

        val okParam = OKParam.ofGetXml(url = url, param = param)
        val okResponse = u9HttpRequest.startRequest(okParam = okParam)

        if (!okResponse.ok) return okResponse

        val status = try {
            when (okResponse.asInt("ErrorMsgId")) {
                0 -> U9RequestStatus.OK
                129 -> U9RequestStatus.Maintain
                else -> U9RequestStatus.Fail
            }
        } catch (e: Exception) {
            U9RequestStatus.Fail
        }
        return okResponse.copy(status = status)

//        val betResult = okHttpUtil.doGetXml(platform = Platform.SaGaming, url = url, clz = SaGamingValue.BetResult::class.java)
//
//        check(betResult.errorMsgId == 0 || betResult.errorMsgId == 112) {
//            log.error("saGaming network error: errorMsgId = ${betResult.errorMsgId}, errorMsg = ${betResult.errorMsg}")
//            OnePieceExceptionCode.PLATFORM_DATA_FAIL
//        }
//        return betResult.betDetailList?: emptyList()
    }


    override fun register(registerReq: GameValue.RegisterReq): GameResponse<String> {
        val clientToken = registerReq.token as SaGamingClientToken

        val time = LocalDateTime.now().format(dateTimeFormatter)

        val data = listOf(
                "method=RegUserInfo",
                "Key=${clientToken.secretKey}",
                "Time=$time",
                "Username=${registerReq.username}",
                "CurrencyType=${clientToken.currency}"
        )

        val okResponse = this.doGetXml(clientToken = clientToken, data = data, time = time)
        return this.bindGameResponse(okResponse = okResponse) {
            registerReq.username
        }
    }

    override fun balance(balanceReq: GameValue.BalanceReq): GameResponse<BigDecimal> {
        val clientToken = balanceReq.token as SaGamingClientToken
        val time = LocalDateTime.now().format(dateTimeFormatter)
        val data = listOf(
                "method=GetUserStatusDV",
                "key=${clientToken.secretKey}",
                "Time=$time",
                "Username=${balanceReq.username}"
        )

        val okResponse = this.doGetXml(clientToken = clientToken, data = data, time = time)
        return this.bindGameResponse(okResponse = okResponse) {
            it.asBigDecimal("Balance")
        }
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameResponse<GameValue.TransferResp> {
        val clientToken = transferReq.token as SaGamingClientToken
        val time = LocalDateTime.now().format(dateTimeFormatter)

        val data = when (transferReq.amount.toDouble() > 0) {
            true -> {
                listOf(
                        "method=CreditBalanceDV",
                        "Key=${clientToken.secretKey}",
                        "Time=$time",
                        "Username=${transferReq.username}",
                        "OrderId=${transferReq.orderId}",
                        "CreditAmount=${transferReq.amount.abs()}"
                )
            }
            false -> {
                listOf(
                        "method=DebitBalanceDV",
                        "Key=${clientToken.secretKey}",
                        "Time=$time",
                        "Username=${transferReq.username}",
                        "OrderId=${transferReq.orderId}",
                        "DebitAmount=${transferReq.amount.abs()}"
                )
            }
        }

        val okResponse = this.doGetXml(clientToken = clientToken, data = data, time = time)
        return this.bindGameResponse(okResponse = okResponse) {
            val balance = it.asBigDecimal("Balance")
            GameValue.TransferResp.successful(balance = balance)
        }
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameResponse<GameValue.TransferResp> {
        val clientToken = checkTransferReq.token as SaGamingClientToken
        val time = LocalDateTime.now().format(dateTimeFormatter)

        val data = listOf(
                "method=CheckOrderId",
                "Key=${clientToken.secretKey}",
                "Time=$time",
                "OrderId=${checkTransferReq.orderId}"
        )

        val okResponse = this.doGetXml(clientToken = clientToken, data = data, time = time)
        return this.bindGameResponse(okResponse = okResponse) {
            val successful = it.asBoolean("isExist")
            GameValue.TransferResp.of(successful)
        }
    }

    override fun start(startReq: GameValue.StartReq): GameResponse<String> {
        val clientToken = startReq.token as SaGamingClientToken

        val time = LocalDateTime.now().format(dateTimeFormatter)
        val data = listOf(
                "method=LoginRequest",
                "key=${clientToken.secretKey}",
                "Time=$time",
                "Username=${startReq.username}",
                "CurrencyType=${clientToken.currency}",
                "h5web=true"
        )

        val okResponse = this.doGetXml(clientToken = clientToken, data = data, time = time)

        return this.bindGameResponse(okResponse = okResponse) {

            val token = it.asString("Token")

            val lang = when (startReq.language) {
                Language.CN -> "zh_CN"
                Language.VI -> "vn"
                Language.ID -> "id"
                Language.TH -> "th"
                Language.EN -> "en_US"
                Language.MY -> "ms"
                else -> "zh_CN"
            }

            val mobile = startReq.launch != LaunchMethod.Web
            val urlParam = listOf(
                    "username=${startReq.username}",
                    "token=$token",
                    "lobby=GPGaming",
                    "lang=${lang}",
                    "mobile=${mobile}",
                    "h5web=true"
            ).joinToString(separator = "&")
            "${clientToken.gamePath}/app.aspx?$urlParam"
        }

    }

    override fun startSlot(startSlotReq: GameValue.StartSlotReq): GameResponse<String> {
        val clientToken = startSlotReq.token as SaGamingClientToken

        val time = LocalDateTime.now().format(dateTimeFormatter)
        val data = listOf(
                "method=LoginRequest",
                "key=${clientToken.secretKey}",
                "GameCode=",
                "Time=$time",
                "Username=${startSlotReq.username}",
                "CurrencyType=${clientToken.currency}",
                "h5web=true"
        )

        val okResponse = this.doGetXml(clientToken = clientToken, data = data, time = time)
        return this.bindGameResponse(okResponse = okResponse) {
            it.asString("Token")
        }
    }

    override fun pullBetOrders(pullBetOrderReq: GameValue.PullBetOrderReq): GameResponse<List<BetOrderValue.BetOrderCo>> {
        val clientToken = pullBetOrderReq.token as SaGamingClientToken
        val time = LocalDateTime.now().format(dateTimeFormatter)
        val data = listOf(
                "method=GetAllBetDetailsForTimeIntervalDV",
                "key=${clientToken.secretKey}",
                "Time=$time",
                "FromTime=${pullBetOrderReq.startTime.format(dateTimeFormatter2)}",
                "ToTime=${pullBetOrderReq.endTime.format(dateTimeFormatter2)}"
        )
        val okResponse = this.doGetBetXml(clientToken = clientToken, data = data, time = time)

        return this.bindGameResponse(okResponse = okResponse) {
            val content = okResponse.response

            val result = xmlMapper.readValue<SaGamingValue.BetResult>(content)

            result.betDetailList?.map { betMap ->
                val bet = betMap.mapUtil
                val orderId = bet.asString("BetID")
                val username = bet.asString("Username")
                val (clientId, memberId) = PlatformUsernameUtil.prefixPlatformUsername(platform = Platform.SaGaming, platformUsername = username)
                val betTime = bet.asLocalDateTime("BetTime")
                val settleTime = bet.asLocalDateTime("PayoutTime")
                val betAmount = bet.asBigDecimal("BetAmount")
                val rolling = bet.asBigDecimal("Rolling")
                val resultAmount = bet.asBigDecimal("ResultAmount")
                val payout = betAmount.plus(resultAmount)

                val originData = objectMapper.writeValueAsString(bet.data)

                BetOrderValue.BetOrderCo(orderId = orderId, clientId = clientId, memberId = memberId, platform = Platform.SaGaming, betTime = betTime,
                        settleTime = settleTime, betAmount = betAmount, payout = payout, originData = originData, validAmount = rolling)

            } ?: emptyList()
        }


    }
}