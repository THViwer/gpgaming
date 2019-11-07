package com.onepiece.treasure.games.slot.kiss918

import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.beans.model.token.Kiss918ClientToken
import org.apache.commons.codec.digest.DigestUtils

class Kiss918Build(
        private val domain: String,
        private val path: String,
        private val time: Long
) {

    private val param = arrayListOf(
            "time=$time"
    )

    companion object {
        fun instance(domain: String = GameConstant.KISS918_API_URL, path: String): Kiss918Build {
            return Kiss918Build(domain = domain, path = path, time = System.currentTimeMillis())
        }
    }

    fun set(key: String, value: String): Kiss918Build {
        param.add("$key=$value")
        return this
    }

    private fun sign(beforeParam: String?, username: String, token: Kiss918ClientToken): String {
        val signStr = "${beforeParam?: ""}${token.autoCode}${username}${time}${token.key}".toLowerCase()
        return DigestUtils.md5Hex(signStr)
    }

    fun build(beforeParam: String? = null, token: Kiss918ClientToken, username: String): String {
        val sign = sign(beforeParam, username, token).toUpperCase()
        param.add("sign=$sign")
        param.add("authcode=${token.autoCode}")
        return "$domain$path?${param.joinToString(separator = "&")}"
    }

}