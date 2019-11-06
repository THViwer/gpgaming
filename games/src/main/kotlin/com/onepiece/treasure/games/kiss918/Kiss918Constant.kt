package com.onepiece.treasure.games.kiss918

import org.apache.commons.codec.digest.DigestUtils

object Kiss918Constant {

    const val AGENT_CODE = "MAN1006"
    const val AUTH_CODE = "MthBAKyqdZRrKEKRuJvy"
    const val SECRET_KEY = "j7ZNS4e79Sr7Fa2SX325"
    const val API_URL = "http://api.918kiss.com:9991"
    const val API_ORDER_URL = "http://api.918kiss.com:9919"

}

class Kiss918Builder(
        private val domain: String,
        private val path: String,
        private val time: Long
) {

    private val param = arrayListOf(
            "time=$time",
            "authcode=${Kiss918Constant.AUTH_CODE}"
    )

    companion object {
        fun instance(domain: String = Kiss918Constant.API_URL, path: String): Kiss918Builder {
            return Kiss918Builder(domain = domain, path = path, time = System.currentTimeMillis())
        }
    }

    fun set(key: String, value: String):Kiss918Builder {
        param.add("$key=$value")
        return this
    }

    private fun sign(beforeParam: String?, username: String): String {
        val signStr = "${beforeParam?: ""}${Kiss918Constant.AUTH_CODE}${username}${time}${Kiss918Constant.SECRET_KEY}".toLowerCase()
        return DigestUtils.md5Hex(signStr)
    }

    fun build(beforeParam: String? = null, username: String): String {
        val sign = sign(beforeParam, username).toUpperCase()
        param.add("sign=$sign")
        return "$domain$path?${param.joinToString(separator = "&")}"
    }

}