package com.onepiece.treasure.games.joker

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.games.joker.value.JokerBalanceResult
import com.onepiece.treasure.games.joker.value.JokerWalletResult
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.HmacUtils
import org.apache.commons.codec.net.URLCodec
import org.springframework.util.Base64Utils
import java.util.*
import kotlin.collections.ArrayList

object JokerConstant {

//    val url = "http://www.joker.com"
    const val url = "http://94.237.64.70:81"

    const val appId = "F1S8"

}

class JokerParamBuilder private constructor(
        val method: String
){

     val data: ArrayList<String> = arrayListOf()

    companion object {
        fun instance(method: String): JokerParamBuilder {
            return JokerParamBuilder(method)
        }
    }

    fun set(k: String, v: String): JokerParamBuilder {
        data.add("$k=$v")
        return this
    }

    fun build(): String {
        val urlParam = data.joinToString(separator = "&").let {
            if (it.isBlank()) "" else "&$it"
        }
        val timestamp = System.currentTimeMillis() / 1000
        val signParam = "Method=$method&Timestamp=$timestamp$urlParam"

        val bytes = HmacUtils.getHmacSha1("qc8y6kbyinc14".toByteArray()).doFinal(signParam.toByteArray())
        val sign = Base64.encodeBase64String(bytes)

        return "AppID=${JokerConstant.appId}&Signature=${sign}&$signParam"
    }

}


fun main() {
    val mapper = jacksonObjectMapper()
    val okHttpUtil = OkHttpUtil(mapper)

    val urlParam = JokerParamBuilder.instance("JP").build()
//    val result = okHttpUtil.doPost(JokerConstant.url, urlParam, String::class.java)
//    println(result)
    println(urlParam)

//\
//     http://api688.net:81/?AppID=F1S8&Signature=msNyXDibzSGnBD6PXukqYWRuJdw=&Method=JP&Timestamp=1572416112

//    curl -X POST -d "AppID=F1S8&Signature=msNyXDibzSGnBD6PXukqYWRuJdw=&Method=JP&Timestamp=1572416112" http://api688.net:81

    val x = String(URLCodec.decodeUrl("AppID=F1S8&Signature=msNyXDibzSGnBD6PXukqYWRuJdw=".toByteArray()))
    println(x)

    // curl -d 'Method=JP&Timestamp=1572416112' http://api688.net:81?AppID=F1S8&Signature=msNyXDibzSGnBD6PXukqYWRuJdw=
    // curl -d 'Method=JP&Timestamp=1572416112' http://api688.net:81?&Signature=msNyXDibzSGnBD6PXukqYWRuJdw=
    // curl -d 'AppID=F1S8&Signature=msNyXDibzSGnBD6PXukqYWRuJdw=Method=JP&Timestamp=1572416112' http://api688.net:81
}