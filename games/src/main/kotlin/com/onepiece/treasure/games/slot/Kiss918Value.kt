package com.onepiece.treasure.games.slot

import com.onepiece.treasure.games.bet.JacksonMapUtil

sealed class Kiss918Value {

    data class Result(

            val msg: String?,

            val success: Boolean

    ): JacksonMapUtil()

}