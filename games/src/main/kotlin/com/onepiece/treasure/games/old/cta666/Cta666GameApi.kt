package com.onepiece.treasure.games.old.cta666

import com.onepiece.treasure.beans.enums.StartPlatform
import com.onepiece.treasure.games.old.GameApi
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.games.value.ClientAuthVo
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service

@Service
class Cta666GameApi(
        private val okHttpUtil: OkHttpUtil
) : GameApi() {

    // 暂时用马币
    val currency = "MYR"
    val lang = "en"

    override fun register(clientAuthVo: ClientAuthVo?, username: String, password: String): String {

        val param = Cat666ParamBuilder.instance("signup")

        val md5Password = DigestUtils.md5Hex(password)
        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}",
                "data":"G",
                "member":{
                    "username":"$username",
                    "password":"$md5Password",
                    "currencyName":"$currency",
                    "winLimit":1000
                }
            } 
        """.trimIndent()

        val result = okHttpUtil.doPostJson(param.url, data, Cta666Result.Register::class.java)
        Cat666Constant.checkCode(result.codeId)

        return username
    }

    override fun start(clientAuthVo: ClientAuthVo?, username: String, password: String): Map<StartPlatform, String> {

        val param = Cat666ParamBuilder.instance("login")
        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}",
                "lang":"$lang",
                "member":{
                    "username":"$username",
                    "password":"$password"
                }
            } 
        """.trimIndent()

        val result = okHttpUtil.doPostJson(param.url, data, Cta666Result.StartGame::class.java)
        Cat666Constant.checkCode(result.codeId)

        return mapOf(
                StartPlatform.Pc to "${result.list[0]}${result.token}",
                StartPlatform.Wap to "${result.list[1]}${result.token}",
                StartPlatform.Android to "${result.list[2]}${result.token}",
                StartPlatform.Ios to "${result.list[2]}${result.token}"
        )

    }


}