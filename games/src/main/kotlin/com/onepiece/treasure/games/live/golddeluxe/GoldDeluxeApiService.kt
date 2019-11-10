package com.onepiece.treasure.games.live.golddeluxe

import com.onepiece.treasure.beans.model.token.DefaultClientToken
import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.utils.StringUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class GoldDeluxeApiService(
        private val okHttpUtil: OkHttpUtil
) : GoldDeluxeApi {

    private val log = LoggerFactory.getLogger(GoldDeluxeApiService::class.java)

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")

    override fun createMember(token: DefaultClientToken, username: String): String {


        val messageId = "M${LocalDateTime.now().format(dateTimeFormat)}${StringUtil.generateNonce(5)}"
        val xml = """
            <?xml version="1.0"?>
            <Request>
              <Header>
                <Method>cCreateMember</Method>
                <MerchantID>${token.appId}}</MerchantID>
                <MessageID>${messageId}</MessageID>
              </Header>
              <Param>
                <UserID>${username}</UserID>
                <CurrencyCode>MRY</CurrencyCode>
            <BetGroup>default</BetGroup>
              </Param>
            </Request>
        """.trimIndent()

        val result = okHttpUtil.doPostJson(GameConstant.GOLDDELUXE_API_URL, xml, String::class.java)

        log.info("create member result: $result")

        return username

    }
}