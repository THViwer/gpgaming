package com.onepiece.treasure.web.controller

import com.onepiece.treasure.games.http.OkHttpUtil
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DemoController(
        private val okHttpUtils: OkHttpUtil
) {

    @GetMapping("/demo")
    fun demo() {
        val url = "http://api688.net:81/?AppID=F1S8&Signature=msNyXDibzSGnBD6PXukqYWRuJdw=&Method=JP&Timestamp=1572416112"
        val str = okHttpUtils.doPost(url, "", String::class.java)
        println(str)
    }

}

