package com.onepiece.treasure.games.live.cta666

import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.games.cta666.Cat666Constant
import com.onepiece.treasure.games.token.DefaultClientToken
import org.apache.commons.codec.digest.DigestUtils
import java.util.*

class Cta666Build private constructor(
        val url: String,
        val random: String,
        val token: String
) {

    companion object {

        private fun getRequestUrl(method: String, agentName: String): String {
            return "${GameConstant.CTA666_URL}/api/${method}/$agentName/"
        }

        private fun getRandom(): String {
            return UUID.randomUUID().toString().replace("-","")
        }

        private fun getToken(random: String, key: String): String {
            return DigestUtils.md5Hex("${Cat666Constant.AGENT_NAME}${Cat666Constant.KEY}$random")

        }

        fun instance(token: DefaultClientToken, method: String): Cta666Build {
            val random = getRandom()
            val url = getRequestUrl(method, token.appId)
            val requestToken = getToken(random, token.key)

            return Cta666Build(url = url, random = random, token = requestToken)
        }
    }

}