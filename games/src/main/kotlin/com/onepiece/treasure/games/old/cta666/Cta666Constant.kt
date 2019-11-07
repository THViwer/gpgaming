package com.onepiece.treasure.games.old.cta666

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import org.apache.commons.codec.digest.DigestUtils
import java.util.*

object Cat666Constant {

    const val URL = "http://api.ctapi888.com"
    const val AGENT_NAME = "CT01060502"
    const val KEY = "6bd7291cd0ce4f808dde9b67f114cbb2"

    fun getRandom(): String {
        return UUID.randomUUID().toString().replace("-","")
    }

    fun getRequestUrl(method: String): String {
        return "$URL/api/${method}/$AGENT_NAME/"
    }

    fun getToken(random: String): String {
        return DigestUtils.md5Hex("$AGENT_NAME$KEY$random")

    }

    fun checkCode(codeId: Int) {
        when (codeId) {
            0 -> {}
            300 -> { OnePieceExceptionCode.PLATFORM_AEGIS}
            else -> { OnePieceExceptionCode.PLATFORM_REQUEST_ERROR }
        }
    }

}

class Cat666ParamBuilder private constructor(
        val url: String,
        val random: String,
        val token: String
) {

    companion object {
        fun instance(method: String): Cat666ParamBuilder {
            val random = Cat666Constant.getRandom()
            val url = Cat666Constant.getRequestUrl(method)
            val token = Cat666Constant.getToken(random)

            return Cat666ParamBuilder(url = url, random = random, token = token)
        }
    }

}

