package com.onepiece.treasure.games.kiss918

import org.apache.commons.codec.digest.DigestUtils

object Kiss918Constant {

    const val AGENT_CODE = "MAN1006"
    const val AUTH_CODE = "MthBAKyqdZRrKEKRuJvy"
    const val SECRET_KEY = "j7ZNS4e79Sr7Fa2SX325"
    const val API_URL = "http://api.918kiss.com:9991"
    const val API_URL2 = "http://api.918kiss.com:9919"

}

class Kiss918Builder(
        private val path: String,
        private val time: Long
) {

    private val param = arrayListOf(
            "time=$time",
            "authcode=${Kiss918Constant.AUTH_CODE}"
    )

    companion object {
        fun instance(path: String): Kiss918Builder {
            return Kiss918Builder(path = path, time = System.currentTimeMillis())
        }
    }

    fun set(key: String, value: String):Kiss918Builder {
        param.add("$key=$value")
        return this
    }

    private fun sign(): String {
        val signStr = "${Kiss918Constant.AUTH_CODE}${Kiss918Constant.AGENT_CODE}${time}${Kiss918Constant.SECRET_KEY}".toLowerCase()
        return DigestUtils.md5Hex(signStr)
    }

    fun build(): String {
        val sign = sign()
        param.add("sign=$sign")
        return "${Kiss918Constant.API_URL2}$path?${param.joinToString(separator = ",")}"
    }

}