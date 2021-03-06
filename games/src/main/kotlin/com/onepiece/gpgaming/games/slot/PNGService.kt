package com.onepiece.gpgaming.games.slot

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.token.PNGClientToken
import com.onepiece.gpgaming.games.GameValue
import com.onepiece.gpgaming.games.PlatformService
import com.onepiece.gpgaming.games.http.GameResponse
import com.onepiece.gpgaming.games.http.OKParam
import com.onepiece.gpgaming.games.http.OKResponse
import com.onepiece.gpgaming.games.http.U9HttpRequest
import okhttp3.Credentials
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate

@Service
class PNGService : PlatformService() {

    fun doPostXml(clientToken: PNGClientToken, data: String, action: String): OKResponse {
        val basicAuth = Credentials.basic(clientToken.username, clientToken.password)
        val headers = mapOf(
                "Authorization" to basicAuth,
                "SOAPAction" to action
        )

        val okParam = OKParam.ofPostXml(url = clientToken.apiPath, param = data, headers = headers)
                .copy(mediaType = U9HttpRequest.MEDIA_TEXT_XML, clz = PNGValue.Result::class.java)

        return u9HttpRequest.startRequest(okParam = okParam)
    }

    private fun getLang(language: Language): String {
        return when (language) {
            Language.CN -> "zh_CN"
            Language.EN -> "en_US"
            Language.ID -> "id_ID"
            Language.TH -> "th_TH"
            Language.VI -> "vi_VN"
            Language.MY -> "ms_MY"
            else -> "en_US"
        }
    }

    override fun register(registerReq: GameValue.RegisterReq): GameResponse<String> {
        val clientToken = registerReq.token as PNGClientToken

        val data = """
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:v1="http://playngo.com/v1">  
              <soapenv:Header/>  
              <soapenv:Body> 
                <v1:RegisterUser> 
                  <v1:UserInfo> 
                    <v1:ExternalUserId>${registerReq.username}</v1:ExternalUserId>  
                    <v1:Username>${registerReq.username}</v1:Username>  
                    <v1:Nickname>${registerReq.name}</v1:Nickname>  
                    <v1:Currency>${clientToken.currency}</v1:Currency>  
                    <v1:Country>MY</v1:Country>  
                    <v1:Birthdate>1990-01-01</v1:Birthdate>  
                    <v1:Registration>${LocalDate.now()}</v1:Registration>  
                    <v1:BrandId>${clientToken.brandId}</v1:BrandId>
                    <v1:Language>en_GB</v1:Language>  
                    <v1:Gender>m</v1:Gender> 
                  </v1:UserInfo> 
                </v1:RegisterUser> 
              </soapenv:Body> 
            </soapenv:Envelope>
        """.trimIndent()

        val okResponse = this.doPostXml(clientToken = clientToken, data = data, action = "http://playngo.com/v1/CasinoGameService/RegisterUser")
        return this.bindGameResponse(okResponse = okResponse) {
            registerReq.username
        }
    }


    override fun balance(balanceReq: GameValue.BalanceReq): GameResponse<BigDecimal> {

        val clientToken = balanceReq.token as PNGClientToken
        val data = """
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:v1="http://playngo.com/v1">  
              <soapenv:Header/>  
              <soapenv:Body> 
                <v1:Balance> 
                  <v1:ExternalUserId>${balanceReq.username}</v1:ExternalUserId>  
                </v1:Balance> 
              </soapenv:Body> 
            </soapenv:Envelope>
        """.trimIndent()

        val okResponse = this.doPostXml(clientToken = clientToken, data = data, action = "http://playngo.com/v1/CasinoGameService/Balance")

        return this.bindGameResponse(okResponse = okResponse) {
            it.asMap("Body").asMap("BalanceResponse").asMap("UserBalance").asBigDecimal("Real")
        }
    }

    override fun transfer(transferReq: GameValue.TransferReq): GameResponse<GameValue.TransferResp> {
        val clientToken = transferReq.token as PNGClientToken

        val okResponse = when (transferReq.amount.toDouble() > 0) {
            true -> {
                val data = """
                    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:v1="http://playngo.com/v1">  
                      <soapenv:Header/>  
                      <soapenv:Body> 
                        <v1:Credit> 
                          <v1:ExternalUserId>${transferReq.username}</v1:ExternalUserId>  
                          <v1:Amount>${transferReq.amount.abs()}</v1:Amount>  
                          <v1:Currency>${clientToken.currency}</v1:Currency>  
                          <v1:ExternalTransactionId>${transferReq.orderId}</v1:ExternalTransactionId> 
                        </v1:Credit> 
                      </soapenv:Body> 
                    </soapenv:Envelope>
                """.trimIndent()

                this.doPostXml(clientToken = clientToken, data = data, action = "http://playngo.com/v1/CasinoGameService/Credit")
            }
            false -> {
                val data = """
                    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:v1="http://playngo.com/v1">  
                      <soapenv:Header/>  
                      <soapenv:Body> 
                        <v1:Debit> 
                          <v1:ExternalUserId>${transferReq.username}</v1:ExternalUserId>  
                          <v1:Amount>${transferReq.amount.abs()}</v1:Amount>  
                          <v1:Currency>${clientToken.currency}</v1:Currency>  
                          <v1:ExternalTransactionId>${transferReq.orderId}</v1:ExternalTransactionId> 
                        </v1:Debit> 
                      </soapenv:Body> 
                    </soapenv:Envelope>
                """.trimIndent()
                this.doPostXml(clientToken = clientToken, data = data, action = "http://playngo.com/v1/CasinoGameService/Debit")
            }
        }

        return this.bindGameResponse(okResponse = okResponse) {
            GameValue.TransferResp.successful()
        }
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): GameResponse<GameValue.TransferResp> {
        val clientToken = checkTransferReq.token as PNGClientToken

        return when (checkTransferReq.type) {
            "deposit" -> {
                val data = """
                    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:v1="http://playngo.com/v1">
                       <soapenv:Header />
                       <soapenv:Body>
                          <v1:CreditAccount>
                             <v1:ExternalUserId>${checkTransferReq.username}</v1:ExternalUserId>
                             <v1:Amount>${checkTransferReq.amount}</v1:Amount>
                             <v1:ExternalTransactionId>${checkTransferReq.orderId}</v1:ExternalTransactionId>
                          </v1:CreditAccount>
                       </soapenv:Body>
                    </soapenv:Envelope>
            """.trimIndent()
                val okResponse = this.doPostXml(clientToken = clientToken, data = data, action = "http://playngo.com/v1/CasinoGameService/CreditAccount")
                this.bindGameResponse(okResponse = okResponse) {
                    val successful = it.asMap("Body").asMap("CreditAccountResponse").asMap("UserAccount").data["TransactionId"] != null
                    GameValue.TransferResp.of(successful = successful)
                }

            }
            "withdraw" -> {
                val data = """
                    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:v1="http://playngo.com/v1">
                       <soapenv:Header />
                       <soapenv:Body>
                          <v1:DebitAccount>
                             <v1:ExternalUserId>${checkTransferReq.username}</v1:ExternalUserId>
                             <v1:Amount>${checkTransferReq.amount.abs()}</v1:Amount>
                             <v1:ExternalTransactionId>${checkTransferReq.orderId}</v1:ExternalTransactionId>
                          </v1:DebitAccount>
                       </soapenv:Body>
                    </soapenv:Envelope>
            """.trimIndent()
                val okResponse = this.doPostXml(clientToken = clientToken, data = data, action = "http://playngo.com/v1/CasinoGameService/DebitAccount")
                this.bindGameResponse(okResponse = okResponse) {
                    val successful = it.asMap("Body").asMap("DebitAccountResponse").asMap("UserAccount").data["TransactionId"] != null
                    val balance = it.asMap("Body").asMap("DebitAccountResponse").asMap("UserAccount").asBigDecimal("Real")
                    GameValue.TransferResp.of(successful = successful, balance = balance)
                }
            }
            else -> error(OnePieceExceptionCode.PLATFORM_DATA_FAIL)
        }

    }


    private fun getToken(clientToken: PNGClientToken, username: String): String {
        val data = """
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:v1="http://playngo.com/v1">  
              <soapenv:Header/>  
              <soapenv:Body> 
                <v1:GetTicket> 
                  <v1:ExternalUserId>${username}</v1:ExternalUserId> 
                </v1:GetTicket> 
              </soapenv:Body> 
            </soapenv:Envelope>

        """.trimIndent()

        val okResponse = this.doPostXml(clientToken = clientToken, data = data, action = "http://playngo.com/v1/CasinoGameService/GetTicket")
        return okResponse.asMap("Body").asMap("GetTicketResponse").asString("Ticket")
    }

    override fun startSlotDemo(startSlotReq: GameValue.StartSlotReq): GameResponse<String> {

        val lang = when (startSlotReq.language) {
            Language.MY -> "ms_MY"
            Language.VI -> "vi_VN"
            Language.TH -> "th_TH"
            Language.ID -> "id_ID"
            Language.EN -> "en_US"
            Language.CN -> "zh_CN"
            else -> "en_US"
        }

        val channel = if (startSlotReq.launchMethod == LaunchMethod.Web) "channel=desktop" else "channel=mobile"

        val urlParam = listOf(
                "pid=8835",
                "gameid=${startSlotReq.gameId}",
                "height=100%",
                "width=100%",
                "practice=1",
                channel,
                "lang=$lang"
        ).joinToString("&")

        val path = "https://bsistage.playngonetwork.com/casino/ContainerLauncher?$urlParam"
        return GameResponse.of(data = path)
    }

    override fun startSlot(startSlotReq: GameValue.StartSlotReq): GameResponse<String> {

        val clientToken = startSlotReq.token as PNGClientToken

        val token = this.getToken(clientToken = clientToken, username = startSlotReq.username)

        val lang = when (startSlotReq.language) {
            Language.MY -> "ms_MY"
            Language.VI -> "vi_VN"
            Language.TH -> "th_TH"
            Language.ID -> "id_ID"
            Language.EN -> "en_US"
            Language.CN -> "zh_CN"
            else -> "en_US"
        }

        val channel = if (startSlotReq.launchMethod == LaunchMethod.Web) "channel=desktop" else "channel=mobile"

        val urlParam = listOf(
                "pid=8835",
                "gameid=${startSlotReq.gameId}",
                "height=100%",
                "width=100%",
                "practice=0",
                channel,
//                ticket,
                "ticket=$token",
//                "tusername=$token",
                "lang=$lang"
        ).filter { it.isNotBlank() }.joinToString("&")


//        val domain = when (startSlotReq.launchMethod) {
//            LaunchMethod.Web -> "https://bsistage.playngonetwork.com/casino/ContainerLauncher"
//            LaunchMethod.Wap -> "https://bsistage.playngonetwork.com/casino/PlayMobile"
//            else -> "https://bsistage.playngonetwork.com/casino/ContainerLauncher"
//        }

        val path = "${clientToken.gamePath}?$urlParam"
        return GameResponse.of(data = path)
    }

}