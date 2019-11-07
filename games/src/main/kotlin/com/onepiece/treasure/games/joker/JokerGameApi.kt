package com.onepiece.treasure.games.joker

import com.onepiece.treasure.beans.enums.StartPlatform
import com.onepiece.treasure.games.GameApi
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.games.joker.value.JokerLoginResult
import com.onepiece.treasure.games.joker.value.JokerRegisterResult
import com.onepiece.treasure.games.joker.value.JokerSlotGame
import com.onepiece.treasure.games.joker.value.JokerSlotGameResult
import com.onepiece.treasure.games.value.ClientAuthVo
import com.onepiece.treasure.games.value.SlotGame
import org.springframework.stereotype.Service

@Service
class JokerGameApi(
        private val okHttpUtil: OkHttpUtil
) : GameApi() {

    override fun register(clientAuthVo: ClientAuthVo?, username: String, password: String): String {

        // register
        val (url, formBody) = JokerParamBuilder.instance("CU")
                .set("Username", username)
                .build()

        val registerResult = okHttpUtil.doPostForm(url, formBody, JokerRegisterResult:: class.java)
//        check(registerResult.status == "Created") { OnePieceExceptionCode.PLATFORM_MEMBER_REGISTER_FAIL }

        // set password
        val (url2, formBody2) = JokerParamBuilder.instance("SP")
                .set("Username", username)
                .set("Password", password)
                .build()
        okHttpUtil.doPostForm(url2, formBody2)

        return username
    }

    override fun games(): List<SlotGame> {

        val (url, formBody) = JokerParamBuilder.instance("ListGames").build()

        val data: List<JokerSlotGame> = okHttpUtil.doPostForm(url, formBody, JokerSlotGameResult::class.java).listGames

        return data.map {
            val platforms = it.supportedPlatForms.split(",").map { platformName ->
                when (platformName) {
                    "Desktop" -> SlotGame.GamePlatform.PC
                    "Mobile" -> SlotGame.GamePlatform.Mobile
                    else -> SlotGame.GamePlatform.PC
                }
            }.toList()

            val specials = it.specials?.split(",")?.map { sp ->
                when (sp) {
                    "new" -> SlotGame.Special.New
                    "hot" -> SlotGame.Special.Hot
                    else -> SlotGame.Special.Hot
                }
            }?: emptyList()

            SlotGame(gameId = it.gameCode, gameName = it.gameName, platforms = platforms, specials = specials, icon = it.image1)
        }
    }

    private fun login(username: String?): String {

        val (url, formBody) = JokerParamBuilder.instance("RT")
                .set("Username", username)
                .build()
        val result = okHttpUtil.doPostForm(url, formBody, JokerLoginResult::class.java)
        return result.token
    }

    override fun start(clientAuthVo: ClientAuthVo?, username: String, gameId: String, redirectUrl: String): Map<StartPlatform, String> {
        val token = this.login(username = username)
        val path = "${JokerConstant.gameUrl}?token=$token&game=$gameId&redirectUrl=$redirectUrl"

        return mapOf(
                StartPlatform.Pc to path,
                StartPlatform.Wap to path
        )
    }



}