package com.onepiece.treasure.games.joker

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.games.GameMemberApi
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.games.joker.value.JokerRegisterResult

class JokerGameMemberApi(
        private val okHttpUtil: OkHttpUtil,
        private val objectMapper: ObjectMapper
) : GameMemberApi {

    override fun register(username: String, password: String) {

        // register
        val registerUrlParam = JokerParamBuilder.instance("CU")
                .set("username", username)
                .build()

        val registerResult = okHttpUtil.doGet(JokerConstant.url, registerUrlParam) { code, json ->
            objectMapper.readValue<JokerRegisterResult>(json!!)

        }
        check(registerResult.status == "OK") { OnePieceExceptionCode.PLATFORM_MEMBER_REGISTER_FAIL }

        // set password
        val setPwdUrlParam = JokerParamBuilder.instance("SP")
                .set("username", username)
                .set("password", password)
                .build()
        okHttpUtil.doGet(JokerConstant.url, setPwdUrlParam) { code, json -> }
    }

    override fun start(gameId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun start(platform: Platform) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}