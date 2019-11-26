//package com.onepiece.treasure.games.slot.pussy888
//
//import com.onepiece.treasure.beans.model.token.Pussy888ClientToken
//import com.onepiece.treasure.games.GameConstant
//import org.apache.commons.codec.digest.DigestUtils
//
//class Pussy888Build(
//        private val domain: String,
//        private val path: String,
//        private val time: Long
//) {
//
//    private val param = arrayListOf(
//            "time=$time"
//    )
//
//    companion object {
//        fun instance(domain: String = GameConstant.PUSSY_API_URL, path: String): Pussy888Build {
//            return Pussy888Build(domain = domain, path = path, time = System.currentTimeMillis())
//        }
//    }
//
//    fun set(key: String, value: String): Pussy888Build {
//        param.add("$key=$value")
//        return this
//    }
//
//    private fun sign(beforeParam: String?, username: String, token: Pussy888ClientToken): String {
//        val signStr = "${beforeParam?: ""}${token.autoCode}${username}${time}${token.key}".toLowerCase()
//        return DigestUtils.md5Hex(signStr)
//    }
//
//    fun build(beforeParam: String? = null, token: Pussy888ClientToken, username: String): String {
//        val sign = sign(beforeParam, username, token).toUpperCase()
//        param.add("sign=$sign")
//        param.add("authcode=${token.autoCode}")
//        return "$domain$path?${param.joinToString(separator = "&")}"
//    }
//
//}