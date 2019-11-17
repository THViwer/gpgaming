package com.onepiece.treasure.games.live.ct

import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.beans.model.token.DefaultClientToken
import org.apache.commons.codec.digest.DigestUtils
import java.util.*

class CtBuild private constructor(
        val url: String,
        val random: String,
        val token: String
) {

    companion object {

        private fun getRequestUrl(method: String, agentName: String): String {
            return "${GameConstant.CT_API_URL}/api/${method}/$agentName/"
        }

        private fun getRandom(): String {
            return UUID.randomUUID().toString().replace("-","")
        }

        private fun getToken(random: String, appId: String, key: String): String {
            return DigestUtils.md5Hex("${appId}${key}$random")

        }

        fun instance(token: DefaultClientToken, method: String): CtBuild {
            val random = getRandom()
            val url = getRequestUrl(method, token.appId)
            val requestToken = getToken(random, token.appId, token.key)

            return CtBuild(url = url, random = random, token = requestToken)
        }
    }

}