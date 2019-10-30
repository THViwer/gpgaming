package com.onepiece.treasure.games

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.games.value.SlotGame

interface GameApi {

    fun register(username: String, password: String)

    fun games(): List<SlotGame>

    fun start(username: String, gameId: String, redirectUrl: String): String

    fun start(platform: Platform)

}