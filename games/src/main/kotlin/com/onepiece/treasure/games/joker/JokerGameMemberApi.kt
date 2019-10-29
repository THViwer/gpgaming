package com.onepiece.treasure.games.joker

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.games.GameMemberApi
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.games.joker.value.JokerRegisterResult
import com.onepiece.treasure.games.joker.value.JokerSlotGame
import com.onepiece.treasure.games.value.SlotGame
import okhttp3.Response

class JokerGameMemberApi(
        private val okHttpUtil: OkHttpUtil,
        private val objectMapper: ObjectMapper
) : GameMemberApi {

    override fun register(username: String, password: String) {

        // register
        val registerUrlParam = JokerParamBuilder.instance("CU")
                .set("username", username)
                .build()

        val registerResult = okHttpUtil.doPost(JokerConstant.url, registerUrlParam, JokerRegisterResult:: class.java)
        check(registerResult.status == "OK") { OnePieceExceptionCode.PLATFORM_MEMBER_REGISTER_FAIL }

        // set password
        val setPwdUrlParam = JokerParamBuilder.instance("SP")
                .set("username", username)
                .set("password", password)
                .build()
        okHttpUtil.doPost(JokerConstant.url, setPwdUrlParam)
    }


    private val type = object: TypeReference<List<JokerSlotGame>>(){}

    override fun games(): List<SlotGame> {

        val urlParam = JokerParamBuilder.instance("ListGame").build()

        val data: List<JokerSlotGame> = okHttpUtil.doPost(JokerConstant.url, urlParam) { response: Response ->
            val bytes = response.body!!.bytes()
            objectMapper.readValue(bytes, type)
        }

        return data.map {
            val platforms = it.supportedPlatForms.split(",").map { platformName ->
                when (platformName) {
                    "Desktop" -> SlotGame.GamePlatform.PC
                    "Mobile" -> SlotGame.GamePlatform.Mobile
                    else -> SlotGame.GamePlatform.PC
                }
            }.toList()

            val specials = it.special.split(",").map { sp ->
                when (sp) {
                    "new" -> SlotGame.Special.New
                    "hot" -> SlotGame.Special.Hot
                    else -> SlotGame.Special.Hot
                }
            }

            SlotGame(gameId = it.gameCode, gameName = it.gameName, platforms = platforms, specials = specials, icon = it.image1)
        }

    }

    override fun start(token: String, gameId: String, redirectUrl: String): String {
        val urlParam = "token=$token&game=$gameId&redirectUrl=$redirectUrl"
        return okHttpUtil.doPost(JokerConstant.url, urlParam, String::class.java)
    }

    override fun start(platform: Platform) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}