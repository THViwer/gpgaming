package com.onepiece.treasure.games.kiss918

import com.onepiece.treasure.games.GameApi
import com.onepiece.treasure.games.http.OkHttpUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class Kiss918GameApi(
        private val okHttpUtil: OkHttpUtil
) : GameApi() {

    private val log = LoggerFactory.getLogger(Kiss918GameApi::class.java)

    override fun register(username: String, password: String): String {

        val url = Kiss918Builder.instance("/ashx/account/account.ashx")
                .set("action", "RandomUserName")
                .build()


        val result = okHttpUtil.doGet(url, String::class.java)
        log.info("generator username result: $result")
        val generatorUsername = "sfaf"

        val addPlayerUrl = Kiss918Builder.instance("/ashx/account/account.ashx")
                .set("action", "AddPlayer")
                .set("agent", Kiss918Constant.AGENT_CODE)
                .set("passwd", password)
                .set("userName", generatorUsername)
                .set("name", generatorUsername)
                .set("tel", "1234124141241")
                .set("Memo", generatorUsername)
                .set("UserType", "11")
                .build()
        val addPlayerResult = okHttpUtil.doGet(addPlayerUrl, String::class.java)
        log.info("add player result : $addPlayerResult")

        return generatorUsername

    }
}