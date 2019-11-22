package com.onepiece.treasure.games.fishing

import com.onepiece.treasure.games.bet.JacksonMapUtil

sealed class GGFishingValue {

    data class Result(
            val status: Int
    ): JacksonMapUtil()


}

