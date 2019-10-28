package com.onepiece.treasure.games

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.onepiece.treasure.games.http.OkHttpUtil

fun main() {

    val objectMapper = jacksonObjectMapper()
    val okHttpUtil = OkHttpUtil(objectMapper)

    val v = okHttpUtil.doGet("http://94.237.64.70:8001/api/v1/web/clientBank", null, String::class.java)
    println(v)
}