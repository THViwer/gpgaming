package com.onepiece.treasure.games

import org.apache.commons.codec.digest.DigestUtils
import org.junit.Test

class SboTest {

    @Test
    fun demo() {


        val opCode = "GSA65"
        val agent = "GSA65"

        val username = "Saff001"
        val language = "en"

        val key = "JMNH2DJHY5HKSXSHD64YRPSJU7SHY6"


        val param = "registerplayer/${opCode}/player/${opCode}${username}/agent/${agent}/lang/${language}"

        val sign = DigestUtils.md5Hex("$param/$key")

        val url = "${GameConstant.SBO_API_URL}/sportsfundservice/$param?access=${sign}"


        println(url)
//        val okHttpClient = OkHttpClient()
//
//        val request = Request.Builder()
//                .url(url)
//                .get()
//                .build()
//
//        val response = okHttpClient.newCall(request).execute()

//        println(response.code)
//        println(response.body!!.string())

    }

}