package com.onepiece.treasure.games

import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.commons.codec.digest.DigestUtils
import org.junit.Test
import java.time.LocalDate

class Kiss918  {

    val AGENT_CODE = "MAN1006"
    val AUTH_CODE = "MthBAKyqdZRrKEKRuJvy"
    val SECRET_KEY = "j7ZNS4e79Sr7Fa2SX325"
    val API_URL = "http://api.918kiss.com:9991"
    val API_URL2 = "http://api.918kiss.com:9919"

    @Test
    fun demo() {

        val path = "$API_URL2/ashx/AgentMoneyLog.ashx?"

        val time = System.currentTimeMillis()

        val signParam = "$AUTH_CODE$AGENT_CODE$time$SECRET_KEY".toLowerCase()
        val sign = DigestUtils.md5Hex(signParam)

        val param = listOf(
                "userName=$AGENT_CODE",
                "sDate=${LocalDate.now()}",
                "eDate=${LocalDate.now().plusDays(1)}",
                "time=${System.currentTimeMillis()}",
                "authcode=${AUTH_CODE}",
                "sign=$sign"
        ).joinToString(separator = "&")

        val client = OkHttpClient()

        println("$path$param")
        val request = Request.Builder()
                .url("$path$param")
                .get()
                .build()

        val response = client.newCall(request).execute()

        println(response.code)
        println(response.body!!.string())

    }
}