package com.onepiece.treasure.games

import com.onepiece.treasure.games.joker.JokerGameApi
import org.junit.Test
import java.util.*


class JokerGameApiTest: BaseTest() {

    val gameApi = JokerGameApi(okHttpUtil)

    @Test
    fun register() {
        val username = UUID.randomUUID().toString().substring(0, 4)
        println("username=$username")
        gameApi.register(username, "123456")
    }

    @Test
    fun games(){
        val games = gameApi.games()
        check(games.isNotEmpty())
        println(games)
    }

    @Test
    fun start() {

        val username = "fc51"
        //http://api688.net:81?token=xm6qewpgxtt7c&game=fwria11mjbrwh&redirectUrl=http://abc.com
        val url = gameApi.start(username = username,  gameId = "fwria11mjbrwh", redirectUrl = "http://www.baidu.com")
        println(url)

    }

}