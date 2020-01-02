package com.onepiece.gpgaming.games.combination

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.model.token.AsiaGamingClientToken
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.bet.DesECBUtil
import com.onepiece.gpgaming.utils.StringUtil
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.net.URLEncoder

@Service
class AsiaGamingService : PlatformService() {


    fun startGetXml(data: List<String>, clientToken: AsiaGamingClientToken): AsiaGamingValue.Result {


        val signData = data.joinToString(separator = "/\\\\\\\\/")
        val params = DesECBUtil.encrypt(data = signData, key = clientToken.desSecret)
                .let { URLEncoder.encode(it, "utf-8") }
        val key = DigestUtils.md5Hex("$signData${clientToken.md5Secret}")

        val apiPath = "${clientToken.apiDomain}/doBusiness.do?params=$params&key=$key"
        return okHttpUtil.doGet(url = apiPath, clz = AsiaGamingValue.Result::class.java)
    }


    override fun register(registerReq: GameValue.RegisterReq): String {
        val clientToken = registerReq.token as AsiaGamingClientToken

        val data = listOf(
                "cagent=${clientToken.agentCode}",
                "loginname=${registerReq.username}",
                "method=lg",
                "actype=1", // 1 真钱 2 试玩
                "password=${registerReq.password}",
                "oddtype=0", // 设备类型 0:电脑 1:手机 6:手机(直播平台) 7:电脑(直播平台)
                "cur=${clientToken.currency}"
        )

        val result = this.startGetXml(data = data, clientToken = clientToken)
        check(result.info == "0") { result.msg }


        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val clientToken = balanceReq.token as AsiaGamingClientToken


        val data = listOf(
                "cagent=${clientToken.agentCode}",
                "loginname=${balanceReq.username}",
                "method=gb",
                "actype=1",
                "password=${balanceReq.password}",
                "cur=${clientToken.currency}"
        )

        val result = this.startGetXml(data = data, clientToken = clientToken)
        return try {
            result.info.toBigDecimal()
        } catch (e: Exception) {
            error(result.msg)
        }
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameValue.TransferResp {

        val clientToken = transferReq.token as AsiaGamingClientToken

        val amount = when (clientToken.currency) {
            "IDR", "VND" -> transferReq.amount.abs().div(BigDecimal.valueOf(1000))
            else -> transferReq.amount.abs()
        }

        val type = if (transferReq.amount.toDouble() > 0) "IN" else "OUT"
        val preData = listOf(
                "cagent=${clientToken.agentCode}",
                "loginname=${transferReq.username}",
                "method=tc",
                "billno=${transferReq.orderId}",
                "type=$type",
                "credit=${amount}",
                "actype=1",
                "password=${transferReq.password}",
                "cur=${clientToken.currency}"
        )
        val preResult = this.startGetXml(data = preData, clientToken = clientToken)
        check(preResult.info == "0") { preResult.msg }


        val data = listOf(
                "cagent=${clientToken.agentCode}",
                "loginname=${transferReq.username}",
                "method=tcc",
                "billno=${transferReq.orderId}",
                "type=$type",
                "credit=${amount}",
                "actype=1",
                "password=${transferReq.password}"
        )
        val result = this.startGetXml(data = data, clientToken = clientToken)
        check(result.info == "0") { result.msg }

        return GameValue.TransferResp.successful(platformOrderId = transferReq.orderId)
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameValue.TransferResp {

        val clientToken = checkTransferReq.token as AsiaGamingClientToken

        val data = listOf(
                "cagent=${clientToken.agentCode}",
                "billno=${checkTransferReq.orderId}",
                "method=qos",
                "actype=1",
                "cur=${clientToken.currency}"
        )
        val result = this.startGetXml(data = data, clientToken = clientToken)
        check(result.info == "0" || result.info == "1") { result.msg }

        return GameValue.TransferResp.successful(platformOrderId = checkTransferReq.orderId)
    }

    private fun getLang(language: Language): String {
        return when (language) {
            Language.CN -> "1"
            Language.EN -> "3"
            Language.ID -> "11"
            Language.TH -> "6"
            Language.VI -> "8"
            else -> "3"
        }
    }

    private fun getOddType(launch: LaunchMethod): String {
        return when (launch) {
            LaunchMethod.Wap -> "1"
            else -> "0"
        }
    }

    override fun start(startReq: GameValue.StartReq): String {
        val clientToken = startReq.token as AsiaGamingClientToken

        val mh5 = if (startReq.launch == LaunchMethod.Wap) "mh5=y" else ""
        val data = listOf(
                "cagent=${clientToken.agentCode}",
                "loginname=${startReq.username}",
                "password=${startReq.password}",
                "dm=${startReq.redirectUrl}",
                "sid=${clientToken.agentCode}${StringUtil.generateNumNonce(15)}",
                "actype=1",
                "lang=${getLang(startReq.language)}",
//                "gameType="
                "oddtype=${getOddType(startReq.launch)}", // 设备类型 0:电脑 1:手机 6:手机(直播平台) 7:电脑(直播平台)
                "cur=${clientToken.currency}",
                mh5
        ).filter { it.isNotBlank() }

        val signData = data.joinToString(separator = "/\\\\\\\\/")
        val params = DesECBUtil.encrypt(data = signData, key = clientToken.desSecret)
                .let { URLEncoder.encode(it, "utf-8") }
        val key = DigestUtils.md5Hex("$signData${clientToken.md5Secret}")


        return "${clientToken.startGameApiDomain}/forwardGame.do?params=${params}&key=${key}"
    }

}
