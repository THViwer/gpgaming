package com.onepiece.treasure.games.cta666

import com.onepiece.treasure.beans.enums.StartPlatform
import com.onepiece.treasure.games.GameApi
import com.onepiece.treasure.games.http.OkHttpUtil
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service

@Service
class Cat666GameApi(
        private val okHttpUtil: OkHttpUtil
) : GameApi() {

    // 暂时用马币
    val currency = "MYR"
    val lang = "en"

    override fun register(username: String, password: String) {

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

        val result = okHttpUtil.doPostJson(param.url, data, Cat666Result.Register::class.java)
        Cat666Constant.checkCode(result.codeId)

    }

    override fun start(username: String, password: String): Map<StartPlatform, String> {

        val param = Cat666ParamBuilder.instance("login")
        val data = """
            {
                "token":"${param.token}",
                "random":"${param.random}",
                "lang":"$lang",
                "member":{
                    "username":"$username",
                    "password":"$password"//可以不传,如果密码不同,将自动修改会员登入密码
                }
            } 
        """.trimIndent()

        val result = okHttpUtil.doPostJson(param.url, data, Cat666Result.StartGame::class.java)
        Cat666Constant.checkCode(result.codeId)

        return mapOf(
                StartPlatform.Pc to result.list[0],
                StartPlatform.Wap to result.list[1],
                StartPlatform.Android to result.list[2],
                StartPlatform.Ios to result.list[2]
        )

    }


}