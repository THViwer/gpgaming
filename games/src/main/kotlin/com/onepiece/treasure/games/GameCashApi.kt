package com.onepiece.treasure.games

import com.onepiece.treasure.games.joker.value.JokerBalanceResult

interface GameCashApi {

    fun balance(username: String): JokerBalanceResult

    fun transferIn()

    fun transferOut()

}