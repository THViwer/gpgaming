package com.onepiece.treasure.games.live.golddeluxe

import com.onepiece.treasure.beans.model.token.DefaultClientToken
import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformApi
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.utils.StringUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class GoldDeluxeService(
        private val okHttpUtil: OkHttpUtil
) : PlatformApi() {

    private val log = LoggerFactory.getLogger(GoldDeluxeService::class.java)

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")


    override fun register(registerReq: GameValue.RegisterReq): String {
        val messageId = "M${LocalDateTime.now().format(dateTimeFormat)}${StringUtil.generateNonce(5)}"
        val xml = """
            <?xml version="1.0"?>
            <Request>
              <Header>
                <Method>cCreateMember</Method>
                <MerchantID>${(registerReq.token as DefaultClientToken).appId}}</MerchantID>
                <MessageID>${messageId}</MessageID>
              </Header>
              <Param>
                <UserID>${registerReq.username}</UserID>
                <CurrencyCode>MRY</CurrencyCode>
            <BetGroup>default</BetGroup>
              </Param>
            </Request>
        """.trimIndent()

        val result = okHttpUtil.doPostJson(GameConstant.GOLDDELUXE_API_URL, xml, String::class.java)

        log.info("create member result: $result")

        return registerReq.username
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
