package com.onepiece.gpgaming.games.fishing

import com.onepiece.gpgaming.games.bet.JacksonMapUtil


sealed class GGFishingValue {

    data class Result(
            val status: Int
    ): JacksonMapUtil()


}

