package com.onepiece.gpgaming.games.sport

import com.onepiece.gpgaming.beans.model.token.BtiClientToken
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.http.GameResponse
import java.math.BigDecimal
import java.util.*

class BtiService : PlatformService() {


    override fun register(registerReq: GameValue.RegisterReq): GameResponse<String> {
        TODO("Not yet implemented")
    }

    override fun balance(balanceReq: GameValue.BalanceReq): GameResponse<BigDecimal> {
        TODO("Not yet implemented")
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameResponse<GameValue.TransferResp> {
        TODO("Not yet implemented")
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameResponse<GameValue.TransferResp> {
        TODO("Not yet implemented")
    }

    override fun start(startReq: GameValue.StartReq): GameResponse<String> {
//        用亚洲版进入游戏-https：// [brand-game-domain] / [lang] / asian-view /
//        用欧洲版进入游戏-https：// [brand-game-domain] / [lang] / sports /
//        用手机版进入游戏-https：// [brand-game-domain] / [lang] / sports /
        return super.start(startReq)
    }
}

fun main() {
    val clientId = 1
    val memberId = 1
    val clientToken = BtiClientToken(path = "https://whlapi3.bti360.io/WHLCustomers.asmx")
    val name = ""
    val password = ""
    val username = UUID.randomUUID().toString().replace("-", "").substring(0, 10)

    println("username = $username")
    val registerReq = GameValue.RegisterReq(clientId = clientId, memberId = memberId, token = clientToken, name = name,
            password = password, username = username)

    /**
     * AgentUserName 是 string(50) 商户于 BTi 之帐户
    AgentPassword 是 string(50) 商户于 BTi 之密码
    MerchantCustomerCode 是 string(50) 于商户系统之独特客户辨识符
    LoginName 是 string(50) 客户用于登入商户系统。(必须为独特的)
    CurrencyCode 是 string(3) 博彩帐户会以什么货币建立。 ISO 4217.
    Eg EUR
    CountryCode 是 string(2) 玩家声称来自的国家。 ISO 3166-1. Eg DE
    City 是 string(50) 用户的城市
    FirstName 是 string(100) 名字
    LastName 是 string(100) 姓氏
    Group1ID 是 string(255) 玩家当前 VIP 等级 (数字)
    CustomerMoreInfo 是 string(4000) 未使用，请传入空白。 CustomerDefaultLanguage 是 string(2) 玩家偏好语言。 ISO 639-1. Eg it
    DomainID 是 String 域 ID。若玩家未在域中请传入空白。
    DateOfBirth
     */

    val body = """
        <?xml version="1.0" encoding="utf-8"?> 
        <soap12:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xmlns:xsd="http://www.w3.org/2001/XMLSchema"
          xmlns:soap12="http://www.w3.org/2003/05/soap-envelope"> 
          <soap12:Body> 
            <GetSpeech xmlns="http://xmlme.com/WebServices"> 
              <Request>string</Request> 
            </GetSpeech> 
          </soap12:Body> 
        </soap12:Envelope>
    """.trimIndent()

}