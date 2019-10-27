package com.onepiece.treasure.games

import com.onepiece.treasure.beans.enums.Platform

interface GameMemberApi {

    fun start(gameId: String)

    fun start(platform: Platform)

}