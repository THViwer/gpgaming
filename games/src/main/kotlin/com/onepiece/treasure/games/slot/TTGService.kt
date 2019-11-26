package com.onepiece.treasure.games.slot

import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.token.ClientToken
import com.onepiece.treasure.beans.model.token.TTGClientToken
import com.onepiece.treasure.beans.value.internet.web.SlotGame
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformService
import com.onepiece.treasure.games.bet.MapUtil
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Suppress("UNCHECKED_CAST")
@Service
class TTGService: PlatformService() {

    private fun startPostXml(method: String, data: String): MapUtil {

        val url = "${gameConstant.getDomain(Platform.TTG)}${method}"
        val xmlData = okHttpUtil.doPostXml(url = url, data = data, clz = Map::class.java)
//        return this.formatXml(xml)
        return MapUtil.instance(data = xmlData as Map<String, Any>)
    }

//    private fun formatXml(xml: String): MapUtil {
//        val map = xml.split(" ").filter { it.contains("=") }.map {
//            val d = it.split("=")
//            d[0] to d[1]
//        }.toMap()
//        return MapUtil.instance(map)
//    }


    override fun register(registerReq: GameValue.RegisterReq): String {

        val tokenClient = registerReq.token as TTGClientToken
        this.login(username = registerReq.username, tokenClient = tokenClient)

        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        val url = "${gameConstant.getDomain(Platform.TTG)}/cip/player/${balanceReq.username}/balance"
        val xml = okHttpUtil.doGetXml(url = url, clz = Map::class.java)
        val mapUtil = MapUtil.instance(xml as Map<String, Any>)
        return mapUtil.asBigDecimal("real")
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {

        val tokenClient = transferReq.token as TTGClientToken

        val data = """
            <transactiondetail uid="${transferReq.username}" amount="${transferReq.amount}" />
        """.trimIndent()
        val mapUtil = this.startPostXml(method = "/cip/transaction/${tokenClient.agentName}/${transferReq.orderId}", data = data)
        check(mapUtil.asString("retry") == "0") { OnePieceExceptionCode.PLATFORM_DATA_FAIL }

        return transferReq.orderId
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): Boolean {
        val tokenClient = checkTransferReq.token as TTGClientToken
        val url = "${gameConstant.getDomain(Platform.TTG)}/cip/transaction/${tokenClient.agentName}/${checkTransferReq.orderId}"
        val xml = okHttpUtil.doGetXml(url = url, clz = String::class.java)
        val mapUtil = MapUtil.instance(xml as Map<String, Any>)
        return mapUtil.data["uid"] != null
    }

    private fun login(username: String, tokenClient: TTGClientToken): MapUtil {
        val data = """
            <?xml version="1.0" encoding="UTF-8"?>
            <logindetail>
               <player account="MYR" country="MY" firstName="" lastName="" userName="" nickName="" tester="0" partnerId="Gpgaming88" commonWallet="0" />
               <partners>
                  <partner partnerId="zero" partnerType="0" />
                  <partner partnerId="IG" partnerType="1" />
                  <partner partnerId="${tokenClient.agentName}" partnerType="1" />
               </partners>
            </logindetail>
        """.trimIndent()
        return this.startPostXml(method = "/cip/gametoken/${username}", data = data)
    }

    override fun slotGames(token: ClientToken, launch: LaunchMethod): List<SlotGame> {
        return when (launch) {
            LaunchMethod.Wap -> TTGGames.mobileGames
            LaunchMethod.Web -> TTGGames.pcGames
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    override fun startSlot(startSlotReq: GameValue.StartSlotReq): String {
        val tokenClient = startSlotReq.token as TTGClientToken
        val mapUtil = this.login(username = startSlotReq.username, tokenClient = tokenClient)
        val token = mapUtil.asString("token")

        /**
         * Language = Simplified Chinese (zh-cn)
         * Language = Traditional Chinese (zh-tw)
         * Language = Vietnamese (vi)
         * Language = Korean (ko)
         * Language = Japanese (ja)
         * Language = Thai (th)
         * Language = English (en)
         */
        val lang = when (startSlotReq.language) {
            Language.CN -> "zh-cn"
            Language.VI -> "vi"
            Language.TH -> "th"
            Language.EN -> "en"
            else -> "en"
        }

        val (gameName, gameType) = TTGGames.getNameAndType(startSlotReq.gameId)

        val data = listOf(
                "playerHandle=$token",
                "account=MYR",
                "gameId=${startSlotReq.gameId}",
                "gameName=$gameName",
                "gameType=$gameType",
                "gameSuite=Flash",
                "lang=$lang"
        ).joinToString(separator = "&")

        return "http://ams-games.stg.ttms.co/casino/default/game/game.html?$data"
    }

}