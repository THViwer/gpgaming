package com.onepiece.treasure.games.joker

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.games.GameCashApi
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.games.joker.value.JokerBalanceResult
import org.springframework.stereotype.Service

@Service
class JokerGameCashApi(
        private val okHttpUtil: OkHttpUtil,
        private val objectMapper: ObjectMapper
) : GameCashApi {

    override fun balance(username: String): JokerBalanceResult {
        val urlParam = JokerParamBuilder.instance("GC")
                .set("username", username)
                .build()

        return okHttpUtil.doGet(JokerConstant.url, urlParam) { code, json ->
            //TODO register account
            check(code == 200) { OnePieceExceptionCode.AUTHORITY_FAIL }

            objectMapper.readValue<JokerBalanceResult>(json!!)
        }
    }

    override fun transferIn() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun transferOut() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}