package com.onepiece.treasure.games

import com.onepiece.treasure.games.joker.JokerGameCashApi
import org.junit.Test
import java.math.BigDecimal
import java.util.*

class JokerGameCashApiTest: BaseTest() {

    private val gameApi = JokerGameCashApi(okHttpUtil)

    @Test
    fun wallet() {
        val balance = gameApi.wallet(username)
        println(balance)
    }

    @Test
    fun clientBalance() {
        val balance = gameApi.clientBalance()
        println(balance)
    }

    @Test
    fun transfer() {
        val result = gameApi.transfer(username = username, money = BigDecimal.valueOf(-97.66), orderId = UUID.randomUUID().toString().replace("-", ""))
        println(result)
    }



}