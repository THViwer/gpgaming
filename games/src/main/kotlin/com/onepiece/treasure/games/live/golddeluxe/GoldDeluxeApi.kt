package com.onepiece.treasure.games.live.golddeluxe

import com.onepiece.treasure.beans.model.token.DefaultClientToken

interface GoldDeluxeApi {

//    fun launch(token: DefaultToken)

    fun createMember(token: DefaultClientToken, username: String): String

}