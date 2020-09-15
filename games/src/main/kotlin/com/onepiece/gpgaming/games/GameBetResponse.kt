package com.onepiece.gpgaming.games

import com.onepiece.gpgaming.beans.value.database.BetOrderValue

data class GameBetResponse(

        val path: String,

        val param: String,

        val response: String,

        val data: List<BetOrderValue.BetOrderCo>

)