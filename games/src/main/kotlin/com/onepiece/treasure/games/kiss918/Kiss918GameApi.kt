package com.onepiece.treasure.games.kiss918

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
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

        val url = Kiss918Builder.instance(path = "/ashx/account/account.ashx")
                .set("loginUser", Kiss918Constant.AGENT_CODE)
                .set("userName", Kiss918Constant.AGENT_CODE)
                .set("UserAreaId", "1")
                .set("action", "RandomUserName")
                .build(Kiss918Constant.AGENT_CODE, Kiss918Constant.AGENT_CODE)

        val result = okHttpUtil.doGet(url, Kiss918Value.RegisterUsernameResult::class.java)
        log.info("generator username result: $result")
        check(result.success) { OnePieceExceptionCode.PLATFORM_METHOD_FAIL }

        val generatorUsername = result.account

//        val newPassword = DESUtil.encrypt("fawfwfsfa", Kiss918Constant.SECRET_KEY)
        val addPlayerUrl = Kiss918Builder.instance(path = "/ashx/account/account.ashx")
                .set("action", "AddUser")
                .set("agent", Kiss918Constant.AGENT_CODE)
                .set("PassWd", password)
                .set("userName", generatorUsername)
                .set("Name", generatorUsername)
                .set("tel", "1234124141241")
                .set("Memo", "-")
                .set("UserType", "1")
                .set("UserAreaId", "1")// number
                .set("pwdtype", "1")
                .build(username = generatorUsername)
        val addPlayerResult = okHttpUtil.doGet(addPlayerUrl, String::class.java)
        log.info("add player result : $addPlayerResult")

        return generatorUsername
    }
}