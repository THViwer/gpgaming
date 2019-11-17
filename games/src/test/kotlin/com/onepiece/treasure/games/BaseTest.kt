package com.onepiece.treasure.games

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.onepiece.treasure.games.http.OkHttpUtil

open class BaseTest {

    val username = "fc51"

    val mapper = jacksonObjectMapper()
//    val okHttpUtil = OkHttpUtil(mapper)

}